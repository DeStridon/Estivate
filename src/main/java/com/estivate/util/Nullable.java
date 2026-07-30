// package com.estivate.util;


// public final class Nullable<T> {

//     private boolean isNull;
//     private T value;

//     private Nullable(boolean isNull, T value) {
//         this.isNull = isNull;
//         this.value = value;
//     }

//     public static <T> Nullable<T> ofNull() {
//         return new Nullable<>(true, null);
//     }

//     public static <T> Nullable<T> of(T value) {
//         return new Nullable<>(false, value);
//     }

//     public void set(T value) {
//     	if(value == null) {
//     		this.isNull = true;
//     	}
// 		this.value = null;
//     }
    
//     public void setNull() { set(null); }
//     public T get() { return isNull ? null : value; }
// }