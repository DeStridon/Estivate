package com.estivate.manager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.Estivate;
import com.estivate.manager.ManagerInterceptor.StringComposer;
import com.estivate.query.Query.Order;
import com.estivate.query.SelectQuery;
import com.estivate.util.FieldUtils;
import com.estivate.util.Pair;

import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;

public class ManagerQueryWrapper {

    public Class<?> entityClass;
    public List<Pair<Method, String>> criterions = new ArrayList<>();
    public List<Pair<String, Order.Direction>> orders = new ArrayList<>();
    List<String> fieldList;


    public ManagerQueryWrapper(Class<?> managerClass, String methodName) throws Exception {

        Type genericSuperclass = managerClass.getGenericSuperclass();
        Type entityType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        this.entityClass = (Class<?>) entityType;

        

        List<String> criterionList = getMethods().stream()
            .sorted((a, b) -> Integer.compare(b.length(), a.length())) // Sort by length descending
            .collect(Collectors.toList());

        fieldList = FieldUtils.getEntityFields(entityClass).stream()
    		.map(Member::getName)
    		.sorted((a, b) -> Integer.compare(b.length(), a.length())) 
            .collect(Collectors.toList());

        if (methodName.equals("findAll")) {
            return;
        }

        int position = 0;
        if (startsWithIgnoreCase(methodName, "findAllBy", position)) {
            position = 9;
        }
        else if(startsWithIgnoreCase(methodName, "findOneBy", position)){
            position = 9;
        }
        else if(startsWithIgnoreCase(methodName, "countBy", position)){
            position = 7;
        }
        else if(startsWithIgnoreCase(methodName, "existsBy", position)){
            position = 8;
        }
        else if(startsWithIgnoreCase(methodName, "findBy", position)){
            position = 6;
        }
        else{
            throw new Exception("Unsupported method name: " + methodName);
        }

        while (position < methodName.length()) {

            // Match field
            String field = getFieldName(methodName, position);
            position += field.length();
            
            // Match criterion (optional - defaults to 'eq')
            final int criterionPos = position;
            String criterion = criterionList.stream()
                .filter(c -> startsWithIgnoreCase(methodName, c, criterionPos))
                .findFirst()
                .orElse("");
            
            position += criterion.length();
            
            // Count expected arguments based on criterion
            
            List<Method> criterionMethods = Arrays.asList(SelectQuery.class.getMethods()).stream()
            .filter(x -> x.getParameterCount() > 0 && x.getParameterTypes()[0].equals(String.class))
            .collect(Collectors.toList());

            Method criterionMethod = criterionMethods.stream()
            .filter(x -> x.getName().equalsIgnoreCase(criterion.isEmpty() ? "eq" : criterion))
            .findFirst().orElseThrow(() -> new Exception("No criterion method found, available methods: " + criterionList.stream().collect(Collectors.joining(", "))));
            
            criterions.add(new Pair<>(criterionMethod, field));



            // Check for 'And' separator
            if (position < methodName.length()) {
                if (methodName.startsWith("And", position)) {
                    position += 3;
                }
                else if(methodName.startsWith("OrderBy", position)){
                    position += 7;
                    while(position < methodName.length()){
                        String orderField = getFieldName(methodName, position);
                        position += orderField.length();
                        
                        if(methodName.startsWith("Asc", position)){
                            position += 3;
                            orders.add(new Pair<>(orderField, Order.Direction.Asc));
                        }
                        else if(methodName.startsWith("Desc", position)){
                            position += 4;
                            orders.add(new Pair<>(orderField, Order.Direction.Desc));
                        }
                        else{
                            orders.add(new Pair<>(orderField, Order.Direction.Asc));
                        }
                    }
                }

                else{
                    throw new Exception(String.format(
                        "Expected 'And' at position %d but found '%s'",
                        position,
                        methodName.substring(position, Math.min(position + 10, methodName.length()))
                    ));
                }
                
            }
        }

        

    }

    public SelectQuery<?> getQuery(Object[] args) throws IllegalAccessException, InvocationTargetException{
        int argCounter = 0;
        SelectQuery<?> query = Estivate.selectQuery(entityClass);
        for(Pair<Method, String> criterion : criterions){
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
        for(Pair<String, Order.Direction> order : orders){
            query.orderBy(order.getX(), order.getY());
        }
        return query;
    }

    public static List<String> getMethods(){
        List<String> result = new ArrayList<>();

        result.addAll(new StringComposer().compose("in", "notIn").compose("", "IfNotEmpty", "IfNotEmptyNullable", "OrNull", "IfNotEmptyOrNull", "OrFalseIfEmpty").results);
        result.addAll(new StringComposer().compose("eq", "notEq", "gt", "gte", "lt", "lte", "between").compose("", "IfNotNull", "OrNull", "Nullable").results);
        result.addAll(new StringComposer().compose("like", "notLike").compose("", "StartsWith", "EndsWith", "Contains").compose("", "IfNotNull", "In", "InIfNotEmpty").results);
        result.addAll(Arrays.asList("isNull", "isNotNull", "isTrue", "isFalse"));
        return result;
    }

    public String getFieldName(String methodName, int position) throws Exception {
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

    

}
