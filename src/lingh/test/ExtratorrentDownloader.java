package lingh.test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.util.concurrent.Semaphore;

import lingh.util.DBHelper;

import org.apache.commons.codec.binary.Base64;

public class ExtratorrentDownloader implements Runnable {
	private String id;
	private String targetLink;
	private DBHelper dbHelper;
	private static final int CONCURRENT_COUNT = 30;
	private static final Semaphore sp = new Semaphore(CONCURRENT_COUNT);  
	public ExtratorrentDownloader(String id, String link) {
		this.id = id;
		this.targetLink = link;
		this.dbHelper = new DBHelper();
	}
	
	/**
	 * @throws Exception
	 * @desc 测试单个
	 */
	private String getSingleTorrent(String torrentLink) throws Exception {
		URL url = new URL(torrentLink);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		// 必须设置false，否则会自动redirect到重定向后的地址
		con.setInstanceFollowRedirects(true);
		con.addRequestProperty("Accept-Charset", "UTF-8;");
		con.addRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");
		con.connect();
		System.out.println("code:" + con.getResponseCode());
		InputStream is = con.getInputStream();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// 创建一个Buffer字符串
		byte[] buffer = new byte[1024];
		// 每次读取的字符串长度，如果为-1，代表全部读取完毕
		int len = 0;
		// 使用一个输入流从buffer里把数据读取出来
		while ((len = is.read(buffer)) != -1) {
			// 用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
			outStream.write(buffer, 0, len);
		}
		// 关闭输入流
		is.close();
		// 把outStream里的数据写入内存
		byte[] imageBytes = outStream.toByteArray();
		String base64Str = Base64.encodeBase64String(imageBytes);
//		System.out.println(base64Str);
		return base64Str;
	}

	@Override
	public void run() {
		try {
//			sp.acquire();
			System.out.println(Thread.currentThread().getName() + " start, used:" +(CONCURRENT_COUNT-sp.availablePermits())+ ", available:" +  sp.availablePermits());
			String base64Torrent = getSingleTorrent(targetLink);
			if (null != base64Torrent)
			{
				String sql = "update extra_to set torrent_base64='"+base64Torrent+"' where id = " + id;
				dbHelper.executeUpdate(sql, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sp.release();
			System.out.println(Thread.currentThread().getName() + " done, used:" +(CONCURRENT_COUNT-sp.availablePermits())+ ", available:" +  sp.availablePermits());
		}
	}

	public static void main(String[] args) {
		// getTorrent("http://extratorrentonline.com/download/5392008/SimplyAnal+-+Ferrera.torrent");
		DBHelper dbHelper = new DBHelper();
		String sql = "select id, torrent_link from extra_to where torrent_base64 is null";
		ResultSet rst =dbHelper.executeQuery(sql, null);
		try {
			int i = 0;
			while (rst.next())
			{
				String id = rst.getString("id");
				String torrentLink = rst.getString("torrent_link");
				System.out.println("id:" + id + ", torrent link:" + torrentLink);
				ExtratorrentDownloader downloader = new ExtratorrentDownloader(id, torrentLink);
				ExtratorrentDownloader.sp.acquire();
				Thread downloadThread = new Thread(downloader);
				downloadThread.start();
				i++;
				System.out.println("################deal " + i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
