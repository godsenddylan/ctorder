package net.chetong.order.model;

/**
 * 影像-抽象结点
 */
public abstract class Node {
	private Long nodeId;//节点id
	private String name;//节点名称
	private Long parentId;//当前结点父节点 图片为 tagId
	private String type;//节点类型 0 tag，1 photo
	
	public Node(String type){
		this.type = type;
	}
	public Long getNodeId() {
		return nodeId;
	}
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
