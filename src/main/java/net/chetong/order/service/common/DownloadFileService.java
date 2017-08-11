package net.chetong.order.service.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 公共的服务方法
 */
public interface DownloadFileService {
	/**
	 * 下载文件
	 * @author wufj@chetong.net
	 *         2016年3月30日 上午10:27:17
	 * @param request
	 * @param response
	 */
	public void downloadFile(HttpServletRequest request, HttpServletResponse response);
}
