package net.chetong.order.util.ctenum;

public enum CXOrderType {
	SURVEY("0","查勘"),
	BD_LOSS("1","标的定损"),
	SZ_LOSS("2","三者定损"),
	DAMAGE("3","物损");
	
	private String key;
	private String value;
	private CXOrderType(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public String getKey(){
		return this.key;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public static String toLable(String key){
		switch (key) {
		case "0":
			return "查勘";
		case "1":
			return "标的定损";
		case "2":
			return "三者定损";
		case "3":
			return "物损";
		default:
			return "";
		}
	}
}
