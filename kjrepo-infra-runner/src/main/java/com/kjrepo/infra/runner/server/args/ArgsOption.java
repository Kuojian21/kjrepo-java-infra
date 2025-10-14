package com.kjrepo.infra.runner.server.args;

import java.util.Objects;

public class ArgsOption {

	public static ArgsOption of(String key, String value) {
		return new ArgsOption(key, value);
	}

	private final String option;
	private final String key;
	private final String value;

	private ArgsOption(String key, String value) {
		super();
		this.key = key;
		this.value = value;
		this.option = key.replaceAll("^--", "").replaceAll("^-", "");
	}

	public String option() {
		return option;
	}

	public String key() {
		return key;
	}

	public String value() {
		return value;
	}

	public boolean hasArgs() {
		return value != null;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, option, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArgsOption other = (ArgsOption) obj;
		return Objects.equals(key, other.key) && Objects.equals(option, other.option)
				&& Objects.equals(value, other.value);
	}

}
