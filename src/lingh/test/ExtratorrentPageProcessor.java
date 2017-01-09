package lingh.test;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Random;

import lingh.dao.ExtraTorrentDao;
import lingh.dao.impl.ExtraTorrentDaoImpl;
import lingh.entity.ExtraTorrentDetail;

import org.apache.commons.codec.binary.Base64;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

public class ExtratorrentPageProcessor implements PageProcessor {

	// 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
	private Site site = null;
	private final static String PATH="E:\\linghtest\\Extratorrent\\";
	
	private String getTorrent(String torrentLink)
	{
		try {
			URL url = new URL(torrentLink);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("GET");  
	            //必须设置false，否则会自动redirect到重定向后的地址  
	        con.setInstanceFollowRedirects(true);
	        con.addRequestProperty("Accept-Charset", "UTF-8;");  
	        con.addRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");  
	        con.connect();
	        System.out.println("code:" + con.getResponseCode());
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
	        String base64 = Base64.encodeBase64String(imageBytes);
	        System.out.println("base64:" + base64);
			return base64;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public void process(Page page) {
		try {
			System.out.println("##############deal:" + page.getUrl().toString());
			Random r = new Random();
			File file = new File(PATH + r.nextInt() + ".html");
			FileOutputStream out = new FileOutputStream(file);
			out.write(page.getHtml().toString().getBytes());
			out.flush();
			out.close();
			Html html = page.getHtml();
			Selectable allpage = html.xpath("//a[@class='pager_link']/@href");
			Selectable tlzs = html.xpath("//tbody/tr[@class='tlz']");
			Selectable tlrs = html.xpath("//tbody/tr[@class='tlr']");
			List<Selectable> all = tlzs.nodes();
			all.addAll(tlrs.nodes());
			for (Selectable tlz:tlzs.nodes())
			{
				ExtraTorrentDetail detail = new ExtraTorrentDetail();
				int i = 0;
				for(Selectable td: tlz.xpath("//td").nodes())
				{
					System.out.println("td["+i+"]" + td.toString());
					switch(i)
					{
					case 0:
						List<String> hrefs = td.xpath("//a/@href").all(); 
						System.out.println("torrent:" + hrefs.get(0));
						detail.setTorrentLink(hrefs.get(0));
//						String torrent = getTorrent(hrefs.get(0));
//						System.out.println("torrent content base64:" + torrent);
//						detail.setTorrentBase64(torrent);
						System.out.println("maget link:" + hrefs.get(1));
						detail.setMagnet(hrefs.get(1));
						break;
					case 1:
						String country = td.xpath("//img/@alt").toString();
						System.out.println("country:" + country);
						detail.setCountry(country);
//						String link = td.xpath("//a/@href").toString();
//						System.out.println("link:" + link);
//						detail.setTorrentLink(link);
						String subject = td.xpath("//a/text()").toString();
						System.out.println("subject:" + subject);
						detail.setSubject(subject);
						List<Selectable> tmp = td.xpath("//div[@class='usr']/a").nodes();
						Selectable tmp2 = tmp.get(tmp.size()-1);
						String author = tmp2.xpath("/a/text()").toString();
						System.out.println("author:" + author);
						detail.setAuthor(author);
						break;
					case 2:
						String time = td.xpath("//td/text()").toString();
						System.out.println("time:" + time);
						detail.setTime(time);
						break;
					case 3:
						String size = td.xpath("//td/text()").toString();
						size.replace("&nbsp;", " ");
						System.out.println("size:" + size);
						detail.setSize(size);
						break;
					case 4:
						String sn = td.xpath("//td/text()").toString();
						System.out.println("sn:" + sn);
						detail.setSeeds(sn);
						break;		
					case 5:
						String ln = td.xpath("//td/text()").toString();
						System.out.println("ln:" + ln);
						detail.setLeechers(ln);
						break;	
					case 6:
						String health = td.xpath("//div/@class").toString();
						System.out.println("health:" + health);
						detail.setHealth(health);
						break;							
					}
					i++;
				}
				ExtraTorrentDao dao = new ExtraTorrentDaoImpl();
				dao.saveDetail(detail);
			}
			page.addTargetRequests(allpage.all());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Site getSite() {
		if (site == null) {
            site = Site.me().setDomain("extratorrentonline.com").addStartUrl("http://extratorrentonline.com/category/948/HD+Video+Torrents.html").setSleepTime(1000);
        }
        return site;
	}

	public static void main(String[] args) {
		Spider.create(new ExtratorrentPageProcessor()).thread(30)
		.pipeline(new FilePipeline("E:\\linghtest\\test\\"))
        .downloader(new SeleniumDownloader("E:\\linghtest\\chromedriver_win32\\chromedriver.exe").setSleepTime(5))
        .run();
	}

}
