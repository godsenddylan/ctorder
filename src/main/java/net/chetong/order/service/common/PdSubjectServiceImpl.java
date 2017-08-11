package net.chetong.order.service.common;

import org.springframework.stereotype.Service;

import net.chetong.order.model.PdServiceSubjectVO;


@Service("subjectService")
public class PdSubjectServiceImpl extends BaseService implements PdSubjecService {

	public PdServiceSubjectVO getPdServiceSubject(long serviceSubjectId, long serviceId) {

		PdServiceSubjectVO pdServiceSubjectExample = new PdServiceSubjectVO();
		pdServiceSubjectExample.setId(serviceSubjectId);
		pdServiceSubjectExample.setServiceId(serviceId);
		return commExeSqlDAO.queryForObject("pd_service_subject.queryPdServiceSubject", pdServiceSubjectExample);

	}

}
