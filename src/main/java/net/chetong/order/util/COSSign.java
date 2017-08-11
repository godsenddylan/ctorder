package net.chetong.order.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 腾讯云对象存储服务生成签名
 * @author Administrator
 *
 */
public class COSSign {
	
	/**
	 * 录音文件
	 */
	//生成签名所需信息包括项目ID
	public static final String APP_ID_COS ="10000212";
	//空间名称（bucket,文件资源的组织管理单元）
	public static final String BUCKET_VOICE ="chetongapp";
	//项目的Secret ID
	public static final String SECRET_ID_COS = "AKIDye96R4p9gZO0pJPJC7n3Ve7AhoVVNRKU";
	//项目的Secret key
	public static final String SECRET_KEY_COS="8PTp0JrIRw5rpXTbZyJzKvWc0dBduOtV";
	
	
	/**
	 * 影像文件
	 */
	public static final String APP_ID = "200681";
	public static final String SECRET_ID = "AKIDgAGfaYIEyJEXaCgtztNLiIqkANlBnrQQ";
	public static final String SECRET_KEY = "jpivTlLgJrqVDq02f1h3oLWdabCMEMWW";

	
	/**
	 * 
	 * @param appId
	 * @param bucket
	 * @param secretId
	 * @param expiredTime 签名的有效期是多少秒，单次签名时，必须设置为0,比如1分钟就传入60
	 * @param field 多次签名为空，资源存储的唯一标识，格式为"/appid/bucketname/用户自定义路径或资源名"，并且需要对其中非'/'字符进行urlencode编码
	 * @return
	 */
	public static String getCOSSign(String appId,String bucket,String secretId,String secretKey,long expiredTime,String field){
		Calendar cal =Calendar.getInstance();
		//当前时间秒数
		long currentTime = cal.getTimeInMillis()/1000; 
		//总的有效期
		long expired = currentTime+expiredTime;
		//随机数
		int randomNum = getRandomNum();
		
		return getCOSSign(appId, bucket, secretId,secretKey,expired,currentTime,randomNum, field);
	}
	/**
	 * 
	 * @param appId
	 * @param bucket
	 * @param secretId
	 * @param secretKey
	 * @param expiredTime 签名的有效期持续到多少秒，是时间的所有秒数，比现在时间要大，单次签名时，必须设置为0,
	 * @param fileid
	 * @return
	 */
	public static String getCOSSignByExpiredTime(String appId,String bucket,String secretId,String secretKey,long expiredTime,String fileid){
		Calendar cal =Calendar.getInstance();
		//当前时间秒数
		long currentTime = cal.getTimeInMillis()/1000; 
		//随机数
		int randomNum = getRandomNum();
		return getCOSSign(appId, bucket, secretId,secretKey,expiredTime,currentTime,randomNum, fileid);
	}
	/**
	 * 获取默认的签名
	 * @param expiredTime
	 * @param field
	 * @return
	 */
	public static String getCOSSign(long expiredTime,String field){
		return getCOSSign(APP_ID_COS, BUCKET_VOICE, SECRET_ID_COS, SECRET_KEY_COS, expiredTime, field);
	}
	
	public static String getCOSSign(String appId,String bucket,String secretId,String secretKey,long expiredTime,long currentTime,int randomNum,String field){
		if(null ==appId || appId.equals("") || null==bucket || bucket.equals("") || null == secretId|| secretId.equals("") || null == secretKey || secretKey.equals("")){
			return null;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("a=").append(appId)
		   .append("&b=").append(bucket).append("&k=").append(secretId)
		   .append("&e=").append(expiredTime).append("&t=").append(currentTime)
		   .append("&r=").append(randomNum).append("&f=").append(field);
		try {
			String signStr = signatureEncode(sb.toString(), secretKey);
			return signStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/*
	 * 万象优图鉴权及签名,单次签名
	 */
	public static String getOnceSign(String appId,String bucket,String secretId,String secretKey,String userId,String fileid){
		Calendar cal =Calendar.getInstance();
		//当前时间秒数
		long currentTime = cal.getTimeInMillis()/1000; 
		//随机数
		int randomNum = getRandomNum();
		StringBuffer str = new StringBuffer();
		str.append("a=").append(appId);
		if(null != bucket || !"".equals(bucket)){
			str.append("&b=").append(bucket);
		}
		str.append("&k=").append(secretId).append("&e=0").append("&t=").append(currentTime)
		   .append("&r=").append(randomNum).append("&u=").append(userId).append("&f=").append(fileid);
		try {
			return  signatureEncode(str.toString(), secretKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/*
	 * 获取货运险影像图片单次有效签名
	 */
	public static String getImageOnceSign(String userId,String fileid){
		return getOnceSign(APP_ID,null,SECRET_ID,SECRET_KEY,userId,fileid);
	}
	private static String signatureEncode(String plainText,String secretKey) throws Exception{
		byte[] signTmp =HMACSHA1.getSignature(plainText,secretKey);
		byte[] all=new byte[signTmp.length+plainText.getBytes().length];
		System.arraycopy(signTmp, 0, all, 0, signTmp.length);
		System.arraycopy(plainText.getBytes(), 0, all, signTmp.length, plainText.getBytes().length);
		return Base64Util.encode(all);
	}
	
	private static int getRandomNum(){
		Random random= new Random();
		return random.nextInt(1000000);
	}
	
	
	public static void main(String[] args) {
		long extime = 30*24*60*60;
		String cosSign = getCOSSign(extime, "");
		System.out.println(cosSign);
	}
}

 class HMACSHA1 {
	 
	private static final String HMAC_SHA1 = "HmacSHA1";

	public static byte[] getSignature(String data, String key) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA1");
		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), mac.getAlgorithm());
		mac.init(signingKey);
		return mac.doFinal(data.getBytes());
	}
}
 
 class Base64Util {

		private static final char last2byte = (char) Integer.parseInt("00000011", 2);
		private static final char last4byte = (char) Integer.parseInt("00001111", 2);
		private static final char last6byte = (char) Integer.parseInt("00111111", 2);
		private static final char lead6byte = (char) Integer.parseInt("11111100", 2);
		private static final char lead4byte = (char) Integer.parseInt("11110000", 2);
		private static final char lead2byte = (char) Integer.parseInt("11000000", 2);
		private static final char encodeTable[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
				'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
				'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
				'4', '5', '6', '7', '8', '9', '+', '/' };

		public static String encode(byte from[]) {
			StringBuffer to = new StringBuffer((int) ((double) from.length * 1.3400000000000001D) + 3);
			int num = 0;
			char currentByte = '\0';
			for (int i = 0; i < from.length; i++)
				for (num %= 8; num < 8; num += 6) {
					switch (num) {
					case 1: // '\001'
					case 3: // '\003'
					case 5: // '\005'
					default:
						break;

					case 0: // '\0'
						currentByte = (char) (from[i] & lead6byte);
						currentByte >>>= '\002';
						break;

					case 2: // '\002'
						currentByte = (char) (from[i] & last6byte);
						break;

					case 4: // '\004'
						currentByte = (char) (from[i] & last4byte);
						currentByte <<= '\002';
						if (i + 1 < from.length)
							currentByte |= (from[i + 1] & lead2byte) >>> 6;
						break;

					case 6: // '\006'
						currentByte = (char) (from[i] & last2byte);
						currentByte <<= '\004';
						if (i + 1 < from.length)
							currentByte |= (from[i + 1] & lead4byte) >>> 4;
						break;
					}
					to.append(encodeTable[currentByte]);
				}

			if (to.length() % 4 != 0) {
				for (int i = 4 - to.length() % 4; i > 0; i--)
					to.append("=");

			}
			return to.toString();
		}

	}