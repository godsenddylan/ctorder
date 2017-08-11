package net.chetong.order.service.user;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;

import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.CtUserVO;
import net.chetong.order.model.MyEntrustQueryPeopleVO;
import net.chetong.order.util.exception.ProcessException;

/**
 * 用户
 * @author wufeng@chetong.net
 * @creation 2015年11月4日
 */

public interface UserService {
	/**
	 * 查询车童列表（车险）
	 * @param modelMap
	 * @return 车童列表数据
	 */
	public List<MyEntrustQueryPeopleVO> queryCtUserListWithSend_old(ModelMap modelMap) throws ProcessException;
	
	/**
	 * 查询车童（车险）多线程版
	 * @author wufj@chetong.net
	 *         2016年3月29日 下午2:02:35
	 * @param modelMap
	 * @return
	 * @throws ProcessException
	 */
	public List<MyEntrustQueryPeopleVO> queryCtUserListWithSend(ModelMap modelMap) throws Exception;
	
	/**
	 * 查询车童列表（货运险）
	 * @author wufj@chetong.net
	 *         2016年1月4日 上午9:31:42
	 * @param modelMap
	 * @return
	 * @throws ProcessException
	 */
	public List<MyEntrustQueryPeopleVO> queryHyUserListWithSend(ModelMap modelMap) throws ProcessException;
	
	/**
	 * 查询当前真实登录用户根据useid
	 * @author wufj@chetong.net
	 *         2015年12月7日 上午10:36:18
	 * @param userId 用户id
	 * @return  如果是车童个人账号，直接返回车童用户信息
	 *                如果是团队或者机构主账号，直接返回
	 *                如果是团队或者机构子账号，返回主账号信息
	 */
	public CtUserVO queryCurRealUser(Long userId);
	
	/**
	 * 查询机构、团队总账户
	 * @author wufj@chetong.net
	 *         2016年1月21日 上午10:31:36
	 * @param userId
	 * @return
	 */
	public CtGroupVO queryTopGroup(Long userId);
	
	/**
	 * 根据派单人id获取真正的买家信息
	 * @param userId 派单人id（登录PC的用户id）
	 * @param grantUserId 委托人id（登录PC的用户id）
	 * @return 买家信息
	 */
	public String getBuyerInfo(String userId, String grantUserId);
	
	/**
	 * 查询用户信息
	 * @author wufj@chetong.net
	 *         2015年12月7日 上午10:50:09
	 * @param userId 用户id
	 * @return
	 */
	public CtUserVO queryCtUserByKey(String userId);
	
	/**
	 * 查询用户的团队信息根据用户id
	 * @author wufj@chetong.net
	 *         2015年12月2日 下午4:27:10
	 * @param userId 用户id
	 * @return
	 */
	public CtGroupVO queryUserGroupByUserId(Long userId);
	
	/**
	 * 更新用户金额
	 * @author hougq@chetong.net
	 *         2015年12月15日 
	 * @param userId 用户id
	 * @return
	 */
	public void updateUserMoney(String userId,BigDecimal realTotalMoney);
	
	/**
	 * 货运险导出我的委托
	 *         2016年1月12日 下午3:47:51
	 * @param userId
	 * @param response
	 * @param request
	 * @throws Exception
	 */
	public void exportMyEntrustHY(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) throws ProcessException;
	/**
	 * 货运险导出我的委托
	 *         2016年1月12日 下午3:47:51
	 * @param userId
	 * @param response
	 * @param request
	 * @throws Exception
	 */
	public void exportMyEntrustHYForGroup(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) throws ProcessException;
	
	/**
	 * 货运险导出我的委托 按报案号导出
	 *         2016年1月12日 下午3:50:51
	 * @param userId
	 * @param response
	 * @param request
	 * @throws Exception
	 */
	public void exportMyEntrustWithCaseNoHY(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) throws ProcessException;
	/**
	 * 货运险导出我的委托 按报案号导出
	 *         2016年1月12日 下午3:50:51
	 * @param userId
	 * @param response
	 * @param request
	 * @throws Exception
	 */
	public void exportMyEntrustWithCaseNoHYForGroup(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) throws ProcessException;

	/**
	 * @Description: 查询订单导入中记录的团队
	 * @param orderNo
	 * @return
	 * @return CtGroupVO
	 * @author zhouchushu
	 * @date 2016年3月9日 下午3:57:59
	 */
	public CtGroupVO queryImportOrderGroup(String orderNo);

	/**
	 * 查询sql中指定的用户id
	 * @return
	 */
	public List<Map<String, Object>> queryTheLoginId();

}
