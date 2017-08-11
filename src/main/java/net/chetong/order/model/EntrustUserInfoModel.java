package net.chetong.order.model;

/**
 * 货运险-- CBS保险公司 type=6
 * fm_order_loss_data_mapping
 * @author jiemin
 *
 */
public class EntrustUserInfoModel {

	private long entrustId;  /*-- cbs_id --*/
	private String entrustName; /*-- cbs_name --*/

	public long getEntrustId() {
		return entrustId;
	}

	public void setEntrustId(long entrustId) {
		this.entrustId = entrustId;
	}

	public String getEntrustName() {
		return entrustName;
	}

	public void setEntrustName(String entrustName) {
		this.entrustName = entrustName;
	}

}
