package net.chetong.order.service.order;

import net.chetong.order.model.FmOrderVO;
import net.sf.json.JSONArray;

public interface HandOutService {

	public void saveHandOut(JSONArray jsonArray, FmOrderVO orderVO);

}
