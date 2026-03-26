package com.kjrepo.infra.storage.hdfs;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.kjrepo.infra.executor.lazy.LazyExecutor;

public class HdfsRepository extends LazyExecutor<FileSystem, Configuration> {

	public HdfsRepository(URI uri, Configuration conf) {
		super(conf, () -> {
			try {
				return FileSystem.newInstance(uri, conf);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public FSDataOutputStream create(Path path, boolean overwrite) throws IOException {
		return super.execute(resource -> {
			return resource.create(path, overwrite);
		}, new String[] { "create" });
	}

	public FSDataInputStream open(Path path) throws IOException {
		return super.execute(resource -> {
			return resource.open(path);
		}, new String[] { "open" });
	}

}
