package com.estivate.test.reconciliation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.estivate.context.Context;
import com.estivate.reconciliation.ReconciliationManager;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.reconciliation.resolvers.excluded.ExcludedPackageResolver;
import com.estivate.test.reconciliation.resolvers.included.IncludedPackageResolver;

/**
 * Verifies that {@link ReconciliationManager#addResolvers(String...)} only loads
 * classes annotated with {@code @ReconciliationScope} from the cited packages.
 */
public class ResolverPackageScanTest {

	Context context = DatabaseGenerator.getContext();

	@Test
	public void addResolvers_onlyLoadsResolversFromCitedPackage() {
		ReconciliationManager manager = new ReconciliationManager(context)
			.addResolvers("com.estivate.test.reconciliation.resolvers.included");

		List<Class<?>> resolverClasses = manager.getResolvers().stream()
			.map(Object::getClass)
			.collect(Collectors.toList());

		assertTrue(resolverClasses.contains(IncludedPackageResolver.class), "Resolver from the cited package should be loaded");
		assertFalse(resolverClasses.contains(ExcludedPackageResolver.class), "Resolver from a different package must not be loaded");

		for (Class<?> resolverClass : resolverClasses) {
			assertTrue(resolverClass.getName().startsWith("com.estivate.test.reconciliation.resolvers.included"),"Unexpected resolver outside cited package: " + resolverClass.getName());
		}

		assertEquals(1, resolverClasses.size(),"Exactly one resolver should be loaded from the cited package, got: " + resolverClasses);
	}
}
