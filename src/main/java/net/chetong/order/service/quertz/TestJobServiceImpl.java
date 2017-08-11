package net.chetong.order.service.quertz;

import org.springframework.stereotype.Service;

import net.chetong.order.service.common.BaseService;
import net.chetong.order.util.Config;

@Service("testJobService")
public class TestJobServiceImpl extends BaseService implements TestJobService {
//	private static Logger log = LogManager.getLogger(TestJobService.class);
	private final String JOB_SWITCH ="job_switch";
	@Override
	public void testJob() {
		if(!Config.JOB_SWITCH){
//			log.info("JOB 开关未打开");
			return;
		}
		log.info("我是job");
	}

}
