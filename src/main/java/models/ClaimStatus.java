package models;

public enum ClaimStatus {
	Open("open",0), Pending("pending",1), Closed("closed",2);
	
	private String desc;
	private int value;
	ClaimStatus(String desc, int value){
		this.desc = desc;
		this.value = value;
		
	}
	public String getDesc() {
		return desc;
	}
//	public ClaimStatus getDesc(int value) {
//		return (value == getValue() ? this : null);
//	}
//	
	public int getValue() {
		return value;
	}

}
