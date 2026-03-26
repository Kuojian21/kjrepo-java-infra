package com.kjrepo.infra.common.spi;

public interface PkgSpi {

	String pkg();

	default String[] pkgs() {
		return new String[] { pkg() };
	}

}
