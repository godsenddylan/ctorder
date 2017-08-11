package net.chetong.order.common.interceptor.param;

public abstract class ParamProcessAbstractFilter implements ParamProcessFilter{
	public ParamProcessAbstractFilter(){
		ParamProcessAspect.addFilters(this);
	}
}
