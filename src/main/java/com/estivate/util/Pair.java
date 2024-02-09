package com.estivate.util;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Objects;

/**
 * Generic pair object.
 * Simple wrapper around 2 objects.
 *
 * @param <X>
 * @param <Y>
 */
public class Pair<X, Y> implements Cloneable {
	public X x; 
	public Y y; 
	public Pair(X x, Y y) { 
		this.x = x; 
		this.y = y; 
	}
  
	public X getX(){
		return x;
	}
	public Y getY(){
		return y;
	}
	public Pair<X, Y> clone() {
		return new Pair<X,Y>(x,y);
	}

	@Override
	public String toString() {
		return "Pair [x=" + x + ", y=" + y + "]";
	}
	
	
	// turns   Pair = { x = en-US, y = fr-FR}   into   ["en-US","fr-FR"] 
	public List<String> toList() {
		List<String> res = new ArrayList<String>();
		res.add(x.toString());
		res.add(y.toString());
		return res;
	}

	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Pair))
			return false;
		
		if(obj == this)
			return true;
		
		Pair<?, ?> pair = (Pair<?, ?>) obj;
		
		boolean isSame = java.util.Objects.equals(pair.x, x) && java.util.Objects.equals(pair.y, y);
		
		return isSame;
	}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(this.x, this.y);
	}
	
	public static<T> Pair<T,T> fromList(List<T> list){
		if(list.size() != 2) {
			return null;
		}
		return new Pair<T,T>(list.get(0), list.get(1));
	}		
}
