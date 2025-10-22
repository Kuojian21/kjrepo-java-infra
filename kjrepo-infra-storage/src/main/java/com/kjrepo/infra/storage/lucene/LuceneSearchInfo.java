package com.kjrepo.infra.storage.lucene;

import org.apache.lucene.search.SearcherFactory;

public class LuceneSearchInfo {

	private boolean applyAllDeletes = true;
	private boolean writeAllDeletes = false;
	private SearcherFactory searcherFactory;

	public boolean isApplyAllDeletes() {
		return applyAllDeletes;
	}

	public void setApplyAllDeletes(boolean applyAllDeletes) {
		this.applyAllDeletes = applyAllDeletes;
	}

	public boolean isWriteAllDeletes() {
		return writeAllDeletes;
	}

	public void setWriteAllDeletes(boolean writeAllDeletes) {
		this.writeAllDeletes = writeAllDeletes;
	}

	public SearcherFactory getSearcherFactory() {
		return searcherFactory;
	}

	public void setSearcherFactory(SearcherFactory searcherFactory) {
		this.searcherFactory = searcherFactory;
	}
}
