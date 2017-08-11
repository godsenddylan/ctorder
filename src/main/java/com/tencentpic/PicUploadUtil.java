package com.tencentpic;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

import com.tencentpic.qcloud.PicCloud;
import com.tencentpic.qcloud.UploadResult;

public class PicUploadUtil {
	//appid, access id, access key请到http://app.qcloud.com注册
	public static final String APP_ID = "200681";
	public static final String SECRET_ID = "AKIDgAGfaYIEyJEXaCgtztNLiIqkANlBnrQQ";
	public static final String SECRET_KEY = "jpivTlLgJrqVDq02f1h3oLWdabCMEMWW";
	
	public static UploadResult picUpload(String picPath) {

		PicCloud pc = new PicCloud(APP_ID, SECRET_ID, SECRET_KEY);
		String userid = "";
//		String pic = "D:\\test.jpg";		
		String url = "";
		String download_url = "";
		UploadResult result = new UploadResult();
		int ret = pc.Upload(userid, picPath, result);
		if(ret == 0){
			System.out.println("upload pic success, url=" + result.url + " download_url="+result.download_url);
		}else{
			System.out.println("upload pic error, error code="+ret);
		}
		return result;
	}
	
	public static UploadResult picUploadByFile(MultipartFile file) {

		PicCloud pc = new PicCloud(APP_ID, SECRET_ID, SECRET_KEY);
		String userid = "";
//		String pic = "D:\\test.jpg";		
		String url = "";
		String download_url = "";
		UploadResult result = new UploadResult();
		int ret = pc.UploadByFile(userid, file, result);
		if(ret == 0){
			System.out.println("upload pic success, url=" + result.url + " download_url="+result.download_url);
		}else{
			System.out.println("upload pic error, error code="+ret);
		}
		return result;
	}
	
	public static UploadResult picUploadByOrgFile(File file) {

		PicCloud pc = new PicCloud(APP_ID, SECRET_ID, SECRET_KEY);
		String userid = "";
//		String pic = "D:\\test.jpg";		
		String url = "";
		String download_url = "";
		UploadResult result = new UploadResult();
		int ret = pc.UploadByFile(userid, file, result);
		if(ret == 0){
			System.out.println("upload pic success, url=" + result.url + " download_url="+result.download_url);
		}else{
			System.out.println("upload pic error, error code="+ret);
		}
		return result;
	}
}

