package net.chetong.order.service.webService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import org.apache.commons.collections.CollectionUtils;
import net.chetong.order.model.webservice.ResultModel;
import net.chetong.order.model.webservice.YcImageVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.exception.ProcessException;

@WebService(endpointInterface="net.chetong.order.service.webService.YcInterfaceService",serviceName="ycInterfaceServiceImpl")
public class YcInterfaceServiceImpl extends BaseService implements YcInterfaceService {
	
	@Override
	public ResultModel queryImageList(String reportNo,String taskId) throws ProcessException {
		ResultModel rstMd = new ResultModel();
		try{ 
			if(StringUtil.isNullOrEmpty(reportNo)){
				rstMd.setRstCode("-1");
				rstMd.setRstMsg("报案号 ["+reportNo+"]为空！ ");
				return rstMd;
			}
			//根据任务ID 获取订单号
			Map<String,Object> imgMap = new HashMap<String,Object>();
			imgMap.put("reportNo", reportNo);
			imgMap.put("cpyTaskId", taskId);
			List<YcImageVO> imgList  = commExeSqlDAO.queryForList("sqlmap_hy_image.queryImgList", imgMap);
			if(CollectionUtils.isEmpty(imgList)){
				rstMd.setRstCode("-1");
				rstMd.setRstMsg("报案号 ["+reportNo+"]未获取到照片信息！");
				return rstMd;
			}
//			YcImageVO[] imgArr = new YcImageVO[imgList.size()];
//			imgArr = imgList.toArray(imgArr);
			rstMd.setRstCode("1");
			rstMd.setRstMsg("报案号 ["+reportNo+"]获取到附件["+imgList.size()+"]成功！");
			rstMd.setRestObj(imgList);
			return rstMd;
		}catch(Exception e){
			rstMd.setRstCode("-1");
			rstMd.setRstMsg("报案号 ["+reportNo+"]获取附件异常！"+e);
		}
		return rstMd;
	}
}
