package com.estivate.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PrePersist;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.context.MySQLContext;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.PersistBenchEntity;
import com.estivate.test.query.mysql.MysqlTestDatabase;
import com.estivate.util.Chronometer;
import com.estivate.util.ClassMeta;

/**
 * Same persist cost comparison as {@link PersistCostComparisonTest}, against real MySQL 8
 * (Testcontainers). Skipped when Docker is unavailable.
 */
@Testcontainers(disabledWithoutDocker = true)
public class PersistCostComparisonMysqlTest {

	private static final int WARMUP = 50;
	private static final int ITERATIONS = 300;
	private static final int BATCH = 200;

	private static final String INSERT_SQL = "INSERT INTO PERSISTBENCHENTITY (NAME, EMAIL, SCORE, ACTIVE, NOTE) VALUES (?, ?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE PERSISTBENCHENTITY SET NAME = ?, EMAIL = ?, SCORE = ?, ACTIVE = ?, NOTE = ? WHERE ID = ?";

	@Container
	static MySQLContainer<?> mysql = MysqlTestDatabase.newContainer();

	private static DataSource dataSource;
	private static Context context;
	private static SessionFactory sessionFactory;

	@BeforeAll
	public static void setUp() {
		dataSource = MysqlTestDatabase.dataSource(mysql);
		context = new MySQLContext(dataSource);
		context.nameMapper = new TestData.TestNameMapper();

		Estivate.Tools.createTableFullQuery(PersistBenchEntity.class, context)
			.ifNotExists()
			.execute(context);

		StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
			.applySetting(AvailableSettings.DATASOURCE, dataSource)
			.applySetting(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQL8Dialect")
			.applySetting(AvailableSettings.HBM2DDL_AUTO, "none")
			.applySetting(AvailableSettings.SHOW_SQL, "false")
			.build();
		sessionFactory = new MetadataSources(registry)
			.addAnnotatedClass(PersistBenchEntity.class)
			.buildMetadata()
			.buildSessionFactory();
	}

	@AfterAll
	public static void tearDown() {
		if(sessionFactory != null) {
			sessionFactory.close();
		}
	}

	@Test
	public void comparesInsertAndUpdateCost() throws Exception {
		timeInserts(WARMUP, 1, this::estivateInsert);
		timeInserts(WARMUP, 1, this::jdbcInsertPerCall);
		timeReusedJdbcInserts(WARMUP, 1);
		timeInserts(WARMUP, 1, this::hibernateInsertPerCall);
		estivateBatch();
		estivateCollectionBatch();
		hibernateBatch();
		jdbcBatch();

		long estivateInsert = 0;
		long estivateInsertSharedConnection = 0;
		long jdbcPerCallInsert = 0;
		long jdbcReusedInsert = 0;
		long hibernatePerCallInsert = 0;
		long estivateBatch = 0;
		long estivateCollectionBatch = 0;
		long hibernateBatch = 0;
		long jdbcBatch = 0;
		long estivateUpdate = 0;
		long jdbcPerCallUpdate = 0;
		long hibernatePerCallUpdate = 0;
		List<PersistBenchEntity> estivateRows = null;

		// Two passes. The first pays for JIT; the printed numbers are the second.
		for(int pass = 0; pass < 2; pass++) {
			estivateInsert = timeInserts(0, ITERATIONS, this::estivateInsert);
			estivateInsertSharedConnection = timeEstivateInsertsWithSharedConnection(0, ITERATIONS);
			jdbcPerCallInsert = timeInserts(0, ITERATIONS, this::jdbcInsertPerCall);
			jdbcReusedInsert = timeReusedJdbcInserts(0, ITERATIONS);
			hibernatePerCallInsert = timeInserts(0, ITERATIONS, this::hibernateInsertPerCall);
			estivateBatch = timeBatch(this::estivateBatch);
			estivateCollectionBatch = timeBatch(this::estivateCollectionBatch);
			hibernateBatch = timeBatch(this::hibernateBatch);
			jdbcBatch = timeBatch(this::jdbcBatch);

			estivateRows = insertRows(ITERATIONS, this::estivateInsert);
			for(PersistBenchEntity row : estivateRows) {
				row.saveState();
			}
			List<PersistBenchEntity> jdbcRows = insertRows(ITERATIONS, this::jdbcInsertPerCall);
			List<PersistBenchEntity> hibernateRows = insertRows(ITERATIONS, this::hibernateInsertPerCall);
			timeUpdates(estivateRows, this::estivateUpdate);
			timeUpdates(jdbcRows, this::jdbcUpdatePerCall);
			timeUpdates(hibernateRows, this::hibernateUpdatePerCall);
			estivateUpdate = timeUpdates(estivateRows, this::estivateUpdate);
			jdbcPerCallUpdate = timeUpdates(jdbcRows, this::jdbcUpdatePerCall);
			hibernatePerCallUpdate = timeUpdates(hibernateRows, this::hibernateUpdatePerCall);
		}

		long methodScanNanos = timeNanos(20_000, () -> ClassMeta.findMethodWithAnnotation(CustomerEntity.class, PrePersist.class));

		System.out.println("Persist cost (MySQL 8 / Testcontainers), microseconds per row");
		System.out.println("  insert  estivate=" + micros(estivateInsert) + " estivate-shared-connection=" + micros(estivateInsertSharedConnection) + " jdbc-per-call=" + micros(jdbcPerCallInsert) + " jdbc-reused-statement=" + micros(jdbcReusedInsert) + " hibernate-per-call=" + micros(hibernatePerCallInsert));
		System.out.println("  update  estivate=" + micros(estivateUpdate) + " jdbc-per-call=" + micros(jdbcPerCallUpdate) + " hibernate-per-call=" + micros(hibernatePerCallUpdate));
		System.out.println("  batch of " + BATCH + " rows, milliseconds: estivate-loop=" + (estivateBatch / 1_000_000) + " estivate-collection=" + (estivateCollectionBatch / 1_000_000) + " hibernate-one-tx=" + (hibernateBatch / 1_000_000) + " jdbc-batch=" + (jdbcBatch / 1_000_000));
		System.out.println("  uncached lifecycle method scan on CustomerEntity: " + (methodScanNanos / 20_000) + " ns");
		System.out.println(profileInsert(100));
		System.out.println(profileUpdate(100));

		Assertions.assertTrue(estivateRows.get(0).id > 0);
		Assertions.assertEquals(ITERATIONS, estivateRows.size());
		Assertions.assertEquals("timed", noteOf(estivateRows.get(0).id));
		Assertions.assertTrue(count() > ITERATIONS);
	}

	private long timeInserts(int warmup, int iterations, Inserter inserter) {
		for(int i = 0; i < warmup; i++) {
			inserter.insert(row(i));
		}
		long begin = System.nanoTime();
		for(int i = 0; i < iterations; i++) {
			inserter.insert(row(i));
		}
		return (System.nanoTime() - begin) / iterations;
	}

	private long timeReusedJdbcInserts(int warmup, int iterations) throws Exception {
		try(Connection connection = dataSource.getConnection()) {
			connection.setAutoCommit(true);
			try(PreparedStatement statement = connection.prepareStatement(INSERT_SQL, java.sql.Statement.RETURN_GENERATED_KEYS)) {
				for(int i = 0; i < warmup; i++) {
					bindInsert(statement, row(i));
					statement.executeUpdate();
					statement.getGeneratedKeys().close();
				}
				long begin = System.nanoTime();
				for(int i = 0; i < iterations; i++) {
					bindInsert(statement, row(i));
					statement.executeUpdate();
					statement.getGeneratedKeys().close();
				}
				return (System.nanoTime() - begin) / iterations;
			}
		}
	}

	private long timeUpdates(List<PersistBenchEntity> rows, Updater updater) {
		for(PersistBenchEntity row : rows) {
			row.note = "warm";
			updater.update(row);
			row.note = "timed";
		}
		long begin = System.nanoTime();
		for(PersistBenchEntity row : rows) {
			updater.update(row);
		}
		return (System.nanoTime() - begin) / rows.size();
	}

	private long timeBatch(Runnable batch) {
		batch.run();
		long begin = System.nanoTime();
		batch.run();
		return System.nanoTime() - begin;
	}

	private long timeNanos(int iterations, Runnable action) {
		for(int i = 0; i < 1000; i++) {
			action.run();
		}
		long begin = System.nanoTime();
		for(int i = 0; i < iterations; i++) {
			action.run();
		}
		return System.nanoTime() - begin;
	}

	private List<PersistBenchEntity> insertRows(int count, Inserter inserter) {
		List<PersistBenchEntity> rows = new ArrayList<>();
		for(int i = 0; i < count; i++) {
			PersistBenchEntity row = row(i);
			inserter.insert(row);
			rows.add(row);
		}
		return rows;
	}

	private void estivateInsert(PersistBenchEntity row) {
		context.insert(row);
	}

	private String profileInsert(int iterations) {
		Chronometer chronometer = new Chronometer("insert-profile-mysql", true).timeThreshold(Integer.MAX_VALUE);
		for(int i = 0; i < 20; i++) {
			context.insert(row(i), chronometer);
		}
		chronometer = new Chronometer("insert-profile-mysql", true).timeThreshold(Integer.MAX_VALUE);
		for(int i = 0; i < iterations; i++) {
			context.insert(row(i), chronometer);
		}
		return chronometer.getLog();
	}

	private String profileUpdate(int iterations) {
		List<PersistBenchEntity> rows = insertRows(iterations, this::estivateInsert);
		for(PersistBenchEntity row : rows) {
			row.saveState();
			row.note = "profile";
		}
		Chronometer warm = new Chronometer("update-profile-mysql-warm", true).timeThreshold(Integer.MAX_VALUE);
		for(PersistBenchEntity row : rows) {
			context.update(row, warm);
			row.saveState();
			row.note = "profiled";
		}
		Chronometer chronometer = new Chronometer("update-profile-mysql", true).timeThreshold(Integer.MAX_VALUE);
		for(PersistBenchEntity row : rows) {
			context.update(row, chronometer);
		}
		return chronometer.getLog();
	}

	private long timeEstivateInsertsWithSharedConnection(int warmup, int iterations) {
		context.withConnection(ignored -> {
			for(int i = 0; i < warmup; i++) {
				context.insert(row(i));
			}
		});
		long begin = System.nanoTime();
		context.withConnection(ignored -> {
			for(int i = 0; i < iterations; i++) {
				context.insert(row(i));
			}
		});
		return (System.nanoTime() - begin) / iterations;
	}

	private void estivateUpdate(PersistBenchEntity row) {
		context.update(row);
	}

	private void estivateBatch() {
		for(int i = 0; i < BATCH; i++) {
			context.insert(row(i));
		}
	}

	private void estivateCollectionBatch() {
		List<PersistBenchEntity> rows = new ArrayList<>();
		for(int i = 0; i < BATCH; i++) {
			rows.add(row(i));
		}
		context.insert(rows);
	}

	private void jdbcInsertPerCall(PersistBenchEntity row) {
		try(Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(INSERT_SQL, java.sql.Statement.RETURN_GENERATED_KEYS)) {
			bindInsert(statement, row);
			statement.executeUpdate();
			try(ResultSet keys = statement.getGeneratedKeys()) {
				if(keys.next()) {
					row.id = keys.getLong(1);
				}
			}
		}
		catch(Exception exception) {
			throw new IllegalStateException(exception);
		}
	}

	private void jdbcUpdatePerCall(PersistBenchEntity row) {
		try(Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
			bindUpdate(statement, row);
			statement.executeUpdate();
		}
		catch(Exception exception) {
			throw new IllegalStateException(exception);
		}
	}

	private void jdbcBatch() {
		try(Connection connection = dataSource.getConnection()) {
			connection.setAutoCommit(false);
			try(PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
				for(int i = 0; i < BATCH; i++) {
					bindInsert(statement, row(i));
					statement.addBatch();
				}
				statement.executeBatch();
			}
			connection.commit();
		}
		catch(Exception exception) {
			throw new IllegalStateException(exception);
		}
	}

	private void hibernateInsertPerCall(PersistBenchEntity row) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		session.persist(row);
		session.flush();
		transaction.commit();
		session.close();
	}

	private void hibernateUpdatePerCall(PersistBenchEntity row) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		session.update(row);
		session.flush();
		transaction.commit();
		session.close();
	}

	private void hibernateBatch() {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		for(int i = 0; i < BATCH; i++) {
			session.persist(row(i));
		}
		session.flush();
		transaction.commit();
		session.close();
	}

	private void bindInsert(PreparedStatement statement, PersistBenchEntity row) throws Exception {
		statement.setString(1, row.name);
		statement.setString(2, row.email);
		statement.setInt(3, row.score);
		statement.setBoolean(4, row.active);
		statement.setString(5, row.note);
	}

	private void bindUpdate(PreparedStatement statement, PersistBenchEntity row) throws Exception {
		statement.setString(1, row.name);
		statement.setString(2, row.email);
		statement.setInt(3, row.score);
		statement.setBoolean(4, row.active);
		statement.setString(5, row.note);
		statement.setLong(6, row.id);
	}

	private PersistBenchEntity row(int index) {
		PersistBenchEntity row = new PersistBenchEntity();
		row.name = "Row " + index;
		row.email = "row" + index + "@example.test";
		row.score = index;
		row.active = true;
		row.note = "note " + index;
		return row;
	}

	private String noteOf(long id) throws Exception {
		try(Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement("SELECT NOTE FROM PERSISTBENCHENTITY WHERE ID = ?")) {
			statement.setLong(1, id);
			try(ResultSet resultSet = statement.executeQuery()) {
				resultSet.next();
				return resultSet.getString(1);
			}
		}
	}

	private long count() throws Exception {
		try(Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM PERSISTBENCHENTITY");
				ResultSet resultSet = statement.executeQuery()) {
			resultSet.next();
			return resultSet.getLong(1);
		}
	}

	private static long micros(long nanosPerOp) {
		return nanosPerOp / 1_000L;
	}

	private interface Inserter {
		void insert(PersistBenchEntity row);
	}

	private interface Updater {
		void update(PersistBenchEntity row);
	}
}
