package com.kjrepo.infra.crawler.demo;

import java.io.File;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class BasicCrawlerController {
	
//	--add-opens java.management/sun.management=ALL-UNNAMED
//	--add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED
	public static void main(String[] args) throws Exception {
		// 1. 定义爬虫数据存储文件夹
		String crawlStorageFolder = System.getProperty("user.dir") + File.separator + "crawler";

		// 2. 配置爬虫参数
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);

		// 可选配置
		config.setMaxDepthOfCrawling(10); // 设置爬取最大深度
		config.setMaxPagesToFetch(1000); // 设置最大爬取页面数
		config.setPolitenessDelay(1000); // 设置请求间隔延迟(毫秒)，遵守robots.txt
		config.setIncludeBinaryContentInCrawling(false); // 不爬取二进制文件(图片、PDF等)
		config.setUserAgentString("MyCrawler4jBot (https://gimytw.cc)"); // 设置User Agent

		// 3. 初始化爬虫组件
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		robotstxtConfig.setEnabled(false);
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

		// 4. 创建爬虫控制器
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

		// 5. 添加种子URL(起始URL)
		controller.addSeed("https://gimytw.cc/"); // 使用 httpbin 作为示例网站
		// controller.addSeed("https://example.com/another-page");

		// 6. 启动爬虫(阻塞操作，直到爬虫结束)
		// 参数：爬虫类，爬虫实例数量
		controller.start(BasicCrawler.class, 5); // 使用5个爬虫线程
	}
}