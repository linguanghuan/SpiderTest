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
	            //��������false��������Զ�redirect���ض����ĵ�ַ  
	        con.setInstanceFollowRedirects(true);
	        con.addRequestProperty("Accept-Charset", "UTF-8;");  
	        con.addRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");  
	        con.connect();
	        System.out.println("code:" + con.getResponseCode());
			InputStream is = con.getInputStream();
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
	        //����һ��Buffer�ַ���  
	        byte[] buffer = new byte[1024];  
	        //ÿ�ζ�ȡ���ַ������ȣ����Ϊ-1������ȫ����ȡ���  
	        int len = 0;  
	        //ʹ��һ����������buffer������ݶ�ȡ����  
	        while( (len=is.read(buffer)) != -1 ){  
	            //���������buffer��д�����ݣ��м����������ĸ�λ�ÿ�ʼ����len�����ȡ�ĳ���  
	            outStream.write(buffer, 0, len);  
	        }  
	        //�ر�������  
	        is.close();  
	        //��outStream�������д���ڴ�  
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
