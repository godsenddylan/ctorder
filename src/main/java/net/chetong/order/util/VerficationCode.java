package net.chetong.order.util;

import java.util.Random;

/**
 * 验证码
 * @author GoldenVista
 *
 */
public class VerficationCode {
	final static String[] STR_ARRAY = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
	
	public static String getVerficationCode(int n){
		String randStr = "";
		int index = 0;
		
		Random rand = new Random();
		
		for (int i = 0; i < n; i++) {
			index = rand.nextInt(STR_ARRAY.length - 1);
			randStr += STR_ARRAY[index];
		}
		
		return randStr;
	}
}