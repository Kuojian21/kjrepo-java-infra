package com.kjrepo.infra.common.info;

import java.util.Objects;

public class Tuple5<T1, T2, T3, T4, T5> extends Tuple4<T1, T2, T3, T4> {

	private T5 t5;

	public T5 getT5() {
		return t5;
	}

	public void setT5(T5 t5) {
		this.t5 = t5;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(t5);
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
		Tuple5<T1, T2, T3, T4, T5> other = (Tuple5<T1, T2, T3, T4, T5>) obj;
		return super.equals(obj) && Objects.equals(t5, other.t5);
	}

}
