package com.kjrepo.infra.loader;

public interface Loader {
	default String pkg() {
		return "";
	}
}
