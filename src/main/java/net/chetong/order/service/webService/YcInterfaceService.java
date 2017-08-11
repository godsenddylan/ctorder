package net.chetong.order.service.webService;


import javax.jws.WebService;

import net.chetong.order.model.webservice.ResultModel;
import net.chetong.order.util.exception.ProcessException;

@WebService
public interface YcInterfaceService {
	public ResultModel queryImageList(String reportNo,String taskId) throws ProcessException;
	
}
