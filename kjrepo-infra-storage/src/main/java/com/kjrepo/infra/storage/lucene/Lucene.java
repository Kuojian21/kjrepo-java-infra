package com.kjrepo.infra.storage.lucene;

import java.io.IOException;
import java.io.Closeable;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.term.HookHelper;
import com.kjrepo.infra.text.json.ConfigUtils;

public class Lucene implements Closeable {

	private final LuceneInfo info;
	private final Executor executor;
	private final LazySupplier<IndexWriter> writer;
	private final LazySupplier<SearcherManager> manager;

	public Lucene(String directory, Analyzer analyzer, LuceneInfo info) {
		this(directory, analyzer, null, info);
	}

	public Lucene(String directory, Analyzer analyzer, Executor executor, LuceneInfo info) {
		this.info = Optional.ofNullable(info).orElseGet(() -> new LuceneInfo());
		this.executor = Optional.ofNullable(executor).orElseGet(MoreExecutors::directExecutor);
		this.writer = LazySupplier.wrap(() -> {
			try {
				IndexWriterConfig config = new IndexWriterConfig(analyzer);
				ConfigUtils.config(config, info.getIndexWriterConfig());
				IndexWriter w = new IndexWriter(FSDirectory.open(Paths.get(directory)), config);
				HookHelper.addHook("lucene", () -> {
					w.close();
				});
				return w;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		this.manager = LazySupplier.wrap(() -> {
			try {
				SearcherManager m = new SearcherManager(writer.get(), info.getSearchInfo().isApplyAllDeletes(),
						info.getSearchInfo().isWriteAllDeletes(), info.getSearchInfo().getSearcherFactory());
				HookHelper.addHook("lucene", () -> {
					m.close();
				});
				return m;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public LuceneInfo getInfo() {
		return info;
	}

	public void addDocument(Field... fields) {
		Document doc = new Document();
		Stream.of(fields).forEach(doc::add);
		addDocument(Lists.<Document>newArrayList(doc));
	}

	public void addDocument(List<Document> documents) {
		executor.execute(() -> {
			try {
				for (Document document : documents) {
					this.writer.get().addDocument(document);
				}
				this.writer.get().commit();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public TopDocs search(Query query, int hits) {
		try {
			IndexSearcher searcher = manager.get().acquire();
			try {
				return searcher.search(query, hits);
			} finally {
				manager.get().release(searcher);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() throws IOException {
		if (this.manager.isInited()) {
			this.manager.get().close();
		}
		if (this.writer.isInited()) {
			this.writer.get().close();
		}
	}

}
