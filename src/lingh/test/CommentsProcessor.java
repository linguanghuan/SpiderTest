package lingh.test;


import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class CommentsProcessor implements PageProcessor  {

	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
	static String commentsDetail;
    public static synchronized String running(String url) {
        Spider.create(new CommentsProcessor()).addUrl(url).run();
        return commentsDetail;
    }
    
	@Override
	public void process(Page page) {
		page.setSkip(true);
		commentsDetail="";
		commentsDetail = page.getRawText();
	}

	@Override
	public Site getSite() {
		return site;
	}

}
