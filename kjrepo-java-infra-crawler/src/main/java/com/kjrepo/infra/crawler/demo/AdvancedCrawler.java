package com.kjrepo.infra.crawler.demo;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class AdvancedCrawler extends WebCrawler {
	private AdvancedCrawlerStat myStat;

	public AdvancedCrawler() {
		myStat = new AdvancedCrawlerStat();
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		return !Pattern.compile("").matcher(href).matches() && href.startsWith("https://httpbin.org/");
	}

	@Override
	public void visit(Page page) {
		myStat.incProcessedPages();

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();
			myStat.incTotalLinks(links.size());

			System.out.println("已处理页面: " + page.getWebURL().getURL());
			System.out.println("发现链接: " + links.size());
		}
	}

	/**
	 * 当该爬虫线程完成任务时调用
	 */
	@Override
	public Object getMyLocalData() {
		return myStat;
	}

	/**
	 * 在所有爬虫结束后处理收集的数据
	 */
	public static class CrawlStatProcessor implements CrawlController.WebCrawlerFactory<AdvancedCrawler> {
		@Override
		public AdvancedCrawler newInstance() throws Exception {
			return new AdvancedCrawler();
		}

		public static void processCrawlerData(AdvancedCrawlerStat[] stats) {
			int totalPages = 0;
			int totalLinks = 0;

			for (AdvancedCrawlerStat stat : stats) {
				totalPages += stat.getProcessedPages();
				totalLinks += stat.getTotalLinks();
			}

			System.out.println("\n======= 爬取统计 =======");
			System.out.println("总处理页面: " + totalPages);
			System.out.println("总发现链接: " + totalLinks);
			System.out.println("========================");
		}
	}
}