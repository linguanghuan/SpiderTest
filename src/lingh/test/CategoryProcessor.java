package lingh.test;

import java.util.ArrayList;
import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.JsonPathSelector;

public class CategoryProcessor implements PageProcessor  {

	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
	static List<String> categories = new ArrayList<String>();
    public static synchronized List<String> running(String url) {
        Spider.create(new CategoryProcessor()).addUrl(url).run();
        return categories;
    }
    
	@Override
	public void process(Page page) {
		page.setSkip(true);
		categories.clear();
        String cataLog = new JsonPathSelector("$.Categories").select(page.getRawText());
        String tags = new JsonPathSelector("$.Tags").select(page.getRawText());
		Html html=new Html(cataLog);
		List<String> catalogs = html.xpath("//a/text()").all();
		List<String> alltags = new Html(tags).xpath("//a/text()").all();
		for(String catalog:catalogs)
		{
			categories.add(catalog);
		}
		for(String tag:alltags)
		{
			categories.add(tag);
		}
	}

	@Override
	public Site getSite() {
		return site;
	}

}
