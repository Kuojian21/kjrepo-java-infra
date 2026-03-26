package com.kjrepo.infra.common.info;

import java.util.Objects;

public class Tuple4<T1, T2, T3, T4> extends Tuple3<T1, T2, T3> {

	private T4 t4;

	public T4 getT4() {
		return t4;
	}

	public void setT4(T4 t4) {
		this.t4 = t4;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(t4);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple4<T1, T2, T3, T4> other = (Tuple4<T1, T2, T3, T4>) obj;
		return super.equals(obj) && Objects.equals(t4, other.t4);
	}

}
