/**
 * Copyright (c) 2008, 神州数码金程（北京）科技有限公司 
 * All rights reserved.
 * 
 * 文件名称：PHPMd5.java
 * 摘   要：
 * 版   本：
 * 作   者：朱延超
 * 创建日期：2012-3-15
 */
package net.chetong.order.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class PHPMd5 {
	private static PHPMd5 instance = null;

	private static MessageDigest md5 =null;
	private PHPMd5() {
	};

	public static PHPMd5 getInstance() {
		try {
			if (instance == null) {
				instance = new PHPMd5();
			}
			if(md5==null) {
				md5=MessageDigest.getInstance("MD5");
			}
		} catch (Exception e) {
			return null;
		}
		return instance;
	}

	public String getStringMd5(String source) {
		String hash = null;
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(source
					.getBytes("UTF-8"));
			hash = getStreamMd5(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hash;
	}

	public String getFileMd5(String file) {
		String hash = null;
		try {
			FileInputStream in = new FileInputStream(file);
			hash = getStreamMd5(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hash;
	}

	public String getStreamMd5(InputStream stream) {
		String hash = null;
		byte[] buffer = new byte[1024];
		BufferedInputStream in = null;
		try {
			
			in = new BufferedInputStream(stream);
			int numRead = 0;
			while ((numRead = in.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			in.close();
			hash = toHexString(md5.digest());
		} catch (Exception e) {
			if (in != null)
				try {
					in.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			e.printStackTrace();
		}
		return hash;
	}

	private String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
			sb.append(hexChar[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	private char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static void main(String[] args) {
		System.out.println(PHPMd5.getInstance().getStringMd5("20140805101chetong"));
//		System.out.println(PHPMd5.getInstance().getStringMd5("prince_iceberg_hot_great_wall_ba"));
		
//		System.out.println(PHPMd5.getInstance().getStringMd5("admin12345admin12345ZZ$min12345admin12345admiTT345admin12345admin12345adminMM345admiXX2345admiQQ2345"));
		// System.out.println(PHPMd5.getInstance()
		// .getFileMd5("E:\\software\\2003_Visio_Pro.iso"));
	}
}
