package com.tencentpic.fhpic.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.tencentpic.ImageUtils;

public class CompressPic {

	/*******************************************************************************
	 * 缩略图类（通用） 本java类能将jpg、bmp、png、gif图片文件，进行等比或非等比的大小转换。 具体使用方法
	 * compressPic(大图片路径,生成小图片路径,大图片文件名,生成小图片文名,生成小图片宽度,生成小图片高度,是否等比缩放(默认为true))
	 */

	// 图片处理
	public File compressPic(MultipartFile file,String outputDir,
			String outputFileName, int width, int height,
			boolean gp) {
		try {
		    
			//Image img = ImageIO.read(file.getInputStream());
		    SimpleDateFormat sdf = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    String text = "";
		    Metadata metadata = JpegMetadataReader.readMetadata(file.getInputStream());
		    if(null == metadata){
		        text = sdf.format(new Date());
		    }else{
		        Directory exif = metadata.getDirectory(ExifIFD0Directory.class);
		        if(null == exif){
		            text = sdf.format(new Date());
		        }else{
		            text = exif.getString(ExifIFD0Directory.TAG_DATETIME); 
		            if(StringUtils.isBlank(text)){
		                text = sdf.format(new Date());
		            }else{
		                SimpleDateFormat sf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
		                text = sdf.format(sf.parse(text));
		            }
		        }
		    }
		    
		   
		    
			//添加水印（文字）
			Image img = ImageUtils.pressText(text, file.getInputStream(), 0, 0, 1.0f);
			
			// 判断图片格式是否正确
			if (img.getWidth(null) == -1) {
				System.out.println(" can't read,retry!" + "<BR>");
				return null;
			} else {
				int newWidth;
				int newHeight;
				// 判断是否是等比缩放v
				if (img.getWidth(null) < width){
					newWidth = img.getWidth(null); // 输出的图片宽度
					newHeight = img.getHeight(null); // 输出的图片高度
				}else if(gp == true) {
					// 为等比缩放计算输出的图片宽度及高度
					double rate1 = ((double) img.getWidth(null))
							/ (double) width + 0.1;
					double rate2 = ((double) img.getHeight(null))
							/ (double) height + 0.1;
					// 根据缩放比率大的进行缩放控制
					double rate = rate1 > rate2 ? rate1 : rate2;
					newWidth = (int) (((double) img.getWidth(null)) / rate);
					newHeight = (int) (((double) img.getHeight(null)) / rate);
				} else {
					newWidth = width; // 输出的图片宽度
					newHeight = height; // 输出的图片高度
				}
				BufferedImage tag = new BufferedImage((int) newWidth,
						(int) newHeight, BufferedImage.TYPE_INT_RGB);
				/*
				 * Image.SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
				 */
				tag.getGraphics().drawImage(
						img.getScaledInstance(newWidth, newHeight,
								Image.SCALE_SMOOTH), 0, 0, null);
				File f=new File(outputDir+ outputFileName);
				if (!f.exists()){
					f.createNewFile();
//					f.mkdirs(); 
				}
				FileOutputStream out = new FileOutputStream(outputDir
						+ outputFileName);
				// JPEGImageEncoder可适用于其他图片类型的转换
				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
				encoder.encode(tag);
				out.close();
				return f;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
