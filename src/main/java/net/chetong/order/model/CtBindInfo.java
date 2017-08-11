package net.chetong.order.model;
import java.io.Serializable;

import com.chetong.aic.entity.base.BaseModel;


/**
 * 
 * 第三方账号与用户ID绑定表,目前只针对微信绑定.
 * 
 **/
@SuppressWarnings("serial")
public class CtBindInfo extends BaseModel implements Serializable {

	/****/
	private Long id;

	/**绑定类型:1-微信,2-懒掌柜,**/
	private Integer bindType;

	/**用户ID**/
	private Long userId;

	/**第三方唯一标识(字符串),微信的openId,懒掌柜的managerId**/
	private String bindId;

	/**第三方账号:微信号,**/
	private String bindNo;

	/**昵称**/
	private String nickname;

	/**头像**/
	private String headPic;

	/**创建者**/
	private Long createId;

	/**创建时间**/
	private String createTime;



	public void setId(Long id){
		this.id = id;
	}

	public Long getId(){
		return this.id;
	}

	public void setBindType(Integer bindType){
		this.bindType = bindType;
	}

	public Integer getBindType(){
		return this.bindType;
	}

	public void setUserId(Long userId){
		this.userId = userId;
	}

	public Long getUserId(){
		return this.userId;
	}

	public void setBindId(String bindId){
		this.bindId = bindId;
	}

	public String getBindId(){
		return this.bindId;
	}

	public void setBindNo(String bindNo){
		this.bindNo = bindNo;
	}

	public String getBindNo(){
		return this.bindNo;
	}

	public void setNickname(String nickname){
		this.nickname = nickname;
	}

	public String getNickname(){
		return this.nickname;
	}

	public void setHeadPic(String headPic){
		this.headPic = headPic;
	}

	public String getHeadPic(){
		return this.headPic;
	}

	public void setCreateId(Long createId){
		this.createId = createId;
	}

	public Long getCreateId(){
		return this.createId;
	}

	public void setCreateTime(String createTime){
		this.createTime = createTime;
	}

	public String getCreateTime(){
		return this.createTime;
	}

}
