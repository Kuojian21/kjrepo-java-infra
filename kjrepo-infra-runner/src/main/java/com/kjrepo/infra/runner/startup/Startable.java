package com.kjrepo.infra.runner.startup;

import java.sql.SQLException;

public interface Startable extends Comparable<Startable> {

	void startup() throws SQLException;

	default int priority() {
		return 10;
	}

	default int compareTo(Startable other) {
		return Integer.compare(this.priority(), other.priority());
	}

}
