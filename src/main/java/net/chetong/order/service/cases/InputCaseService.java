package net.chetong.order.service.cases;

import java.util.Map;

import net.chetong.order.util.exception.ProcessException;

public interface InputCaseService {

	public String saveCaseInfo(Map<String, Object> paraMap) throws ProcessException;

}
