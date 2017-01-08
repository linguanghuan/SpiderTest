package lingh.test;


import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class ViewCountProcessor implements PageProcessor  {

	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
	static String viewCount;
    public static synchronized String running(String url) {
        Spider.create(new ViewCountProcessor()).addUrl(url).run();
        return viewCount;
    }
    
	@Override
	public void process(Page page) {
		page.setSkip(true);
		viewCount = "0";
		viewCount = page.getRawText();
	}

	@Override
	public Site getSite() {
		return site;
	}

}
