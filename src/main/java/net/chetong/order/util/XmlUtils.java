package net.chetong.order.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import com.estar.edp.utils.ObjectUtils;
import com.estar.edp.utils.XMLBean;
import com.estar.edp.utils.XmlTools;

public class XmlUtils {
	
	public static HashMap<String,String> listMap = new HashMap<String,String>();  //XML里面所用到的列表标签名
	
	static{
		listMap.put("list", "list");
		listMap.put("THIRD_VEHICLE_LIST", "THIRD_VEHICLE_LIST");
		listMap.put("CLAIM_LIST", "CLAIM_LIST");
		listMap.put("CLAIM_COVER_LIST", "CLAIM_COVER_LIST");
		listMap.put("DETAIL_LIST", "DETAIL_LIST");
		listMap.put("VEHICLE_EXPENSE_LIST", "VEHICLE_EXPENSE_LIST");//商业险车辆损失情况
		listMap.put("OBJECT_EXPENSE_LIST", "OBJECT_EXPENSE_LIST");//商业险物损列表
		listMap.put("PERSON_EXPENSE_LIST", "PERSON_EXPENSE_LIST");//商业险人员损失列表
		listMap.put("PRODUCT_CLAIM_LIST", "PRODUCT_CLAIM_LIST");//商业险赔偿情况
		listMap.put("A_CLAIM_LIST", "A_CLAIM_LIST");
		listMap.put("B_CLAIM_LIST", "B_CLAIM_LIST");
		listMap.put("SelectedFittingsList", "SelectedFittingsList");
		listMap.put("ReturnResultFittings", "ReturnResultFittings");
	}
	
	public XMLBean parse(Document doc){
		Element root = doc.getRootElement();
		
		XMLBean xmlbean = new XMLBean();
		
		xmlbean.setRoot(true);
		
		parseAttribute(root,xmlbean);
    	
		return xmlbean;
	}
	
	/**指定String转换成Document
	 * @param text
	 * @return
	 * @throws DocumentException
	 */
	public Document getDoc(String str) throws DocumentException{
		
		Document document = DocumentHelper.parseText(str);
		return document;
	}
	
	/**
	 * 将String转换成XMLBean
	 * @param str
	 * @return
	 * @throws DocumentException
	 */
	public XMLBean parse(String str) throws DocumentException{
		
		Document doc = this.getDoc(str);
		return this.parse(doc);
	}
	
	/**
	 * 将XMLBean转换成String
	 * @param xml
	 * @return
	 */
	public String parse(XMLBean xml){
		
		Document doc = this.createDocument(xml);
		return doc.asXML();
	}
	
	/**
	 * 将XML解析成XMLBean对象列表,
	 * @param element  列表XML内容
	 * @param map 父节点生成的XMLBean 对象
	 */
	public void parseList(Element element,XMLBean map){
		Iterator iter = element.elementIterator();
		ArrayList<XMLBean> list = new ArrayList<XMLBean>();
		while(iter.hasNext()){
			
			Element e = (Element)iter.next();
			
			XMLBean xml = new XMLBean();
			
			parseAttribute(e,xml);
        	
        	list.add(xml);
		}
		map.elementPut(list);
	}
	
	/**
	 * 将XML解析成XMLBean对象列表,
	 * @param element  列表XML内容
	 * @param map 父节点生成的XMLBean 对象
	 * @param nodeName 节点名称
	 */
	public void parseList(Element element,XMLBean map,String nodeName){
		Iterator iter = element.elementIterator();
		ArrayList<XMLBean> list = new ArrayList<XMLBean>();
		while(iter.hasNext()){
			
			Element e = (Element)iter.next();
			
			XMLBean xml = new XMLBean();
			
			parseAttribute(e,xml);
        	
        	list.add(xml);
		}
		map.elementPut(nodeName,list);
	}
	
	/**
	 * 将XML节点对象，解析成XMLBean对象
	 * @param element
	 * @param map
	 */
	public void parseElement(Element element,XMLBean map){
		
		Iterator iter = element.elementIterator();
		
		while(iter.hasNext()){
			
			Element e = (Element)iter.next();
			
			XMLBean xml = new XMLBean();
			
			parseAttribute(e,xml);
        	
        	map.elementPut(xml.getName(),xml);
		}
	}
	
	/**
	 * 解析XML属性
	 * @param element
	 * @param xml
	 */
	public void parseAttribute(Element element,XMLBean xml){
		xml.setName(element.getName());
		
//		System.out.println(element.getName() + "  " + element.getText());
		
		xml.setText(element.getText());
		
		HashMap<String,String> map = new HashMap<String,String>();
		
		List attlist = element.attributes();
    	//attributes
    	for(int k=0;k<attlist.size();k++){
    		Attribute attribute = (Attribute)attlist.get(k);
    		
    		map.put(attribute.getName(), attribute.getStringValue());
    	}
//    	System.out.println(map);
    	xml.setAttributes(map);
    	
    	if(element.hasContent()){
    		xml.setLeaf(false);
//    		if(map.containsKey(XmlTools.ATTR_ROWS)){//是列表
//    			if(ObjectUtils.parseInt(map.get(XmlTools.ATTR_ROWS))>0){
//    				parseList(element,xml);
//    			}
//    		}else{
//    			parseElement(element,xml);
//    		}

    		if(map.containsKey(XmlTools.ATTR_ROWS)){//是列表
    			if(ObjectUtils.parseInt(map.get(XmlTools.ATTR_ROWS)) > -1){
    				parseList(element,xml);
    			}
    		}else if(XmlUtils.listMap.containsKey(element.getName())){//如果是列表
    			parseList(element,xml);
    		}else{
    			parseElement(element,xml);
    		}
    	}else{
    		xml.setLeaf(true);
    	}
    	
	}
	/**
	 * 将XMLBean对象生成XML节点
	 * @param xml
	 * @return
	 */
	public Element createElement(XMLBean xml){
		Element element = new DefaultElement(xml.getName());
		
		createElement(element,xml);
		
		return element;
	}
	
	/**
	 * 将XMLBean对象生成XML文档对象
	 * @param xml
	 * @return
	 */
	public Document createDocument(XMLBean xml){
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding(XmlTools.Encoding_GB2312);
    	
//		if(xml==null)System.out.println("%%%%%%%%%%%");
//		System.out.println("*****aaa:"+xml.getName());
		
    	Element root = document.addElement(xml.getName());
    	
    	createElement(root,xml);
    	
    	return document;
	}
	
	public Document createDocument(XMLBean xml,String encoding){
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding(encoding);
    	
    	Element root = document.addElement(xml.getName());
    	
    	createElement(root,xml);
    	
    	return document;
	}
	/**
	 * 将XMLBean对象创建成一个XML节点对象，并放在指定的XML节点对象中。
	 * @param element
	 * @param bean
	 */
	public void createElement(Element element,XMLBean bean){
		
		if(bean.getText()!=null&&!bean.getText().equals("")){
			element.addText(bean.getText());
		}
		
		createAttribute(element,bean);
		
		if(bean.isLeaf()) return;
		
		HashMap map = bean.getElements();
		
		if(map==null||map.isEmpty()||map.size()<=0) return;
		
		Iterator iterator = map.keySet().iterator();
		while(iterator.hasNext()){
			String key = (String)iterator.next();
			Object obj = map.get(key);
			
			if(obj instanceof XMLBean){//element
				XMLBean xmlbean = (XMLBean)obj;
				Element e = element.addElement(xmlbean.getName());
				createElement(e,xmlbean);
			}else if(obj instanceof ArrayList){//list
				ArrayList list = (ArrayList)obj;
				createList(element,list);
			}
		}
	}
	/**
	 * 将列表对象生成XML节点对象，并放在指定的对象下。
	 * @param element
	 * @param list
	 */
	public void createList(Element element,ArrayList list){
		if(list==null||list.isEmpty()||list.size()<=0) return;
		
		int size = list.size();
		for(int i=0;i<size;i++){
			//Element e = element.addElement(XmlTools.TAG_LIST);
			XMLBean bean = (XMLBean)list.get(i);
			
			Element e = element.addElement(bean.getName());
			
			createAttribute(e,bean);
			
			if(!bean.isLeaf()){
				createElement(e,bean);
			}
		}
	}
	
	/**
	 * 
	 * @param element
	 * @param bean
	 */
	public void createAttribute(Element element,XMLBean bean){
		HashMap map = bean.getAttributes();
		
//		System.out.println("------"+map);
		
		if(map==null||map.isEmpty()||map.size()<=0) return;
		
		Iterator iterator = map.keySet().iterator();
		while(iterator.hasNext()){
			String key = (String)iterator.next();
			element.addAttribute(key, (String)map.get(key));
		}
	}
}
