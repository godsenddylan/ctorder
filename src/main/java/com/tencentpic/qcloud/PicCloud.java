package com.tencentpic.qcloud;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;
import com.tencentpic.qcloud.sign.FileCloudSign;
import com.tencentpic.qcloud.sign.MD5;

public class PicCloud
{
	protected static String QCLOUD_DOMAIN = "web.image.myqcloud.com/photos/v1";
	protected static String QCLOUD_DOWNLOAD_DOMAIN = "image.myqcloud.com";
	
	protected int m_appid;
	protected String m_secret_id;
	protected String m_secret_key;
        
        protected int m_errno;
        protected String m_error;

        /**
	    PicCloud 构造方法
            @param  appid        授权appid
	    @param  secret_id    授权secret_id
	    @param  secret_key   授权secret_key
        */
        public PicCloud(int appid, String secret_id, String secret_key){
            m_appid = appid;
            m_secret_id = secret_id;
            m_secret_key = secret_key;
            m_errno = 0;
            m_error = "";
        }
            
        /**
	    PicCloud 构造方法
            @param  appid        授权appid
	    @param  secret_id    授权secret_id
	    @param  secret_key   授权secret_key
        */
		public PicCloud(String appid, String secret_id, String secret_key){
	            m_appid = Integer.parseInt(appid);
	            m_secret_id = secret_id;
	            m_secret_key = secret_key;
	            m_errno = 0;
	            m_error = "";
		}
        
        public int GetErrno(){
            return m_errno;
        }
        
        public String GetErrMsg(){
            return m_error;
        }
        
        public int SetError(int errno, String msg){
            m_errno = errno;
            m_error = msg;
            return errno;
        }
        
        public String GetError(){
            return "errno="+m_errno+" desc="+m_error;
        }

	public int Upload(String userid, String fileName, UploadResult result){
		String req_url = "http://"+QCLOUD_DOMAIN+"/"+m_appid+"/"+userid;
		
		//create sign
		StringBuffer sign = new StringBuffer("");		
		long expired = System.currentTimeMillis() / 1000 + 2592000;
		FileCloudSign.appSign(Integer.toString(m_appid), m_secret_id, m_secret_key, expired, userid, sign);
		String qcloud_sign="QCloud "+sign.toString();
		
		try{
			String BOUNDARY = "---------------------------" + MD5.stringToMD5(String.valueOf(System.currentTimeMillis())).substring(0,15);

			URL realUrl = new URL(req_url);
			HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
			//set header
			connection.setRequestMethod("POST");
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("Host", "web.image.myqcloud.com");
			connection.setRequestProperty("user-agent","qcloud-java-sdk");
			connection.setRequestProperty("Authorization", qcloud_sign);
			
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);  

			OutputStream out = new DataOutputStream(connection.getOutputStream());
			StringBuffer strBuf = new StringBuffer();
			
			if(fileName != null){
				File file = new File(fileName);  
				String filename = file.getName();
				String contentType = URLConnection.getFileNameMap().getContentTypeFor(fileName);
				
				strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");  
				strBuf.append("Content-Disposition: form-data; name=\"FileContent\"; filename=\"" + fileName + "\"\r\n");
				strBuf.append("Content-Type:" + contentType + "\r\n\r\n");  
				
				out.write(strBuf.toString().getBytes());

				DataInputStream ins = new DataInputStream(new FileInputStream(file));  
				int bytes = 0;  
				byte[] bufferOut = new byte[1024];  
				while ((bytes = ins.read(bufferOut)) != -1) {  
					out.write(bufferOut, 0, bytes);  
				}
				ins.close(); 
			}

			byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();  
			out.write(endData); 
			out.flush();  
			out.close(); 
			
			connection.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
			
			String rsp = "";
			String line;
			while ((line = in.readLine()) != null) {
				rsp += line;
			}

			JSONObject jsonObject = new JSONObject(rsp);
			int code = -1;
			String data = "";
			Iterator iterator = jsonObject.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				if(key.equals("code")){
					code = Integer.parseInt(jsonObject.getString(key));
				}else if(key.equals("data")){
					data = jsonObject.getString(key);
				}
			}

			if(code != 0 || data == "")
				return code;

			jsonObject = new JSONObject(data);
			iterator = jsonObject.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				if(key.equals("url")){
					result.url = jsonObject.getString(key);
				}else if(key.equals("download_url")){
					result.download_url = jsonObject.getString(key);
				}
			}

		}catch(Exception e){
			e.printStackTrace(); 
			return -1;
		}
		return 0;
	}
	
	public int UploadByFile(String userid, MultipartFile file, UploadResult result){
		String req_url = "http://"+QCLOUD_DOMAIN+"/"+m_appid+"/"+userid;
		
		//create sign
		StringBuffer sign = new StringBuffer("");		
		long expired = System.currentTimeMillis() / 1000 + 2592000;
		FileCloudSign.appSign(Integer.toString(m_appid), m_secret_id, m_secret_key, expired, userid, sign);
		String qcloud_sign="QCloud "+sign.toString();
		
		try{
			String BOUNDARY = "---------------------------" + MD5.stringToMD5(String.valueOf(System.currentTimeMillis())).substring(0,15);

			URL realUrl = new URL(req_url);
			HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
			//set header
			connection.setRequestMethod("POST");
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("Host", "web.image.myqcloud.com");
			connection.setRequestProperty("user-agent","qcloud-java-sdk");
			connection.setRequestProperty("Authorization", qcloud_sign);
			
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);  

			OutputStream out = new DataOutputStream(connection.getOutputStream());
			StringBuffer strBuf = new StringBuffer();
			if(file != null){
				String filename = file.getOriginalFilename();
				String contentType = URLConnection.getFileNameMap().getContentTypeFor(filename);
				
				strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");  
				strBuf.append("Content-Disposition: form-data; name=\"FileContent\"; filename=\"" + filename + "\"\r\n");
				strBuf.append("Content-Type:" + contentType + "\r\n\r\n");  
				
				out.write(strBuf.toString().getBytes());

				DataInputStream ins = new DataInputStream((FileInputStream) file.getInputStream());  
				int bytes = 0;  
				byte[] bufferOut = new byte[1024];  
				while ((bytes = ins.read(bufferOut)) != -1) {  
					out.write(bufferOut, 0, bytes);  
				}
				ins.close(); 
			}

			byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();  
			out.write(endData); 
			out.flush();  
			out.close(); 
			
			connection.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
			
			String rsp = "";
			String line;
			while ((line = in.readLine()) != null) {
				rsp += line;
			}

			JSONObject jsonObject = new JSONObject(rsp);
			int code = -1;
			JSONObject data = null;
			Iterator iterator = jsonObject.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				if(key.equals("code")){
					code = jsonObject.getInt(key);
				}else if(key.equals("data")){
					data = jsonObject.getJSONObject(key);
				}
			}

			if(code != 0 || data == null)
				return code;

			iterator = data.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				if(key.equals("url")){
					result.url = data.getString(key);
				}else if(key.equals("download_url")){
					result.download_url = data.getString(key);
				}
			}

		}catch(Exception e){
			e.printStackTrace(); 
			return -1;
		}
		return 0;
	}
	
	public int UploadByFile(String userid, File file, UploadResult result){
		String req_url = "http://"+QCLOUD_DOMAIN+"/"+m_appid+"/"+userid;
		
		//create sign
		StringBuffer sign = new StringBuffer("");		
		long expired = System.currentTimeMillis() / 1000 + 2592000;
		FileCloudSign.appSign(Integer.toString(m_appid), m_secret_id, m_secret_key, expired, userid, sign);
		String qcloud_sign="QCloud "+sign.toString();
		
		try{
			String BOUNDARY = "---------------------------" + MD5.stringToMD5(String.valueOf(System.currentTimeMillis())).substring(0,15);

			URL realUrl = new URL(req_url);
			HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
			//set header
			connection.setRequestMethod("POST");
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("Host", "web.image.myqcloud.com");
			connection.setRequestProperty("user-agent","qcloud-java-sdk");
			connection.setRequestProperty("Authorization", qcloud_sign);
			
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);  

			OutputStream out = new DataOutputStream(connection.getOutputStream());
			StringBuffer strBuf = new StringBuffer();
			if(file != null){
				String filename = file.getName();
				String contentType = URLConnection.getFileNameMap().getContentTypeFor(filename);
				
				strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");  
				strBuf.append("Content-Disposition: form-data; name=\"FileContent\"; filename=\"" + filename + "\"\r\n");
				strBuf.append("Content-Type:" + contentType + "\r\n\r\n");  
				
				out.write(strBuf.toString().getBytes());
				DataInputStream ins = new DataInputStream(new FileInputStream(file));  
				int bytes = 0;  
				byte[] bufferOut = new byte[1024];  
				while ((bytes = ins.read(bufferOut)) != -1) {  
					out.write(bufferOut, 0, bytes);  
				}
				ins.close(); 
			}

			byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();  
			out.write(endData); 
			out.flush();  
			out.close(); 
			
			connection.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
			
			String rsp = "";
			String line;
			while ((line = in.readLine()) != null) {
				rsp += line;
			}

			JSONObject jsonObject = new JSONObject(rsp);
			int code = -1;
			JSONObject data = null;
			Iterator iterator = jsonObject.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				if(key.equals("code")){
					code = jsonObject.getInt(key);
				}else if(key.equals("data")){
					data = jsonObject.getJSONObject(key);
				}
			}

			if(code != 0 || data == null)
				return code;

			iterator = data.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				if(key.equals("url")){
					result.url = data.getString(key);
				}else if(key.equals("download_url")){
					result.download_url = data.getString(key);
				}
			}

		}catch(Exception e){
			e.printStackTrace(); 
			return -1;
		}
		return 0;
	}
	
	/**
    Download    下载图片（不启用防盗链）
        @param  userid      业务账号,没有填0
    @param  fileid      图片的唯一标识
        @param  fileName    下载图片的保存路径
        @return 错误码，0为成功
    */
    public int Download(int userid, String fileid, String fileName){
        String download_url = "http://"+m_appid+"."+QCLOUD_DOWNLOAD_DOMAIN+"/"+m_appid+"/"+userid+"/"+fileid+"/original";
        return Download(download_url, fileName);
    }
    
    /**
    DownloadEx  下载图片（启用防盗链）
        @param  userid      业务账号,没有填0
    @param  fileid      图片的唯一标识
        @param  fileName    下载图片的保存路径
        @return 错误码，0为成功
    */
    public int DownloadEx(int userid, String fileid, String fileName){
        String download_url = "http://"+m_appid+"."+QCLOUD_DOWNLOAD_DOMAIN+"/"+m_appid+"/"+userid+"/"+fileid+"/original";
                    //create sign once
        StringBuffer sign = new StringBuffer("");		
        if(0 != FileCloudSign.appSignOnce(Integer.toString(m_appid), m_secret_id, m_secret_key, Integer.toString(userid), download_url, sign)){
            return SetError(-1, "create app sign failed");
        }
        download_url += "?sign="+sign;
        return Download(download_url, fileName);
    }
    
    /**
    Download        下载图片（直接提供url的方式，如果启动防盗链，需要提前自己添加sign）
        @param  download_url    下载url
        @param  fileName        下载图片的保存路径
        @return 错误码，0为成功
    */
    public int Download(String download_url, String fileName){
        if("".equals(fileName)){
            return SetError(-1, "file name is empty.");
        }
        String rsp = "";
        try{
            URL realUrl = new URL(download_url);
            HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
            //set header
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Host", "web.image.myqcloud.com");
            connection.setRequestProperty("user-agent","qcloud-java-sdk");
		
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
            return SetError(-1, "url exception, e="+e.toString());
        }
        
        return SetError(0, "success");
}

};
	
