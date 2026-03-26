package com.kjrepo.infra.crawler.demo;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.kjrepo.infra.common.logger.LoggerUtils;

import java.util.Set;
import java.util.regex.Pattern;

public class BasicCrawler extends WebCrawler {
	private static final Logger logger = LoggerUtils.logger(BasicCrawler.class);

	// 定义过滤规则：只爬取指定模式的URL
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz|pdf))$");

	// 定义要爬取的域名(可选，用于限制爬取范围)
//	private final static Pattern DOMAINS = Pattern.compile(".*\\.httpbin\\.org.*"); // 只爬取 httpbin 域下的页面

	/**
	 * 这个方法决定哪些URL应该被爬取
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();

		// 排除静态文件
		if (FILTERS.matcher(href).matches()) {
			return false;
		}

		// 只爬取指定域名的页面(如果设置了DOMAINS)
//        if (DOMAINS != null && !DOMAINS.matcher(href).matches()) {
//            return false;
//        }
		logger.debug("{} href:{}", url.getURL(), href);
		// 示例：只爬取包含特定路径的URL
		return true; // href.startsWith("https://httpbin.org/");
	}

	/**
	 * 当页面下载完成后，这个方法会被调用
	 */
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		logger.info("正在处理: {}", url);

		// 只处理HTML内容
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();

			// 获取页面标题
			String title = htmlParseData.getTitle();

			// 获取页面文本内容(去除HTML标签)
			String text = htmlParseData.getText();

			// 获取页面HTML
			String html = htmlParseData.getHtml();

			// 获取页面中的所有链接
			Set<WebURL> links = htmlParseData.getOutgoingUrls();

			// 打印提取的信息
			logger.info("第" + page.getWebURL().getDepth() + "层=======================================");
			logger.info("URL: " + url);
			logger.info("标题: " + title);
			logger.info("文本长度: " + text.length());
			logger.info("HTML长度: " + html.length());
			logger.info("发现链接数: " + links.size());
			links.forEach(link -> {
//				logger.info("{}", link.getURL());
			});
			logger.info("=======================================\n");

			// 这里可以添加数据存储逻辑，比如保存到数据库或文件
			// saveToDatabase(url, title, text);
		}
	}
}