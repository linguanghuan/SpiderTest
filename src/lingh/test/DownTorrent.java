package lingh.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


import org.apache.commons.codec.binary.Base64;

public class DownTorrent {

	private static String getTorrent(String torrentLink)
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
			System.out.println(Base64.encodeBase64String(imageBytes));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		getTorrent("http://extratorrentonline.com/download/5392008/SimplyAnal+-+Ferrera.torrent");
	}

}
