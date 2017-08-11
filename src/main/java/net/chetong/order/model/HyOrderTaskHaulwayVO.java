package net.chetong.order.model;

/**
 * 货运险任务运输地址
 * @author wufj@chetong.net
 *         2015年12月30日 下午3:22:45
 */
public class HyOrderTaskHaulwayVO {
	private Long id;
	private Long taskId;   //任务id
	private String startAddress;  //开始地点
	private String endAddress;   //到达地址
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	public String getStartAddress() {
		return startAddress;
	}
	public void setStartAddress(String startAddress) {
		this.startAddress = startAddress;
	}
	public String getEndAddress() {
		return endAddress;
	}
	public void setEndAddress(String endAddress) {
		this.endAddress = endAddress;
	}
}
