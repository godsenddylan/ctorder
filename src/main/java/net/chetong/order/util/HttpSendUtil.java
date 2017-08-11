package net.chetong.order.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import com.estar.app.appsrvyycbx.domain.DataVO;
import com.estar.app.appsrvyycbx.domain.DirFitInfoVO;
import com.estar.app.appsrvyycbx.domain.PdaCarTypeQueryVO;
import com.estar.app.appsrvyycbx.domain.PdaFitListQueryVO;
import com.estar.app.appsrvyycbx.domain.ReturnDirFitInfoVO;
import com.estar.app.appsrvyycbx.domain.ReturnFitInfoVO;
import com.estar.edp.utils.XMLBean;
import com.estar.edp.utils.XmlTools;
import com.thoughtworks.xstream.XStream;

public class HttpSendUtil {
	protected static Logger log = LogManager.getLogger(HttpSendUtil.class);
	/**
	 * 发送xml数据请求到server端
	 * 
	 * @param url
	 *            xml请求数据地址
	 * @param xmlString
	 *            发送的xml数据流
	 * @return null发送失败，否则返回响应内容
	 */
	@SuppressWarnings("deprecation")
	public static String postByXml(String url,String xmlString) {

		// 创建httpclient工具对象
		HttpClient client = new HttpClient();
		// 创建post请求方法
		PostMethod myPost = new PostMethod(url);
		// 设置请求超时时间
		client.setConnectionTimeout(60 * 1000);
		String responseString = null;
		try {
			// 设置请求头部类型
			myPost.setRequestHeader("Content-Type", "text/xml");
			myPost.setRequestHeader("charset", "GBK");

			// 设置请求体，即xml文本内容，注：这里写了两种方式，一种是直接获取xml内容字符串，一种是读取xml文件以流的形式
			myPost.setRequestBody(xmlString);
			
			myPost.setRequestEntity(new StringRequestEntity(xmlString, "text/xml", "GBK"));
			int statusCode = client.executeMethod(myPost);
			if (statusCode == HttpStatus.SC_OK) {
				BufferedInputStream bis = new BufferedInputStream(myPost.getResponseBodyAsStream());
				byte[] bytes = new byte[1024];
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				int count = 0;
				while ((count = bis.read(bytes)) != -1) {
					bos.write(bytes, 0, count);
				}
				byte[] strByte = bos.toByteArray();
				responseString = new String(strByte, 0, strByte.length, "GBK");
				bos.close();
				bis.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		myPost.releaseConnection();
		return responseString;
	}
	
	public static XMLBean sendData(String url,XMLBean bean,String encoding) throws IOException,HttpException,DocumentException{

		HttpClient http = new HttpClient();

		PostMethod post = new PostMethod(url);

		XmlUtils xmlutils = new XmlUtils();
		//System.out.println("-------------------request----------------------");
		String xml = xmlutils.createDocument(bean,encoding).asXML();
		//String xml = StringUtils.convert(xmlutils.createDocument(bean).asXML(),"GB2312","ISO8859_1");
		post.setRequestHeader("Content-type", "text/xml; charset="+encoding);

		post.setRequestEntity(new StringRequestEntity(xml));
		http.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		if (http.executeMethod(post) == HttpStatus.SC_OK){
			SAXReader reader = new SAXReader();

			Document document = null;

			if(encoding.equals(XmlTools.Encoding_GBK)){
				Charset charset = Charset.forName("GBK");
				document = reader.read(new InputStreamReader(post.getResponseBodyAsStream(), charset));
			}else{
				document = reader.read(post.getResponseBodyAsStream());
			}
			XmlUtils xmlUtils = new XmlUtils();
			XMLBean xmlbean = xmlUtils.parse(document);

			return xmlbean;
		}else{
			return null;
		} 
	}
	public static XMLBean sendData(String url,String xml,String encoding) throws IOException,HttpException,DocumentException{
		HttpClient http = new HttpClient();

		PostMethod post = new PostMethod(url);

		post.setRequestEntity(new StringRequestEntity(xml));
		if (http.executeMethod(post) == HttpStatus.SC_OK){
			SAXReader reader = new SAXReader();

			Document document = null;

			if(encoding.equals(XmlTools.Encoding_GBK)){
				Charset charset = Charset.forName("GBK");
				document = reader.read(new InputStreamReader(post.getResponseBodyAsStream(), charset));

			}else{
				document = reader.read(post.getResponseBodyAsStream());
			}
			XmlUtils xmlUtils = new XmlUtils();
			XMLBean xmlbean = xmlUtils.parse(document);

			return xmlbean;
		}else{
			return null;
		} 
	}
	
	public static XMLBean sendData(String url,XMLBean bean) throws IOException,HttpException,DocumentException{

		return sendData(url,bean,XmlTools.Encoding_GB2312);
	}
	
	public static ResultVO<String> imgSend(String urlPath,Map<String,String> imgMap){
		ResultVO<String> rstVO = new ResultVO<String>();
		try {
			URL url = new URL(urlPath);  
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
	        conn.setDoOutput(true);  
	        conn.setDoInput(true);  
	        conn.setUseCaches(false);  
	        conn.setRequestMethod("POST");  
	        //conn.setRequestProperty("connection", "Keep-Alive");  
	        //conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");  
	        conn.setRequestProperty("Charsert", "GB2312");   
	        conn.setRequestProperty("Content-Type", "multipart/form-data;");  
	        conn.setRequestProperty("claimNo", imgMap.get("claimNo"));  
	        conn.setRequestProperty("taskId", imgMap.get("taskId"));  
	        conn.setRequestProperty("compCde", imgMap.get("compCde"));  
	        conn.setRequestProperty("picCls", imgMap.get("picCls"));  
	        conn.setRequestProperty("picDtl", imgMap.get("picDtl"));  
	        conn.setRequestProperty("picRemark", imgMap.get("picRemark"));  
	        conn.setRequestProperty("longitude", imgMap.get("longitude"));  
	        conn.setRequestProperty("latitude", imgMap.get("latitude"));  
	        conn.setRequestProperty("taskType", imgMap.get("taskType"));  
	        conn.setRequestProperty("fileName", imgMap.get("fileName"));  
	        conn.setRequestProperty("requestMark", imgMap.get("requestMark"));  
	        conn.setRequestProperty("dptCde", imgMap.get("dptCde")); 
	        conn.setRequestProperty("dptNme", URLEncoder.encode(imgMap.get("dptNme"), "UTF-8"));  
	        conn.setRequestProperty("empCde", imgMap.get("empCde"));  
	        conn.setRequestProperty("empNme", URLEncoder.encode( imgMap.get("empNme"), "UTF-8"));  
	        conn.setRequestProperty("oprtm", imgMap.get("oprtm"));  
	        conn.setRequestProperty("rows", imgMap.get("rows"));  
	        OutputStream out = new DataOutputStream(conn.getOutputStream());  
	        
	        URL fileUrl = new URL(imgMap.get("link"));  
	        HttpURLConnection fileConn = (HttpURLConnection) fileUrl.openConnection();  
//	        fileConn.setRequestProperty("Host", "web.image.myqcloud.com");
//	        fileConn.setRequestProperty("user-agent","qcloud-java-sdk");
	        fileConn.setDoOutput(true);
	        fileConn.setDoInput(true);
	        fileConn.connect();
	        DataInputStream in = new DataInputStream(fileConn.getInputStream()); 
	        int bytes = 0;  
            byte[] bufferOut = new byte[1024];  
            while ((bytes = in.read(bufferOut)) != -1) {  
                out.write(bufferOut, 0, bytes);  
            }  
            in.close();  
            out.flush();    
            out.close();   
          // 定义BufferedReader输入流来读取URL的响应  
            InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "GB2312");
			char[] bt = new char[1024];
			String countTaskStr = "";
			int len;
			while ((len = isr.read(bt)) != -1) {
				countTaskStr += new String(bt, 0, len);
			}
			isr.close();
			log.info("上传永诚图片返回结果："+countTaskStr);
			if(countTaskStr!=null&&!"".equals(countTaskStr)){
				DataVO rstDvo = null;
				XStream x = new XStream();
				try {
					rstDvo = (DataVO)x.fromXML(countTaskStr);
				} catch (Exception e) {
					log.error("上传永诚图片发送异常：",e);
					rstVO.setResultCode(Constants.ERROR);
					rstVO.setResultMsg("图片上传失败！");
					rstVO.setResultObject("返回结果XML转DataVO对象失败:"+countTaskStr);
					return rstVO;
				}
				if(rstDvo!=null&&"1".equals(rstDvo.getResultCde())){
					rstVO.setResultCode(Constants.SUCCESS);
					rstVO.setResultMsg("图片上传成功");
					rstVO.setResultObject(countTaskStr);
					return rstVO;
				}else{
					rstVO.setResultCode(Constants.ERROR);
					rstVO.setResultMsg("图片上传失败！");
					rstVO.setResultObject("返回结果为:"+countTaskStr);
					return rstVO;
				}
			}else{
				rstVO.setResultCode(Constants.ERROR);
				rstVO.setResultMsg("图片上传失败！");
				rstVO.setResultObject("返回结果为NULL:"+countTaskStr);
				return rstVO;
			}
		} catch (Exception e) {
			log.error("上传永诚图片发送异常2：",e);
			rstVO.setResultCode(Constants.ERROR);
			rstVO.setResultMsg("图片上传失败！");
			rstVO.setResultObject("上传异常:"+e.getMessage());
			return rstVO;
		}
	}
		
//	public static void main(String[] arge){
//		//test_4();
//		
//		String str ="<com.estar.app.appsrvyycbx.domain.DataVO>"+
//  "<resultCde>1</resultCde>"+
//  "<message>接口端图片保存成功</message>"+
//  "<list>"+
//   " <com.estar.app.appsrvyycbx.domain.ReturnMsgVO>"+
//   "   <code>1</code>"+
//     " <message>接口端图片保存成功</message>"+
//   " </com.estar.app.appsrvyycbx.domain.ReturnMsgVO>"+
//  "</list>"+
//"</com.estar.app.appsrvyycbx.domain.DataVO>";
//		
//		XStream x = new XStream();
//		try {
//			DataVO	rstDvo = (DataVO)x.fromXML(str);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	public static void test_4() {
//        String url = "http://222.66.48.162:10001/newpdaclaim/android"; 
		try {
//			DataVO dvo = new DataVO();
			URL url = new URL("http://222.66.48.162:10001/newpdaclaim/android");  
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
	        conn.setDoOutput(true);  
	        conn.setDoInput(true);  
	        conn.setUseCaches(false);  
	        conn.setRequestMethod("POST");  
	        //conn.setRequestProperty("connection", "Keep-Alive");  
	        //conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");  
	        conn.setRequestProperty("Charsert", "GB2312");   
	        conn.setRequestProperty("Content-Type", "multipart/form-data;");  
	        conn.setRequestProperty("claimNo", "4090003002016000107");  
	        conn.setRequestProperty("taskId", "50058153");  
	        conn.setRequestProperty("compCde", "P1001");  
	        conn.setRequestProperty("picCls", "13701");  
	        conn.setRequestProperty("picDtl", "1370101");  
	        conn.setRequestProperty("picRemark", "CT");  
	        conn.setRequestProperty("longitude", "");  
	        conn.setRequestProperty("latitude", "");  
	        conn.setRequestProperty("taskType", "0170002");  
	        conn.setRequestProperty("fileName", "chetongTest.jpg");  
	        conn.setRequestProperty("requestMark", "picUpload");  
	        conn.setRequestProperty("dptCde", ""); 
	        conn.setRequestProperty("dptNme", "");  
	        conn.setRequestProperty("empCde", "107000085");  
	        conn.setRequestProperty("empNme", "陈冬丽");  
	        conn.setRequestProperty("oprtm", "2016-07-22 13:55:08");  
	        conn.setRequestProperty("rows", "2");  
	        OutputStream out = new DataOutputStream(conn.getOutputStream());  
	        URL fileUrl = new URL("http://200681.image.myqcloud.com/200681/0/9d16ed5b-cc2c-4887-8331-20fd2f0d98af/original");  
	        HttpURLConnection fileConn = (HttpURLConnection) fileUrl.openConnection();  
	        fileConn.setRequestMethod("GET");
//	        fileConn.setRequestProperty("Host", "web.image.myqcloud.com");
//	        fileConn.setRequestProperty("user-agent","qcloud-java-sdk");
	        fileConn.setDoOutput(true);
	        fileConn.setDoInput(true);
	        fileConn.connect();
	        DataInputStream in = new DataInputStream(fileConn.getInputStream()); 
	        int bytes = 0;  
            byte[] bufferOut = new byte[1024];  
            while ((bytes = in.read(bufferOut)) != -1) {  
                out.write(bufferOut, 0, bytes);  
            }
            in.close();  
            out.flush();    
            out.close();   
          // 定义BufferedReader输入流来读取URL的响应  
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"GB2312"));  
            String line = null;  
            while ((line = reader.readLine()) != null) {  
                System.out.println(line);  
            }  
		} catch (Exception e) {
			System.out.println("发送POST请求出现异常！" + e);  
			e.printStackTrace();
		}
    }
	
	 /** 
     * @param args 
     */ 
    public static void test_1() {
        String url = "http://zxlptkf.vicp.cc:49611/newpdaclaim/android"; 
		try {
			DataVO dvo = new DataVO();
    		PdaCarTypeQueryVO pdaCarQryVO = new PdaCarTypeQueryVO();
    		pdaCarQryVO.setComDptID("P1001");
    		pdaCarQryVO.setDataFrm("1");
    		pdaCarQryVO.setDptID("07");
    		pdaCarQryVO.setQryInfo("宝马BMW X6 35i越野车");
    		pdaCarQryVO.setQryType("0");
    		pdaCarQryVO.setUserID("107000085");
    		List<Object> list = new ArrayList<Object>();
    		list.add(pdaCarQryVO);
    		dvo.setMethod("pdaCarTypeQuery");
    		dvo.setAction("pAndroidCarTypeQuery");
    		dvo.setList(list);
    		XStream x = new XStream();
    		String xmlString = x.toXML(dvo);
    		System.out.println(xmlString);
    		System.out.println("====================================================");
			String rstXmlStr = postByXml(url,xmlString);
			System.out.println(rstXmlStr);
			DataVO rstDvo = (DataVO)x.fromXML(rstXmlStr);
			System.out.println(rstDvo);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }  
    
    @SuppressWarnings("rawtypes")
	public static void test_2(){
    	String url = "http://10.1.84.10:7001/pjbj/SendPartInterfaceServlet";
    	try{
    		
    		XMLBean root=new XMLBean("root");
			XMLBean head=new XMLBean("head");
			head.setAttribute("function", "qPDADirFitList");
			head.setAttribute("method", "qDirFitList");
			head.setAttribute("sercode","F3D1D35B334BAB3B09D48997D4015D9F34F13CBA84E62CD761749393378DA2F0");
			head.setAttribute("reqPage", "10");
			root.elementPut(head);
			
			XMLBean QueryInfo =new XMLBean("QueryInfo");
			QueryInfo.setAttribute("comDptID","P1001");//保险公司代码
			QueryInfo.setAttribute("fitCarId","BMAAQI0001");//车型代码"
			QueryInfo.setAttribute("fitArea","");//价格区域代码
			QueryInfo.setAttribute("userID","107000085");//查勘员代码
			QueryInfo.setAttribute("dptID","07");//查勘员机构代码
			QueryInfo.setAttribute("dataFrm","1");
			root.elementPut(QueryInfo);
			XMLBean xml = sendData(url,root);
			ReturnDirFitInfoVO returnDirFitInfoVO=new ReturnDirFitInfoVO();
			if(null != xml){
				XmlUtils xmlUtils=new XmlUtils();
				System.out.println(xmlUtils.parse(xml));
				XMLBean result =xml.getElement("result");
				returnDirFitInfoVO.setCode(result.getAttribute("code"));
				returnDirFitInfoVO.setMessage(result.getAttribute("message"));
				
				XMLBean QueryList=xml.getElement("QueryList");
				int rows=0;
				List list=null;
				if (QueryList!=null) {
					rows=QueryList.getInt("rows");
					list=QueryList.getListElement();
				}
				List<DirFitInfoVO> dirFitInfoVOList=new ArrayList<DirFitInfoVO>();
				for (int i = 0; i <rows; i++) {
					XMLBean listBean=(XMLBean)list.get(i);
					DirFitInfoVO dirFitInfoVO=new DirFitInfoVO();
					dirFitInfoVO.setFtnCde2(listBean.getAttribute("ftnCde2"));
					dirFitInfoVO.setFtnNme(listBean.getAttribute("ftnNme"));
					dirFitInfoVOList.add(dirFitInfoVO);
				}
				
				returnDirFitInfoVO.setDirFitInfoVOList(dirFitInfoVOList);
			}
    	}catch(Exception e ){
    		e.printStackTrace();
    	}
    }
    
    public static void test_3(){
    	String url = "http://10.1.84.10:7001/pjbj/SendPartDetailInterfaceServlet";
    	try{
    		PdaFitListQueryVO queryVO = new PdaFitListQueryVO();
    		queryVO.setComDptID("");
    		queryVO.setFitCarId("");
    		queryVO.setFitArea("");
    		queryVO.setParented("");
    		queryVO.setUserID("");
    		queryVO.setDptID("");
    		queryVO.setDataFrm("");
    		
    		XStream x = new XStream();
    		String xmlString = x.toXML(queryVO);
    		System.out.println(xmlString);
    		System.out.println("====================================================");
			String rstXmlStr = postByXml(url,xmlString);
			System.out.println(rstXmlStr);
			ReturnFitInfoVO rstDvo = (ReturnFitInfoVO)x.fromXML(rstXmlStr);
			System.out.println(rstDvo);
    	}catch(Exception e ){
    		e.printStackTrace();
    	}
    }
}
