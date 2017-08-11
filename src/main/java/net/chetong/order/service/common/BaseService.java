package net.chetong.order.service.common;

import javax.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.chetong.order.dao.CommExeSqlDAO;

public class BaseService {
	protected static Logger log = LogManager.getLogger(BaseService.class);
	@Resource
	public CommExeSqlDAO commExeSqlDAO;
	
	public CommExeSqlDAO getCommExeSqlDAO() {
		return commExeSqlDAO;
	}

	public void setCommExeSqlDAO(CommExeSqlDAO commExeSqlDAO) {
		this.commExeSqlDAO = commExeSqlDAO;
	}
}
