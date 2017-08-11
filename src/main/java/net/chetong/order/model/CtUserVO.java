package net.chetong.order.model;

/**
 * ct_user 用户模型
 */
public class CtUserVO implements java.io.Serializable {
	private static final long serialVersionUID = -7486739219839461219L;
	/**
	 * 属性定义.
	 */
	private String id;	/*--  --*/
	private String pid;	/*-- 父账号id --*/
	private String userType;	/*-- 用户类型 0 - 个人 1 - 机构 2-团队 --*/
	private String isSub;	/*-- 是否子账户 0 - 否 1 - 是 --*/
	private String chkStat;	/*-- 认证状态 0 - 未认证 1 - 审核中 2 - 已认证（审核通过）3 - 未通过  --*/
	private String stat;	/*-- 用户状态 0 - 正常1 - 停用 2 - 黑名单 --*/
	private String acStat;	/*-- 账户状态 0 - 正常1 - 冻结 --*/
	private String bankChkStat;	/*-- 银行卡审核状态 0 - 等待审核 1 -审核通过 2 - 审核退回  --*/
	private String serviceStat;	/*-- 加盟服务状态 0未加盟 1认证中 2未通过 3已加盟 4加盟退出 --*/
	private String origin;	/*-- 来源 0 - 用户自注册 1 - 代理注册 --*/
	private String loginName;	/*-- 登录名 个人用户为手机号 机构用户为用户名 --*/
	private String loginPwd;	/*-- 登录密码 --*/
	private String payPwd;	/*-- 支付密码 --*/
	private String email;	/*-- 电子邮箱 --*/
	private String lastname;	/*-- 姓 --*/
	private String firstname;	/*-- 名 --*/
	private String department;	/*-- 所在部门 --*/
	private String pin;	/*-- 身份证号码 --*/
	private String birthday;	/*-- 生日 --*/
	private String sex;	/*-- 性别 0 - 男 1 - 女 --*/
	private String bank;	/*-- 开户行 --*/
	private String branch;	/*-- 支行 --*/
	private String bankNo;	/*-- 账号 --*/
	private String linkBank;	/*-- 联行号 --*/
	private String mobile;	/*-- 手机 --*/
	private String tel;	/*-- 电话 --*/
	private String regTime;	/*-- 注册时间 --*/
	private String lastTime;	/*-- 上次登录时间 --*/
	private String lastIp;	/*-- 上次登录ip --*/
	private String visitCount;	/*-- 访问次数 --*/
	private String welcome;	/*-- 用户自设欢迎词 --*/
	private String payableBondMoney;	/*-- 应缴保证金总额 --*/
	private String hyPayableBondMoney;/**货运险应缴保证金**/
	private String userMoney;	/*-- 账户总额 --*/
	private String bondMoney;	/*-- 保证金 --*/
	private String bondMoneyHy;/**货运险保证金**/
	private String frozenMoney;	/*-- 冻结金额 --*/
	private String availableMoney;	/*-- 可用余额 可以为负数 ，但该值必须大于 负信用额度 即 availableMoney > - creditMoney --*/
	private String creditMoney;	/*-- 信用额度   该额度为用户的总信用额度，定值。 --*/
	private String totalMoney;	/*-- 可用额度 --*/
	private String operId;	/*-- 代理人id --*/
	private String chkAppTime;	/*-- 认证申请时间 --*/
	private String chkAuditTime;	/*-- 认证审核时间 --*/
	private String chkOperAuditId;	/*-- 审核人id --*/
	private String chkAuditReason;	/*-- 认证意见 --*/
	private String bankChkAuditTime;	/*-- 银行卡审核时间 --*/
	private String bankChkOperAuditId;	/*-- 银行卡审核人id --*/
	private String bankChkAuditReason;	/*-- 银行卡审核意见 --*/
	private String mailProvCode;	/*-- 邮寄省份代码 --*/
	private String mailCityCode;	/*-- 邮寄地市代码 --*/
	private String mailAreaCode;	/*-- 邮寄区县代码 --*/
	private String mailProvDesc;	/*-- 邮寄省份 --*/
	private String mailCityDesc;	/*-- 邮寄地市 --*/
	private String mailAreaDesc;	/*-- 邮寄区县 --*/
	private String mailAddress;	/*-- 邮寄地址 --*/
	private String myCredit;	/*--  --*/
	private String isFanhua;	/*-- 是否泛华员工  1是 0否 --*/
	private String sound;	/*-- 推送铃声 --*/
	private String orgName;	/*-- 机构名 --*/
	private String orgShortName;	/*-- 机构名简称 --*/
	private String signRescue;	/*-- 是否与车童网签约救援 0为签约 1已签约 2取消签约 --*/
	private String ext1;	/*-- 是否发放礼品 1发放 0 未发放 --*/
	private String ext2;	/*-- 订单是否初审  0初审 1 不初审 --*/
	private String ext3;	/*-- 车童来源 1 泛华车险 2泛华财险 3经代人员 4保险公司 5公估公司 6修理厂 7其他来源 --*/
	private String ext4;	/*-- 自我介绍 --*/
	private String ext5;	/*-- 收藏码 --*/
	private String isMgr;	/*-- 是否项目经理 1-是 0-否 --*/
	private String showPrice; /*--是否显示价格 1-是 0-否--*/
	private String isSeedPerson;/*是否是种子车童*/
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getIsSub() {
		return isSub;
	}
	public void setIsSub(String isSub) {
		this.isSub = isSub;
	}
	public String getChkStat() {
		return chkStat;
	}
	public void setChkStat(String chkStat) {
		this.chkStat = chkStat;
	}
	public String getStat() {
		return stat;
	}
	public void setStat(String stat) {
		this.stat = stat;
	}
	public String getAcStat() {
		return acStat;
	}
	public void setAcStat(String acStat) {
		this.acStat = acStat;
	}
	public String getBankChkStat() {
		return bankChkStat;
	}
	public void setBankChkStat(String bankChkStat) {
		this.bankChkStat = bankChkStat;
	}
	public String getServiceStat() {
		return serviceStat;
	}
	public void setServiceStat(String serviceStat) {
		this.serviceStat = serviceStat;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getLoginPwd() {
		return loginPwd;
	}
	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}
	public String getPayPwd() {
		return payPwd;
	}
	public void setPayPwd(String payPwd) {
		this.payPwd = payPwd;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public String getBankNo() {
		return bankNo;
	}
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}
	public String getLinkBank() {
		return linkBank;
	}
	public void setLinkBank(String linkBank) {
		this.linkBank = linkBank;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getRegTime() {
		return regTime;
	}
	public void setRegTime(String regTime) {
		this.regTime = regTime;
	}
	public String getLastTime() {
		return lastTime;
	}
	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}
	public String getLastIp() {
		return lastIp;
	}
	public void setLastIp(String lastIp) {
		this.lastIp = lastIp;
	}
	public String getVisitCount() {
		return visitCount;
	}
	public void setVisitCount(String visitCount) {
		this.visitCount = visitCount;
	}
	public String getWelcome() {
		return welcome;
	}
	public void setWelcome(String welcome) {
		this.welcome = welcome;
	}
	public String getPayableBondMoney() {
		return payableBondMoney;
	}
	public void setPayableBondMoney(String payableBondMoney) {
		this.payableBondMoney = payableBondMoney;
	}
	public String getUserMoney() {
		return userMoney;
	}
	public void setUserMoney(String userMoney) {
		this.userMoney = userMoney;
	}
	public String getBondMoney() {
		return bondMoney;
	}
	public void setBondMoney(String bondMoney) {
		this.bondMoney = bondMoney;
	}
	public String getFrozenMoney() {
		return frozenMoney;
	}
	public void setFrozenMoney(String frozenMoney) {
		this.frozenMoney = frozenMoney;
	}
	public String getAvailableMoney() {
		return availableMoney;
	}
	public void setAvailableMoney(String availableMoney) {
		this.availableMoney = availableMoney;
	}
	public String getCreditMoney() {
		return creditMoney;
	}
	public void setCreditMoney(String creditMoney) {
		this.creditMoney = creditMoney;
	}
	public String getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(String totalMoney) {
		this.totalMoney = totalMoney;
	}
	public String getOperId() {
		return operId;
	}
	public void setOperId(String operId) {
		this.operId = operId;
	}
	public String getChkAppTime() {
		return chkAppTime;
	}
	public void setChkAppTime(String chkAppTime) {
		this.chkAppTime = chkAppTime;
	}
	public String getChkAuditTime() {
		return chkAuditTime;
	}
	public void setChkAuditTime(String chkAuditTime) {
		this.chkAuditTime = chkAuditTime;
	}
	public String getChkOperAuditId() {
		return chkOperAuditId;
	}
	public void setChkOperAuditId(String chkOperAuditId) {
		this.chkOperAuditId = chkOperAuditId;
	}
	public String getChkAuditReason() {
		return chkAuditReason;
	}
	public void setChkAuditReason(String chkAuditReason) {
		this.chkAuditReason = chkAuditReason;
	}
	public String getBankChkAuditTime() {
		return bankChkAuditTime;
	}
	public void setBankChkAuditTime(String bankChkAuditTime) {
		this.bankChkAuditTime = bankChkAuditTime;
	}
	public String getBankChkOperAuditId() {
		return bankChkOperAuditId;
	}
	public void setBankChkOperAuditId(String bankChkOperAuditId) {
		this.bankChkOperAuditId = bankChkOperAuditId;
	}
	public String getBankChkAuditReason() {
		return bankChkAuditReason;
	}
	public void setBankChkAuditReason(String bankChkAuditReason) {
		this.bankChkAuditReason = bankChkAuditReason;
	}
	public String getMailProvCode() {
		return mailProvCode;
	}
	public void setMailProvCode(String mailProvCode) {
		this.mailProvCode = mailProvCode;
	}
	public String getMailCityCode() {
		return mailCityCode;
	}
	public void setMailCityCode(String mailCityCode) {
		this.mailCityCode = mailCityCode;
	}
	public String getMailAreaCode() {
		return mailAreaCode;
	}
	public void setMailAreaCode(String mailAreaCode) {
		this.mailAreaCode = mailAreaCode;
	}
	public String getMailProvDesc() {
		return mailProvDesc;
	}
	public void setMailProvDesc(String mailProvDesc) {
		this.mailProvDesc = mailProvDesc;
	}
	public String getMailCityDesc() {
		return mailCityDesc;
	}
	public void setMailCityDesc(String mailCityDesc) {
		this.mailCityDesc = mailCityDesc;
	}
	public String getMailAreaDesc() {
		return mailAreaDesc;
	}
	public void setMailAreaDesc(String mailAreaDesc) {
		this.mailAreaDesc = mailAreaDesc;
	}
	public String getMailAddress() {
		return mailAddress;
	}
	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}
	public String getMyCredit() {
		return myCredit;
	}
	public void setMyCredit(String myCredit) {
		this.myCredit = myCredit;
	}
	public String getIsFanhua() {
		return isFanhua;
	}
	public void setIsFanhua(String isFanhua) {
		this.isFanhua = isFanhua;
	}
	public String getSound() {
		return sound;
	}
	public void setSound(String sound) {
		this.sound = sound;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getOrgShortName() {
		return orgShortName;
	}
	public void setOrgShortName(String orgShortName) {
		this.orgShortName = orgShortName;
	}
	public String getSignRescue() {
		return signRescue;
	}
	public void setSignRescue(String signRescue) {
		this.signRescue = signRescue;
	}
	public String getExt1() {
		return ext1;
	}
	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}
	public String getExt2() {
		return ext2;
	}
	public void setExt2(String ext2) {
		this.ext2 = ext2;
	}
	public String getExt3() {
		return ext3;
	}
	public void setExt3(String ext3) {
		this.ext3 = ext3;
	}
	public String getExt4() {
		return ext4;
	}
	public void setExt4(String ext4) {
		this.ext4 = ext4;
	}
	public String getExt5() {
		return ext5;
	}
	public void setExt5(String ext5) {
		this.ext5 = ext5;
	}
	public String getIsMgr() {
		return isMgr;
	}
	public void setIsMgr(String isMgr) {
		this.isMgr = isMgr;
	}
	public String getShowPrice() {
		return showPrice;
	}
	public void setShowPrice(String showPrice) {
		this.showPrice = showPrice;
	}
	public String getIsSeedPerson() {
		return isSeedPerson;
	}
	public void setIsSeedPerson(String isSeedPerson) {
		this.isSeedPerson = isSeedPerson;
	}
	public String getHyPayableBondMoney() {
		return hyPayableBondMoney;
	}
	public void setHyPayableBondMoney(String hyPayableBondMoney) {
		this.hyPayableBondMoney = hyPayableBondMoney;
	}
	public String getBondMoneyHy() {
		return bondMoneyHy;
	}
	public void setBondMoneyHy(String bondMoneyHy) {
		this.bondMoneyHy = bondMoneyHy;
	}
}
