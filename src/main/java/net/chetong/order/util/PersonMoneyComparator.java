package net.chetong.order.util;

import java.util.Comparator;

import net.chetong.order.model.MyEntrustQueryPeopleVO;

/**
 * 查询车童比较器，排序用
 */
public class PersonMoneyComparator implements Comparator<MyEntrustQueryPeopleVO> {
	
	/**
	 * 返回参数  -1 、0 、1 
	 * 排序规则  -1 表示o1排在o2前面  ;1表示o1排在o2后面
	 */
	
	public int compare(MyEntrustQueryPeopleVO o1,MyEntrustQueryPeopleVO o2) {
		//是否种子车童  则比较价格
		if("1".equals(o1.getIsSeedPerson())&&"1".equals(o2.getIsSeedPerson())){
			return o1.getTotalMoney().compareTo(o2.getTotalMoney());
		}else if("1".equals(o1.getIsSeedPerson())){
			return -1;
		}else if("1".equals(o2.getIsSeedPerson())){
			return 1;
		}else{
			return o1.getTotalMoney().compareTo(o2.getTotalMoney());
		}
	}

}
