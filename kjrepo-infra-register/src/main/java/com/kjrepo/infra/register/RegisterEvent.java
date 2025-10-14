package com.kjrepo.infra.register;

public class RegisterEvent<D> {

	private String key;
	private D oldData;
	private D newData;

	public String getKey() {
		return key;
	}

	public void setKey(String id) {
		this.key = id;
	}

	public D getOldData() {
		return oldData;
	}

	public void setOldData(D oldData) {
		this.oldData = oldData;
	}

	public D getNewData() {
		return newData;
	}

	public void setNewData(D newData) {
		this.newData = newData;
	}

}
