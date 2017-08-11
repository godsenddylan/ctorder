package net.chetong.order.service.hyorder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import net.chetong.order.model.HyExpressVO;
import net.chetong.order.model.HyOrderVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.util.COSSign;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.TencentHttpUtil;
import net.chetong.order.util.exception.ProcessException;
@Service("hyExpressService")
public class HyExpressServiceImpl extends BaseService implements HyExpressService{
	

	/**
	 * 更新快递信息
	 * @author 
	 *         2015年12月30日 上午10:27:07
	 * @param modelMap
	 * @return
	 */
	public ResultVO<Object> insertHyExpress(ModelMap modelMap) throws ProcessException {
		try {
			commExeSqlDAO.insertVO("sqlmap_hy_express.insertExpress", modelMap);
			ResultVO<Object> resultVO = new ResultVO<Object>();
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO);
			return resultVO;
		} catch (Exception e) {
			log.error("货运险更新快递信息出错",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("货运险更新快递信息出错",e);
		}
	}

	/**				
	 * 查询快递信息
	 * @author 
	 *         2015年12月30日 上午10:27:07
	 * @param orderNo
	 * @return
	 */
	
	public ResultVO<Object> queryHyExpressByOrderNo(String orderNo) throws ProcessException{
		try{
			List<Object> queryForList = commExeSqlDAO.queryForList("sqlmap_hy_express.queryHyExpressByOrderNo", orderNo);
			ResultVO<Object> resultVO = new ResultVO<Object>();
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, queryForList);
			return resultVO;
		}catch(Exception e){
			log.error("货运险查询快递信息异常:",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("货运险查询快递信息异常", e);
		}
	}
	/**
	 * 删除图片链接
	 * @author 
	 *         2015年12月30日 上午10:27:07
	 * @param modelMap
	 * @return
	 */
	public ResultVO<Object> updateHyExpressPic(ModelMap modelMap) throws ProcessException {
		try {
			String serviceId = (String) modelMap.get("serviceId");
			Integer flag = null;
			if ("7".equals(serviceId)) {
				//医健险
				flag = commExeSqlDAO.updateVO("sqlmap_hy_express.delExpressPic", modelMap);
			} else if ("5".equals(serviceId)) {
				//货运险
				flag = commExeSqlDAO.updateVO("sqlmap_hy_express.updateExpressPic", modelMap);
			}
			
			String imageUrl = (String) modelMap.get("imageUrl");
			final String urlStr = imageUrl.substring(imageUrl.indexOf(".com") + 4, imageUrl.lastIndexOf("/"));
			
			if (flag > 0) {
				
				//删除腾讯云服务器的图片
				new Thread((new Runnable() {
					
					@Override
					public void run() {
						Map<String, String> headerMap = new HashMap<String, String>();
						String url = null;
						
						url = TencentHttpUtil.YOUTU_INTERFACE_URL_PREFIX+"v1"+urlStr+"/del";
						headerMap.put("Host",TencentHttpUtil.FILE_SERVER_HOST);
						String fileid = urlStr.substring(urlStr.lastIndexOf("/")+1);
						String temp = urlStr.substring(0,urlStr.lastIndexOf("/"));
						String userid = temp.substring(temp.lastIndexOf("/")+1);
						headerMap.put("Authorization",COSSign.getImageOnceSign(userid, fileid));
						String s = TencentHttpUtil.post(url,null, headerMap);
						log.debug(s + "--------------------------------------");
					
					}
				})).start();
				
			}
			ResultVO<Object> resultVO = new ResultVO<Object>();
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO);
			return resultVO;
		} catch (Exception e) {
			log.error("货运险删除快递图片出错",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("货运险删除快递图片出错",e);
		}
	}

	@Override
	public ResultVO<String> updateOrderExpressBySeller(ModelMap modelMap) throws ProcessException {
		String orderNo = (String) modelMap.get("orderNo");
		String userId = (String) modelMap.get("userId");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderNo", orderNo);
		params.put("sellerUserId", userId);
		List<HyOrderVO> sellerOrderList = commExeSqlDAO.queryForList("sqlmap_hy_order.queryOrderBySellerAndOrderNo", params);
		if (sellerOrderList.size()!=1) {
			throw ProcessCodeEnum.FAIL.buildProcessException("您的订单不存在");
		}
		
		modelMap.put("id", modelMap.get("expressId"));
		int updateVO = commExeSqlDAO.updateVO("sqlmap_hy_express.updateByPrimaryKeySelective", modelMap);
		if (updateVO<=0) {
			throw ProcessCodeEnum.FAIL.buildProcessException("快递信息更新失败，不存在该快递信息");
		}
		
		return ProcessCodeEnum.SUCCESS.buildResultVOR("快递信息跟新成功");
	}

}
