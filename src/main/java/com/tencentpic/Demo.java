package com.tencentpic;

import java.io.File;

import com.tencentpic.qcloud.PicCloud;
import com.tencentpic.qcloud.UploadResult;

public class Demo {
	//appid, access id, access key请到http://app.qcloud.com注册
	public static final String APP_ID = "200681";
	public static final String SECRET_ID = "AKIDgAGfaYIEyJEXaCgtztNLiIqkANlBnrQQ";
	public static final String SECRET_KEY = "jpivTlLgJrqVDq02f1h3oLWdabCMEMWW";
	
	public static void main(String[] args) {

		PicCloud pc = new PicCloud(APP_ID, SECRET_ID, SECRET_KEY);
		String userid = "12345";
		String pic = "D:\\test.jpg";		
		String url = "";
		String download_url = "";
		File remoteFile=new File("http://200681.image.myqcloud.com/200681/0/797a19ae-89a4-4b45-a3b6-3df2c7fd82f8/original");
		UploadResult result = new UploadResult();
		int ret = pc.Upload(userid, "http://200681.image.myqcloud.com/200681/0/797a19ae-89a4-4b45-a3b6-3df2c7fd82f8/original", result);
		if(ret == 0){
			System.out.println("upload pic success, url=" + result.url + " download_url="+result.download_url);
		}else{
			System.out.println("upload pic error, error code="+ret);
		}
		
	}
}

