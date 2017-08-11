package net.chetong.order.util.ctenum;

public enum ServiceId {
	CAR("1"),    //车险
	CARGO("5"),      //货运险
	RS("7");//人伤、医健险
	
	private String value;
	private ServiceId(String value){
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static String toLabel(String key){
		switch (key) {
		case "1":
			return "车险";
		case "5":
			return "货运险";
		case "7":
			return "人伤";
		default:
			return "";
		}
	}
}
