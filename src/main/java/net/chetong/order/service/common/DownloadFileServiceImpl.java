package net.chetong.order.service.common;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import net.chetong.order.util.Constants;
import net.chetong.order.util.CtFileUtil;
import net.chetong.order.util.ProcessCodeEnum;

/**
 * 下载文件方法
 */
@Service("downFileService")
public class DownloadFileServiceImpl extends BaseService implements DownloadFileService{
	
	/**
	 * 下载文件
	 * @author wufj@chetong.net
	 *         2016年3月30日 上午10:29:07
	 * @param request
	 * @param response
	 */
	@Override
	public void downloadFile(HttpServletRequest request, HttpServletResponse response){
		String uuid = request.getParameter("uuid");
		StringBuilder downPath = new StringBuilder(request.getSession().getServletContext().getRealPath("/")).append(Constants.DOWNLOAD_TEMP_DIR).append(uuid).append(".zip");
		//StringBuilder downPath = new StringBuilder("f:/"+"downloadCarCase/");
		File file = null;
		 try {
			 file = new File(downPath.toString());
			CtFileUtil.exportFile(response, file, true);
		} catch (IOException e) {
			log.error("下载文件失败",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("下载文件失败",e);
		}
	}
}
