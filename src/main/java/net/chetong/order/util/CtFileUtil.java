package net.chetong.order.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/**
 * file 操作工具类
 * @author wufj@chetong.net
 *         2015年12月9日 上午11:18:45
 */
public class CtFileUtil {
	private static Logger log = LogManager.getLogger(CtFileUtil.class);
	private static ExecutorService exe = new ThreadPoolExecutor(Config.DOWNLOAD_ZIP_COUNT, Config.DOWNLOAD_ZIP_COUNT, 12L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(Config.DOWNLOAD_ZIP_COUNT+5),new ThreadPoolExecutor.DiscardPolicy());
	
	/**
	 * 压缩文件
	 * @author wufj@chetong.net
	 *         2015年12月9日 下午12:39:25
	 * @param sourceName 源文件路径
	 * @param targetName 目标文件路径
	 */
	public static void zip(final String sourceName, final String targetName) {
		try {
			Future<Integer> submit = exe.submit(new ZipClass(targetName, sourceName));
			int activeCount = ((ThreadPoolExecutor)exe).getActiveCount();
			log.info("下载压缩线程池中活动线程数"+activeCount);
			//必须用submit才能阻塞
			if(submit!=null&&submit.get()!=1){
				throw ProcessCodeEnum.FAIL.buildProcessException("下载等待压缩线程结束出错");
			}
		} catch (Exception e) {
			log.error("下载等待压缩线程结束出错",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("下载等待压缩线程结束出错");
		}
	}
	
	public static class ZipClass implements Callable<Integer>{
		
		private String targetName;
		private String sourceName;

		public ZipClass(String targetName, String sourceName) {
			this.targetName = targetName;
			this.sourceName = sourceName;
		}
		
		@Override
		public Integer call() throws Exception {
			zip(targetName, new File(sourceName));
			return 1;
		}
		
		/**
		 * 压缩文件
		 * @author wufj@chetong.net
		 *         2015年12月9日 下午12:39:34
		 * @param targetName 目标文件路径
		 * @param inputFile  源文件
		 */
		private static void zip(String targetName, File inputFile) {
			try {
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(targetName));
				out.setEncoding("gbk");
				out.setLevel(Config.DOWNLOAD_ZIP_LEVEL);
				zip(out, inputFile, "");
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * 压缩文件
		 * @author wufj@chetong.net
		 *         2015年12月9日 下午12:41:15
		 * @param out
		 * @param f
		 * @param base
		 */
		private static void zip(ZipOutputStream out, File f, String base) {
			try {
				if (f.isDirectory()) {
					File[] files = f.listFiles();
					out.putNextEntry(new ZipEntry(base + "/"));
					base = base.length() == 0 ? "" : base + "/";
	                if(files==null){return;}
					for (int i = 0; i < files.length; i++) {
						zip(out, files[i], base + files[i].getName());
					}
				} else {
					out.putNextEntry(new ZipEntry(base));
					FileInputStream in = new FileInputStream(f);
					byte[] buffer = new byte[1024];
					int b;
					while ((b = in.read(buffer)) != -1) {
						out.write(buffer,0,b);
					}
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 压缩文件
	 * @author wufj@chetong.net
	 *         2015年12月9日 下午12:39:34
	 * @param targetName 目标文件路径
	 * @param inputFile  源文件
	 */
	private static void zip(String targetName, File inputFile) {
		try {
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(targetName));
			out.setEncoding("gbk");
			zip(out, inputFile, "");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 压缩文件
	 * @author wufj@chetong.net
	 *         2015年12月9日 下午12:41:15
	 * @param out
	 * @param f
	 * @param base
	 */
	private static void zip(ZipOutputStream out, File f, String base) {
		try {
			if (f.isDirectory()) {
				File[] files = f.listFiles();
				out.putNextEntry(new ZipEntry(base + "/"));
				base = base.length() == 0 ? "" : base + "/";
                if(files==null){return;}
				for (int i = 0; i < files.length; i++) {
					zip(out, files[i], base + files[i].getName());
				}
			} else {
				out.putNextEntry(new ZipEntry(base));
				FileInputStream in = new FileInputStream(f);
				int b;
				while ((b = in.read()) != -1) {
					out.write(b);
				}
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 复制文件
	 * @author wufj@chetong.net
	 *         2015年12月9日 下午12:41:41
	 * @param sourceFile
	 * @param targetFile
	 */
	public static void copyFile(File sourceFile, File targetFile){
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		}catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                // 关闭流
                if (inBuff != null)
                    inBuff.close();
                if (outBuff != null)
                    outBuff.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
		}
	}
	
	/**
	 * 删除目录
	 * @author wufj@chetong.net
	 *         2015年12月9日 下午12:41:56
	 * @param dir
	 */
	public static void delDir(File dir) {
		if(dir==null)return;
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if(children.length==0)dir.delete();
            for (int i=0; i<children.length; i++) {
            	File temp=new File(dir, children[i]);
            	if(temp.isDirectory()){
            		delDir(temp);
            	}else{
            		temp.delete();
            	}
            	temp.delete();
            }
        }
        dir.delete();
    }
	
	/**
	 * 导出文件
	 * @author wufj@chetong.net
	 *         2015年12月14日 上午10:13:14
	 * @param response
	 * @param file
	 * @param isDel
	 * @throws IOException
	 */
	public static void exportFile(HttpServletResponse response, File file,boolean isDel)
	        throws IOException{
	        OutputStream out=null;
	        InputStream in=null;
	        //获取文件名
	        String filename=URLEncoder.encode(file.getName(),"UTF-8");
	        response.setContentType("application/force-download");
	        response.setHeader("Location",filename);
	        response.setHeader("Content-Disposition","attachment;filename="+filename);
	        out=response.getOutputStream();
	        in =new FileInputStream(file.getPath());

	        byte[] buffer=new byte[1024];
	        int i=-1;
	        while((i=in.read(buffer))!=-1){
	            out.write(buffer,0,i);
	        }
	        in.close();
	        out.close();
	        if(isDel){
	            file.delete();
	        }
	    }

	   /**
	    * 判断目录是否存在，否在创建
	    * @author wufj@chetong.net
	    *         2015年12月14日 上午10:13:28
	    * @param path
	    */
	    public static void isDir(String path){
	        File dirPath=new File(path);
	        if(!dirPath.exists()){
	            dirPath.mkdirs();
	        }
	    }
	    
		/** 
	     * zip压缩文件 
	     * @param dir 
	     * @param zippath 
	     */  
	    public static void compressToZip(String dir ,String zippath){  
	        List<String> paths = getFiles(dir);   
	        compressFilesZip(paths.toArray(new String[paths.size()]),zippath,dir);  
	    }  
	    /** 
	     * 递归取到当前目录所有文件 
	     * @param dir 
	     * @return 
	     */  
	    public static List<String> getFiles(String dir){  
	        List<String> lstFiles = null;       
	        if(lstFiles == null){  
	            lstFiles = new ArrayList<String>();  
	        }  
	        File file = new File(dir);  
	        File [] files = file.listFiles();   
	        for(File f : files){  
	            if(f.isDirectory()){  
	                lstFiles.add(f.getAbsolutePath());  
	                lstFiles.addAll(getFiles(f.getAbsolutePath()));  
	            }else{   
	                String str =f.getAbsolutePath();  
	                lstFiles.add(str);  
	            }  
	        }  
	        return lstFiles;  
	    }  
		public static String getFilePathName(String dir,String path){
			String  p=path.replace(dir+File.separator,"");
			p=p.replace("\\", "/");
			return p;
		}
		
		public static void compressFilesZip(String[] files,String zipFilePath,String dir){
			if(null == files || files.length <= 0){
				return ;
			}
			ZipArchiveOutputStream zaos = null;
			try{
				File zipFile = new File(zipFilePath);
				zaos = new ZipArchiveOutputStream(zipFile);
				zaos.setUseZip64(Zip64Mode.AsNeeded);
				for(String strFile : files){
					File file = new File(strFile);
					if(null != file){
						String name = getFilePathName(dir,strFile);
						ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file, name);
						zaos.putArchiveEntry(zipArchiveEntry);
						if(file.isDirectory()){
							continue;
						}
						InputStream is = null;
						try{
							is = new BufferedInputStream(new FileInputStream(file));
							byte[] buffer = new byte[1024];
							int len = -1;
							while((len = is.read(buffer)) != -1){
								zaos.write(buffer, 0, len);
							}
							zaos.closeArchiveEntry();
						}catch(Exception e){
							throw new RuntimeException(e);
						}finally {
							if(is != null){
								is.close();
							}
						}
					}
				}
				zaos.finish();
			}catch(Exception e){
				 throw new RuntimeException(e);  
			}finally {
				 try {  
	                 if(zaos != null) {  
	                     zaos.close();  
	                 }  
	             } catch (IOException e) {  
	                 throw new RuntimeException(e);  
	             }  
			}
			
			
		}
	    
}
