package lingh.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import lingh.dao.CnBlogDao;
import lingh.dao.impl.CnBlogDaoImpl;
import lingh.entity.CnBlogDetail;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.JsonPathSelector;
import us.codecraft.webmagic.selector.Selectable;

public class CnblogsPageProcessor implements PageProcessor {

	// 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(5000);
    
//    private static final String AUTHOR = "LittleHann";
//    private static final String BLOG_ID = "152568"; 
//    
//    private static final String AUTHOR = "qiyeboy";
//    private static final String BLOG_ID = "269038"; 
//    
//    private static final String AUTHOR = "coder2012";
//    private static final String BLOG_ID = "129585"; 
    
    private static final String AUTHOR = "orlion";
    private static final String BLOG_ID = "241238";
    
    private static final String LIST_URL = "http://www\\.cnblogs\\.com/" + AUTHOR + "/default\\.html\\?page=.*";
    private static final String ARITICALE_URL = "http://www\\.cnblogs\\.com/" + AUTHOR + "/p/(\\d+)\\.html";
    //http://www.cnblogs.com/coder2012/archive/2012/10/02/2710518.html
    private static final String ARITICALE_URL2 = "http://www\\.cnblogs\\.com/" + AUTHOR + "/archive/.*/(\\d+)\\.html";
	@Override
	public void process(Page page) {
		if (page.getUrl().regex(LIST_URL).match())
		{
			//http://www.cnblogs.com/LittleHann/default.html?page=1
			Html html = page.getHtml();
			Selectable targets = html.xpath("//div[@id='mainContent']/div[@class='forFlow']/div[@class='day']");
			List<Selectable> nodes = targets.nodes();
			List<String> urls = new ArrayList<String>();
			for (Selectable node: nodes)
			{
				String title = node.xpath("//div[@class='postTitle']/a[@class='postTitle2']/text()").toString();
				System.out.println("title:" + title);
				String herf = node.xpath("//div[@class='postTitle']/a[@class='postTitle2']/@href").toString();
				System.out.println("herf:" + herf);
				String desc = node.xpath("//div[@class='postCon']/div[@class='c_b_p_desc']/text()").toString();
				System.out.println("desc:" + desc);
				if(desc.contains("密码保护"))
				{
					System.out.println("#############Skip title:" + title + ",url:" + herf);
					continue;
				}
				System.out.println("Add title:" + title + ",url:" + herf);
				urls.add(herf);
//				break;
			}
			page.addTargetRequests(urls);
		}
		else if(page.getUrl().regex(ARITICALE_URL).match() || page.getUrl().regex(ARITICALE_URL2).match())
		{
			CnBlogDetail cnBlogDetail = new CnBlogDetail();
			//http://www.cnblogs.com/LittleHann/p/6133733.html
			System.out.println("url:" + page.getUrl().toString());
			cnBlogDetail.setUrl(page.getUrl().toString());
			String postId=page.getUrl().regex(ARITICALE_URL).toString();
			if (postId==null)
			{
				postId = page.getUrl().regex(ARITICALE_URL2).toString();
			}
			System.out.println("postID:" + postId);
			cnBlogDetail.setPostId(postId);
			Html html = page.getHtml();
			
			Selectable targets = html.xpath("//div[@id='post_detail']");
			String title = targets.xpath("//h1[@class='postTitle']/a[@id='cb_post_title_url']/text()").toString();
			System.out.println("title:" + title);
			cnBlogDetail.setTitle(title);
			String time = targets.xpath("//div[@class='postDesc']/span[@id='post-date']/text()").toString();
			System.out.println("time:" + time);
			cnBlogDetail.setTime(time);
			Selectable body = targets.xpath("//div[@id='cnblogs_post_body']");
//			System.out.println(body);
			cnBlogDetail.setContent(body.toString());
			Selectable images = body.xpath("//img");
			String imageStr ="";
			HashMap<String, String> imageReplace = new HashMap<String,String>();
			for(String image:images.all())
			{
				Html imageHtml = new Html(image);
				// <img src="http://images2015.cnblogs.com/blog/532548/201612/532548-20161205095824913-403392471.png" alt="">
				// <img id="img" src="http://www.joesecurity.org/img/technology/hca-new.png?_=4460954" style="border:none;max-width:686px">
				if(imageHtml.regex("src=\"http:").match())
				{
					String imageUrl  = imageHtml.xpath("//img/@src").toString();
//					System.out.println("image:" + image + ", url:" + imageUrl);
					imageStr = imageStr + image + "{sep}" + imageUrl ;
					try {
						URL url = new URL(imageUrl);
						URLConnection con = url.openConnection();
						con.setConnectTimeout(10*1000);
						InputStream is = con.getInputStream();
						ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
				        //创建一个Buffer字符串  
				        byte[] buffer = new byte[1024];  
				        //每次读取的字符串长度，如果为-1，代表全部读取完毕  
				        int len = 0;  
				        //使用一个输入流从buffer里把数据读取出来  
				        while( (len=is.read(buffer)) != -1 ){  
				            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度  
				            outStream.write(buffer, 0, len);  
				        }  
				        //关闭输入流  
				        is.close();  
				        //把outStream里的数据写入内存  
				        byte[] imageBytes= outStream.toByteArray();
						String base64Image = Base64.encodeBase64String(imageBytes);
//						System.out.println("base64Image[" +imageBytes.length+ "]:" + base64Image);
						imageStr = imageStr + "{sep}" + base64Image + "\n";
						String ext = StringUtils.substring(imageUrl, StringUtils.lastIndexOf(imageUrl, ".")+1);
						//http://img.blog.csdn.net/2016031716052173 /ext结果net/2016031716052173好吧，总是会有结果的但是规范
						if (ext==null || ext.length()==0 || ext.length() > 8)  // 默认png格式
						{
							ext="png";
						}
						String replaceStr = "<img src=\"data:image/"+ext+";base64,"+base64Image+"\" alt=\"\">";
						imageReplace.put(image, replaceStr);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					System.out.println("####ignore image:" + image);
				}
			}
			
			if (imageStr.length() > 0)
			{
				cnBlogDetail.setContentImages(imageStr);
				String newBody = body.toString();
				for (Map.Entry<String, String> entry : imageReplace.entrySet()) {
					newBody = newBody.replaceAll(entry.getKey(), entry.getValue());
				}
				cnBlogDetail.setNewContent(newBody);
			}
			
			String catalogUrl = "http://www.cnblogs.com/mvc/blog/CategoriesTags.aspx?blogApp="+AUTHOR+"&blogId="+BLOG_ID+"&postId="+ postId;
//			page.addTargetRequest(catalogUrl);
			List<String> categories = CategoryProcessor.running(catalogUrl);
			System.out.println("category:" + categories.toString());
			cnBlogDetail.setTags(categories.toString());
			String viewCountUrl = "http://www.cnblogs.com/mvc/blog/ViewCountCommentCout.aspx?postId="+ postId;
			String viewCount = ViewCountProcessor.running(viewCountUrl);
			System.out.println("view count:" + viewCount);
			cnBlogDetail.setViewCount(viewCount);
			String commentsUrl = "http://www.cnblogs.com/mvc/blog/GetComments.aspx?postId="+postId+"&blogApp="+AUTHOR+"&pageIndex=0&anchorCommentId=0";
			String commentsDetail = CommentsProcessor.running(commentsUrl);
			String commentCount = new JsonPathSelector("$.commentCount").select(commentsDetail);
			String commentsHtml = new JsonPathSelector("$.commentsHtml").select(commentsDetail);
			System.out.println("commentCount:" + commentCount);
			cnBlogDetail.setCommentCount(commentCount);
//			System.out.println("commentsHtml:" + commentsHtml);
			cnBlogDetail.setComments(commentsHtml);
			cnBlogDetail.setAuthor(AUTHOR);
			CnBlogDao cnBlogDao = new CnBlogDaoImpl();
			cnBlogDao.saveDetail(cnBlogDetail);
		}		
		else
		{
			System.out.println("################url not match");
		}
	}

	@Override
	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {
//		Spider.create(new CnblogsLittleHannPageProcessor()).addUrl("http://www.cnblogs.com/LittleHann/default.html?page=1").thread(1).run();
		CnblogsPageProcessor cnblogsLittleHannPageProcessor = new CnblogsPageProcessor();
		Spider create = Spider.create(cnblogsLittleHannPageProcessor);
		for (int i=1; i<=50; i++)
		{
			create.addUrl("http://www.cnblogs.com/"+AUTHOR+"/default.html?page="+i);
		}
		create.thread(10).run();
	}
	
}
