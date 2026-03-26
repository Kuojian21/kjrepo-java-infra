package com.kjrepo.infra.common.info;

import java.util.Objects;

public class Tuple6<T1, T2, T3, T4, T5, T6> extends Tuple5<T1, T2, T3, T4, T5> {

	private T6 t6;

	public T6 getT6() {
		return t6;
	}

	public void setT6(T6 t6) {
		this.t6 = t6;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(t6);
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
		Tuple6<T1, T2, T3, T4, T5, T6> other = (Tuple6<T1, T2, T3, T4, T5, T6>) obj;
		return super.equals(obj) && Objects.equals(t6, other.t6);
	}

}
