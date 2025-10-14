package com.kjrepo.infra.common.info;

import java.util.Objects;

public class Tuple<T1, T2> {

	public static <T1, T2> Tuple<T1, T2> tuple(T1 t1, T2 t2) {
		Tuple<T1, T2> tuple = new Tuple<>();
		tuple.setT1(t1);
		tuple.setT2(t2);
		return tuple;
	}

	public static <T1, T2, T3> Tuple3<T1, T2, T3> tuple(T1 t1, T2 t2, T3 t3) {
		Tuple3<T1, T2, T3> tuple = new Tuple3<>();
		tuple.setT1(t1);
		tuple.setT2(t2);
		tuple.setT3(t3);
		return tuple;
	}

	public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> tuple(T1 t1, T2 t2, T3 t3, T4 t4) {
		Tuple4<T1, T2, T3, T4> tuple = new Tuple4<>();
		tuple.setT1(t1);
		tuple.setT2(t2);
		tuple.setT3(t3);
		tuple.setT4(t4);
		return tuple;
	}

	public static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> tuple(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
		Tuple5<T1, T2, T3, T4, T5> tuple = new Tuple5<>();
		tuple.setT1(t1);
		tuple.setT2(t2);
		tuple.setT3(t3);
		tuple.setT4(t4);
		tuple.setT5(t5);
		return tuple;
	}

	public static <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> tuple(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5,
			T6 t6) {
		Tuple6<T1, T2, T3, T4, T5, T6> tuple = new Tuple6<>();
		tuple.setT1(t1);
		tuple.setT2(t2);
		tuple.setT3(t3);
		tuple.setT4(t4);
		tuple.setT5(t5);
		tuple.setT6(t6);
		return tuple;
	}

	private T1 t1;
	private T2 t2;

	public T1 getT1() {
		return t1;
	}

	public void setT1(T1 t1) {
		this.t1 = t1;
	}

	public T2 getT2() {
		return t2;
	}

	public void setT2(T2 t2) {
		this.t2 = t2;
	}

	@Override
	public int hashCode() {
		return Objects.hash(t1, t2);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple<T1, T2> other = (Tuple<T1, T2>) obj;
		return Objects.equals(t1, other.t1) && Objects.equals(t2, other.t2);
	}
}
