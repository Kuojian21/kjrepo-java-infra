package com.kjrepo.infra.storage.legacy;

import java.io.IOException;
import java.io.Closeable;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;

import com.annimon.stream.Stream;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.term.HookHelper;
import com.kjrepo.infra.common.utils.StackUtils;
import com.kjrepo.infra.register.context.RegisterFactory;

public class XLucene implements Closeable {

	private final Logger logger = LoggerUtils.logger(getClass());
	private final LazySupplier<IndexWriter> writer;
	private final LazySupplier<LazySupplier<IndexSearcher>> searcher;
	private final String key;
	private final XLuceneRepository repo;

	public XLucene(String directory, Analyzer analyzer, String key, XLuceneRepository repo) {
		this.key = key;
		this.repo = repo;
		this.writer = LazySupplier.wrap(() -> {
			try {
				IndexWriterConfig config = new IndexWriterConfig(analyzer);
				IndexWriter w = new IndexWriter(FSDirectory.open(Paths.get(directory)), config);
				HookHelper.addHook("lucene", () -> {
					w.close();
				});
				return w;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		this.searcher = LazySupplier.wrap(() -> {
			LazySupplier<IndexSearcher> is = LazySupplier.wrap(() -> {
				try {
					return new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(directory))));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
			RegisterFactory.getContext(StackUtils.firstBusinessInvokerClassname()).getRegister(Long.class)
					.addListener(key, e -> {
						if (is.isInited()) {
							IndexSearcher ois = is.get();
							is.refresh();
							try {
								ois.getIndexReader().close();
							} catch (IOException e1) {
								logger.error("", e1);
							}
						}
					});
			HookHelper.addHook("lucene", () -> {
				if (is.isInited()) {
					is.get().getIndexReader().close();
				}
			});
			return is;
		});
	}

	public void addDocument(Field... fields) {
		Document doc = new Document();
		Stream.of(fields).forEach(doc::add);
		addDocument(Lists.<Document>newArrayList(doc));
	}

	public void addDocument(List<Document> documents) {
		repo.execute(key, () -> {
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
			return searcher.get().get().search(query, hits);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() throws IOException {
		if (this.searcher.isInited()) {
			this.searcher.get().get().getIndexReader().close();
		}
		if (this.writer.isInited()) {
			this.writer.get().close();
		}
	}

}
