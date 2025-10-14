package com.kjrepo.infra.storage.mongo;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.kjrepo.infra.common.executor.LazyInfoExecutor;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.UpdateResult;

public class MongoRepository extends LazyInfoExecutor<MongoClient, MongoInfo> {

	public MongoRepository(MongoInfo info) {
		super(info, () -> MongoClients.create(info.getClientSettings(), info.getDriverInformation()));
	}

	/**
	 * com.mongodb.client.model.Filters
	 */
	public MongoCursor<Document> query(String database, String collection, Bson filter) {
		return super.execute(resource -> {
			return resource.getDatabase(database).getCollection(collection).find(filter).iterator();
		});

	}

	public InsertManyResult insert(String database, String collection, List<Document> documents) {
		return super.execute(resource -> {
			InsertManyResult result = resource.getDatabase(database).getCollection(collection).insertMany(documents);
			result.getInsertedIds().forEach((i, v) -> {
				logger.info("{} {}", i, v.asObjectId().getValue());
			});
			return result;
		});
	}

	/**
	 * com.mongodb.client.model.Updates
	 */
	public UpdateResult update(String database, String collection, Bson filter, Bson update) {
		return super.execute(resource -> {
			UpdateResult result = resource.getDatabase(database).getCollection(collection).updateMany(filter, update);
			return result;
		});
	}

	/**
	 * com.mongodb.client.model.Filters
	 */
	public DeleteResult delete(String database, String collection, Bson filter) {
		return super.execute(resource -> {
			DeleteResult result = resource.getDatabase(database).getCollection(collection).deleteMany(filter);
			return result;
		});
	}

}
