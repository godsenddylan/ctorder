package net.chetong.order.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 影像-标签节点
 */
public class TagNode extends Node{
	private Long photoCount=0L; //标签数量
	private List<Node> children = new ArrayList<Node>();
	
	public TagNode() {
		super("0");
	}
	
	public Long getPhotoCount() {
		return photoCount;
	}
	public void setPhotoCount(Long photoCount) {
		this.photoCount = photoCount;
	}
	public List<Node> getChildren() {
		return children;
	}
	public void setChildren(List<Node> children) {
		this.children = children;
	}
}
