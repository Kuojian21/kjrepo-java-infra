package com.kjrepo.infra.storage.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoDriverInformation;

public class MongoInfo {
	private MongoClientSettings clientSettings;
	private MongoDriverInformation driverInformation;

	public MongoClientSettings getClientSettings() {
		return clientSettings;
	}

	public void setClientSettings(MongoClientSettings clientSettings) {
		this.clientSettings = clientSettings;
	}

	public MongoDriverInformation getDriverInformation() {
		return driverInformation;
	}

	public void setDriverInformation(MongoDriverInformation driverInformation) {
		this.driverInformation = driverInformation;
	}

}
