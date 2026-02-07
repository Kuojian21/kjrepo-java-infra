package com.kjrepo.infra.server.startup;

public interface Startable extends Comparable<Startable> {

	void startup() throws Exception;

	default int priority() {
		return 10;
	}

	default int compareTo(Startable other) {
		return Integer.compare(this.priority(), other.priority());
	}

}
