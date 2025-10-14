package com.kjrepo.infra.common.info;

import java.util.Objects;

public class Tuple3<T1, T2, T3> extends Tuple<T1, T2> {

	private T3 t3;

	public T3 getT3() {
		return t3;
	}

	public void setT3(T3 t3) {
		this.t3 = t3;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(t3);
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
		Tuple3<T1, T2, T3> other = (Tuple3<T1, T2, T3>) obj;
		return super.equals(obj) && Objects.equals(t3, other.t3);
	}

}
