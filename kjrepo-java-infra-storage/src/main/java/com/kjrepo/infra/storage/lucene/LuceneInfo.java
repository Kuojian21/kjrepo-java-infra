package com.kjrepo.infra.storage.lucene;

import java.util.Map;

import com.google.common.collect.Maps;

public class LuceneInfo {

	private Map<String, Object> indexWriterConfig = Maps.newHashMap();
	private LuceneSearchInfo searchInfo = new LuceneSearchInfo();

	public Map<String, Object> getIndexWriterConfig() {
		return indexWriterConfig;
	}

	public void setIndexWriterConfig(Map<String, Object> indexWriterConfig) {
		this.indexWriterConfig = indexWriterConfig;
	}

	public LuceneSearchInfo getSearchInfo() {
		return searchInfo;
	}

	public void setSearchInfo(LuceneSearchInfo searchInfo) {
		this.searchInfo = searchInfo;
	}

}
