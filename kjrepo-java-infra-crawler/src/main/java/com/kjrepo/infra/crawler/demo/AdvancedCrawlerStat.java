package com.kjrepo.infra.crawler.demo;
public class AdvancedCrawlerStat {
    private int processedPages = 0;
    private int totalLinks = 0;
    
    public void incProcessedPages() {
        processedPages++;
    }
    
    public void incTotalLinks(int count) {
        totalLinks += count;
    }
    
    // getters and setters
    public int getProcessedPages() { return processedPages; }
    public int getTotalLinks() { return totalLinks; }
}