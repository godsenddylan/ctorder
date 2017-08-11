package com.tencentpic;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.lang.StringUtils;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

public class ImageUtils {

    /**
     * 给图片添加水印、可设置水印图片旋转角度
     * 
     * @param iconPath
     *            水印图片路径
     * @param srcImgPath
     *            源图片路径
     * @param targerPath
     *            目标图片路径
     * @param degree
     *            水印图片旋转角度
     */
    public static void markImageByIcon(String iconPath, String srcImgPath,
            String targerPath, Integer degree) {
        OutputStream os = null;
        try {
            Image srcImg = ImageIO.read(new File(srcImgPath));
            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null),
                    srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
            
            // 得到画笔对象
            // Graphics g= buffImg.getGraphics();
            Graphics2D g = buffImg.createGraphics();

            // 设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g.drawImage(
                    srcImg.getScaledInstance(srcImg.getWidth(null),
                            srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0,
                    null);

            if (null != degree) {
                // 设置水印旋转
                g.rotate(Math.toRadians(degree),
                        (double) buffImg.getWidth() / 2,
                        (double) buffImg.getHeight() / 2);
            }
            // 水印图象的路径 水印一般为gif或者png的，这样可设置透明度
            ImageIcon imgIcon = new ImageIcon(iconPath);
            // 得到Image对象。
            Image img = imgIcon.getImage();
            float alpha = 0.2f; // 透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));
            // 表示水印图片的位置
            g.drawImage(img, 150, 300, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            g.dispose();
            os = new FileOutputStream(targerPath);
            // 生成图片
            ImageIO.write(buffImg, "JPG", os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * 给图片添加文字水印
     * 
     * @param pressText 水印文字
     * 
     * @param srcImageFile 源图像地址
     * 
     * @param destImageFile 目标图像地址
     * 
     * @param x 修正值
     * 
     * @param y 修正值
     * 
     * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressText(String pressText, String srcImageFile,
            String destImageFile, int x, int y, float alpha) {
        try {
            File img = new File(srcImageFile);
            Image src = ImageIO.read(img);
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            
            Graphics2D g = image.createGraphics();
            // 开文字抗锯齿 去文字毛刺
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.drawImage(src, 0, 0, width, height, null);
            // 设置颜色
            g.setColor(new Color(89, 87, 87));
            // 设置 Font
            g.setFont(new Font("方正兰亭中黑_GBK", Font.BOLD, 20));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));
            // 第一参数->设置的内容，后面两个参数->文字在图片上的坐标位置(x,y) .
            g.drawString(pressText, x, y);
            g.dispose();
            ImageIO.write((BufferedImage) image, "JPEG",
                    new File(destImageFile));// 输出到文件流
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /*
     * 给图片添加文字水印
     * 
     * @param pressText 水印文字
     * 
     * @param in 源图像流
     * 
     * @param x 修正值
     * 
     * @param y 修正值
     * 
     * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static Image pressText(String pressText, InputStream in, int x, int y, float alpha) {
        try {
            
            Image src = ImageIO.read(in);
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            
            if(0==x&&0==y){
                x = width - width/3;
                y = height - height/30;
            }
            
            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            
            Graphics2D g = image.createGraphics();
            // 开文字抗锯齿 去文字毛刺
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.drawImage(src, 0, 0, width, height, null);
            // 设置颜色
            g.setColor(new Color(255, 0, 0));
            // 设置 Font
            g.setFont(new Font("方正兰亭中黑_GBK", Font.BOLD, height/30));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));
            // 第一参数->设置的内容，后面两个参数->文字在图片上的坐标位置(x,y) .
            g.drawString(pressText, x, y);
            g.dispose();
            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 
     * @Description: 获取拍照日期
     * @param in
     * @return
     * @return String
     * @author zhouchushu
     * @date 2015年11月25日 下午4:37:01
     */
    public static String getImageTime(InputStream in){
        try {
            String text = "";
            Metadata metadata = JpegMetadataReader.readMetadata(in);
            SimpleDateFormat sdf = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
            
           
            return text;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }

}
