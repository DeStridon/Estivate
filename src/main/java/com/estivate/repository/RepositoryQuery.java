package com.estivate.repository;

import java.io.Serializable;
import java.lang.invoke.MethodType;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.Estivate;
import com.estivate.query.Query.Order;
import com.estivate.query.SelectQuery;
import com.estivate.util.FieldUtils;
import com.estivate.util.Pair;

/**
 * Parsed representation of a repository query method (name → criterions, orders, projection).
 *
 * <pre>{@code
 * SelectQuery<?> query = RepositoryQuery.getQueryFromMethod(CustomerManager::findById, 42L);
 * }</pre>
 */
public class RepositoryQuery {

	@FunctionalInterface
	public interface Fn0<R extends Repository<?>> extends Serializable { Object call(R repository); }

	@FunctionalInterface
	public interface Fn1<R extends Repository<?>, A> extends Serializable { Object call(R repository, A a); }

	@FunctionalInterface
	public interface Fn2<R extends Repository<?>, A, B> extends Serializable { Object call(R repository, A a, B b); }

	@FunctionalInterface
	public interface Fn3<R extends Repository<?>, A, B, C> extends Serializable { Object call(R repository, A a, B b, C c); }

	public static <R extends Repository<?>> SelectQuery<?> getQueryFromMethod(Fn0<R> methodRef) {
		return toQuery(methodRef);
	}

	public static <R extends Repository<?>, A> SelectQuery<?> getQueryFromMethod(Fn1<R, A> methodRef, A a) {
		return toQuery(methodRef, a);
	}

	public static <R extends Repository<?>, A, B> SelectQuery<?> getQueryFromMethod(Fn2<R, A, B> methodRef, A a, B b) {
		return toQuery(methodRef, a, b);
	}

	public static <R extends Repository<?>, A, B, C> SelectQuery<?> getQueryFromMethod(Fn3<R, A, B, C> methodRef, A a, B b, C c) {
		return toQuery(methodRef, a, b, c);
	}

	/** Parses a reflective repository method and resolves its return shape. */
	public static RepositoryQuery parse(Method method) throws Exception {
		Class<?> declaringClass = method.getDeclaringClass();
		if (!Repository.class.isAssignableFrom(declaringClass)) {
			throw new Exception("Method " + method + " is not declared on a Repository subclass");
		}
		@SuppressWarnings("unchecked")
		Class<? extends Repository<?>> repositoryClass = (Class<? extends Repository<?>>) declaringClass;
		RepositoryQuery query = new RepositoryQuery(repositoryClass, method.getName());
		query.resolveReturnType(method);
		return query;
	}

	private static SelectQuery<?> toQuery(Serializable methodRef, Object... args) {
		try {
			return parse(methodOf(methodRef)).toSelectQuery(args);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot build SelectQuery from method reference", e);
		}
	}

	private static Method methodOf(Serializable methodRef) throws Exception {
		Method writeReplace = methodRef.getClass().getDeclaredMethod("writeReplace");
		writeReplace.setAccessible(true);
		SerializedLambda lambda = (SerializedLambda) writeReplace.invoke(methodRef);

		Class<?> repositoryClass = Class.forName(lambda.getImplClass().replace('/', '.'));
		Class<?>[] parameterTypes = MethodType
			.fromMethodDescriptorString(lambda.getImplMethodSignature(), repositoryClass.getClassLoader())
			.parameterArray();

		return repositoryClass.getDeclaredMethod(lambda.getImplMethodName(), parameterTypes);
	}

    public final String methodName;
    public Class<?> entityClass;
    
    public List<Pair<Method, String>> criterions = new ArrayList<>();
    public List<Pair<String, Order.Direction>> orders = new ArrayList<>();
    /** When set, project this field: findDistinctName... (distinct list) or findNameBy... (single value). */
    public String selectField;
    
    // Return shape and element/scalar type
    public Class<?> returnEntity;
    public ReturnType returnType;
    
    List<String> fieldList;

    public int position;

    public RepositoryQuery(Class<?> repositoryClass, String methodName) throws Exception {
        // Basic info
        this.methodName = methodName;
        this.position = 0;

        // Fill entityClass
        Type genericSuperclass = repositoryClass.getGenericSuperclass();
        Type entityType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        this.entityClass = (Class<?>) entityType;

        List<String> criterionList = getMethods().stream()
            .sorted((a, b) -> Integer.compare(b.length(), a.length()))
            .collect(Collectors.toList());

        fieldList = FieldUtils.getEntityFields(entityClass).stream()
            .map(Member::getName)
            .sorted((a, b) -> Integer.compare(b.length(), a.length()))
            .collect(Collectors.toList());

        if (methodName.equals("findAll")) {
            setReturn(entityClass, ReturnType.List);
            return;
        }

        if (consume("findAllBy")) {
            setReturn(entityClass, ReturnType.List);
        }
        else if (consume("findOneBy")) {
            setReturn(entityClass, ReturnType.Entity);
        }
        else if (consume("findDistinct")) {
            selectField = consumeField();
            setReturn(fieldType(selectField), ReturnType.DistinctList);
            if (done()) {
                return;
            }
            if (!consume("By")) {
                throw expected("By");
            }
            if (done()) {
                return;
            }
        }
        else if (consume("countBy")) {
            setReturn(Long.class, ReturnType.Count);
        }
        else if (consume("existsBy")) {
            setReturn(boolean.class, ReturnType.Count);
        }
        else if (consume("findBy")) {
            setReturn(entityClass, null);
        }
        else if (consume("find")) {
            selectField = consumeField();
            setReturn(fieldType(selectField), ReturnType.Entity);
            if (done() || !consume("By")) {
                throw expected("By");
            }
            if (done()) {
                return;
            }
        }
        else {
            throw new Exception("Unsupported method name: " + methodName);
        }

        while (!done()) {
            String field = consumeField();
            String criterion = consumeFirst(criterionList);

            List<Method> criterionMethods = Arrays.asList(SelectQuery.class.getMethods()).stream()
                .filter(x -> x.getParameterCount() > 0 && x.getParameterTypes()[0].equals(String.class))
                .collect(Collectors.toList());

            Method criterionMethod = criterionMethods.stream()
                .filter(x -> x.getName().equalsIgnoreCase(criterion.isEmpty() ? "eq" : criterion))
                .findFirst()
                .orElseThrow(() -> new Exception("No criterion method found, available methods: " + criterionList.stream().collect(Collectors.joining(", "))));

            criterions.add(new Pair<>(criterionMethod, field));

            if (!done()) {
                if (consume("And")) {
                    // next criterion
                }
                else if (consume("OrderBy")) {
                    while (!done()) {
                        String orderField = consumeField();

                        if (consume("Asc")) {
                            orders.add(new Pair<>(orderField, Order.Direction.Asc));
                        }
                        else if (consume("Desc")) {
                            orders.add(new Pair<>(orderField, Order.Direction.Desc));
                        }
                        else {
                            orders.add(new Pair<>(orderField, Order.Direction.Asc));
                        }
                    }
                }
                else {
                    throw expected("And");
                }
            }
        }
    }

    private void setReturn(Class<?> returnEntity, ReturnType returnType) {
        this.returnEntity = returnEntity;
        this.returnType = returnType;
    }

    private Class<?> fieldType(String fieldName) {
        java.lang.reflect.Field field = FieldUtils.findField(entityClass, fieldName);
        return field != null ? field.getType() : Object.class;
    }

    /**
     * If {@link #methodName} starts with {@code token} at {@link #position} (ignore case),
     * advances the cursor by {@code token.length()} and returns true.
     */
    boolean consume(String token) {
        if (!startsWithIgnoreCase(methodName, token, position)) {
            return false;
        }
        position += token.length();
        return true;
    }

    /**
     * Resolves an entity field at {@link #position} and advances the cursor by its length.
     */
    String consumeField() throws Exception {
        String field = getFieldName();
        position += field.length();
        return field;
    }

    /**
     * Consumes the first matching token from {@code tokens} (already longest-first), or returns "".
     */
    String consumeFirst(List<String> tokens) {
        for (String token : tokens) {
            if (consume(token)) {
                return token;
            }
        }
        return "";
    }

    boolean done() {
        return position >= methodName.length();
    }

    private Exception expected(String expected) {
        return new Exception(String.format(
            "Expected '%s' at position %d but found '%s'",
            expected,
            position,
            done() ? "" : methodName.substring(position, Math.min(position + 10, methodName.length()))
        ));
    }

    /**
     * Builds the {@link SelectQuery} corresponding to this parsed repository method.
     */
    public SelectQuery<?> toSelectQuery(Object... args) throws IllegalAccessException, InvocationTargetException {
        int argCounter = 0;
        SelectQuery<?> query = Estivate.selectQuery(entityClass);
        for (Pair<Method, String> criterion : criterions) {
            Method m = criterion.getX();
            String name = m.getName();
            if (name.startsWith("between")) {
                m.invoke(query, criterion.getY(), args[argCounter], args[argCounter + 1]);
                argCounter += 2;
            } else if (name.equals("isNull") || name.equals("isNotNull") || name.equals("isTrue") || name.equals("isFalse")) {
                m.invoke(query, criterion.getY());
            } else {
                m.invoke(query, criterion.getY(), args[argCounter]);
                argCounter++;
            }
        }
        for (Pair<String, Order.Direction> order : orders) {
            query.orderBy(order.getX(), order.getY());
        }
        return query;
    }

    /**
     * Resolves {@link #returnType} from the reflective method when the method name left it ambiguous
     * (typically {@code findBy…}).
     */
    public void resolveReturnType(Method method) throws Exception {
        if (returnType != null) {
            return;
        }
        if (List.class.equals(method.getReturnType())) {
            returnType = ReturnType.List;
            return;
        }
        if (method.getReturnType().equals(returnEntity)) {
            returnType = ReturnType.Entity;
            return;
        }
        throw new Exception("Unsupported return type: " + method.getReturnType());
    }

    public static List<String> getMethods() {
        List<String> result = new ArrayList<>();

        result.addAll(new StringComposer().compose("in", "notIn").compose("", "IfNotEmpty", "IfNotEmptyNullable", "OrNull", "IfNotEmptyOrNull", "OrFalseIfEmpty").results);
        result.addAll(new StringComposer().compose("eq", "notEq", "gt", "gte", "lt", "lte", "between").compose("", "IfNotNull", "OrNull", "Nullable").results);
        result.addAll(new StringComposer().compose("like", "notLike").compose("", "StartsWith", "EndsWith", "Contains").compose("", "IfNotNull", "In", "InIfNotEmpty").results);
        result.addAll(Arrays.asList("isNull", "isNotNull", "isTrue", "isFalse"));
        return result;
    }

    /** Peeks at the entity field at {@link #position} without advancing the cursor. */
    public String getFieldName() throws Exception {
        String field = fieldList.stream()
            .filter(f -> startsWithIgnoreCase(methodName, f, position))
            .findFirst()
            .orElse(null);

        if (field == null) {
            String remaining = methodName.substring(position);
            throw new Exception(String.format(
                "Unknown field at position %d: '%s'. Available fields: %s",
                position,
                remaining.length() > 20 ? remaining.substring(0, 20) + "..." : remaining,
                fieldList.stream().map(f -> f.substring(0, 1).toLowerCase() + f.substring(1))
                    .collect(Collectors.joining(", "))
            ));
        }
        return field;
    }

    private boolean startsWithIgnoreCase(String str, String prefix, int position) {
        return str.toLowerCase().startsWith(prefix.toLowerCase(), position);
    }

    static class StringComposer {
        public List<String> results = new ArrayList<>(Arrays.asList(""));

        public StringComposer compose(String... terms) {
            List<String> newResult = new ArrayList<>();
            for (String oldLine : results) {
                for (String term : terms) {
                    newResult.add(oldLine + term);
                }
            }
            results = newResult;
            return this;
        }
    }
}
