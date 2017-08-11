package net.chetong.order.controller.common;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.service.common.DownloadFileService;

@Controller
@RequestMapping("/downloadFile")
public class DownloadFileController extends BaseController {
	
	@Resource
	private DownloadFileService downloadCaseFile;
	
	/**
	 * 下载文件
	 * @author wufj@chetong.net 2015年12月9日 下午2:49:22
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/downloadFile",method=RequestMethod.GET)
	@ResponseBody
	public void downloadFile(HttpServletRequest request, HttpServletResponse response) {
		downloadCaseFile.downloadFile(request, response);
	}
}
