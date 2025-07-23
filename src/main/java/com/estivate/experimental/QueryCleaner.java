package com.estivate.experimental;

import java.util.ArrayList;
import java.util.List;

import com.estivate.query.Aggregator;
import com.estivate.query.Attribute;
import com.estivate.query.Criterion;
import com.estivate.query.EstivateNode;
import com.estivate.query.Join;
import com.estivate.query.Join.JoinType;
import com.estivate.query.Query;

public class QueryCleaner {

    public static void cleanUnusedJoins(Query<?> query){

        List<Attribute> whereAttributes = listNodeAttributes(query);
        List<Attribute> havingAttributes = listNodeAttributes(query.getHaving());
            
        for(Join join : query.getJoins()){
            // if join is inner or right, it is used
            if(join.joinType == JoinType.INNER || join.joinType == JoinType.RIGHT){
                continue;
            }

            // if any field of joined entity in select, it is used
            if(query.getSelects().stream().anyMatch(select -> select.getEntity().equals(join.rightEntity))){
                continue;
            }

            // if any field of joined entity in where, it is used
            if(whereAttributes.stream().anyMatch(attribute -> attribute.getEntity().equals(join.rightEntity))){
                continue;
            }
            
            // if any field of joined entity in group by, it is used
            if(query.getGroupBys().stream().anyMatch(groupBy -> groupBy.entity.equals(join.rightEntity))){
                continue;
            }
            
            // if any field of joined entity in having, it is used
            if(havingAttributes.stream().anyMatch(attribute -> attribute.getEntity().equals(join.rightEntity))){
                continue;
            }

            // if any field of joined entity in order by, it is used
            if(query.getOrders().stream().anyMatch(order -> order.entity.equals(join.rightEntity))){
                continue;
            }

            // remove join
            query.getJoins().remove(join);
        }
 
    }


    public static List<Attribute> listNodeAttributes(EstivateNode node){

        List<Attribute> attributes = new ArrayList<>();

        if(node instanceof Aggregator) {
            for(EstivateNode criterion : ((Aggregator)node).getCriterions()) {
                attributes.addAll(listNodeAttributes(criterion));
        
            }
        }
        else if(node instanceof Criterion) {
            attributes.add((Attribute)(Criterion)node);
        }

        return attributes;
    
    }

}
