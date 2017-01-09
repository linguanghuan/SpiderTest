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
	 * @desc ���Ե���
	 */
	private String getSingleTorrent(String torrentLink) throws Exception {
		URL url = new URL(torrentLink);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		// ��������false��������Զ�redirect���ض����ĵ�ַ
		con.setInstanceFollowRedirects(true);
		con.addRequestProperty("Accept-Charset", "UTF-8;");
		con.addRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");
		con.connect();
		System.out.println("code:" + con.getResponseCode());
		InputStream is = con.getInputStream();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// ����һ��Buffer�ַ���
		byte[] buffer = new byte[1024];
		// ÿ�ζ�ȡ���ַ������ȣ����Ϊ-1������ȫ����ȡ���
		int len = 0;
		// ʹ��һ����������buffer������ݶ�ȡ����
		while ((len = is.read(buffer)) != -1) {
			// ���������buffer��д�����ݣ��м����������ĸ�λ�ÿ�ʼ����len�����ȡ�ĳ���
			outStream.write(buffer, 0, len);
		}
		// �ر�������
		is.close();
		// ��outStream�������д���ڴ�
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
