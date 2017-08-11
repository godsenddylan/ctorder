package net.chetong.order.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.chetong.order.service.common.BaseService;

/**
 * 腾讯云下载工具类
 * @author wufj@chetong.net
 *         2016年1月19日 上午11:13:54
 */
public class TencentDownloadUtil {
	protected static Logger log = LogManager.getLogger(TencentDownloadUtil.class);
	/**
	 * 腾讯云下载文件
	 * @author wufj@chetong.net
	 *         2016年1月19日 上午11:13:31
	 * @param download_url
	 * @param fileName
	 * @return
	 */
	 public static int downloadByUrl(String download_url, String fileName){
	        if("".equals(fileName)){
	            return 1001;
	        }
	        try{
	            URL realUrl = new URL(download_url);
	            HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
	            connection.setRequestMethod("GET");
	            connection.setDoInput(true);
	            connection.setDoOutput(true);
	            connection.connect();	
	            
	            InputStream in = new DataInputStream(connection.getInputStream());
	            File file = new File(fileName);  
	            DataOutputStream ops = new DataOutputStream(new FileOutputStream(file));
	            int bytes = 0;
	            byte[] bufferOut = new byte[1024];
	            while ((bytes = in.read(bufferOut)) > 0) {
	                ops.write(bufferOut, 0, bytes);  
	            }
	            ops.close();
	            in.close();
	        }catch(Exception e){
	        	log.error("腾讯云下载文件失败",e);
	            return 1002;
	        }
	        return 1000;
	}
}
