package models;

public enum PolicyType {
	Dental("dental", 1), Medical("medical", 2), Life("life", 3), Vision("vision", 4);
	private String desc;
	private int value;

	PolicyType(String desc, int value) {
		this.desc = desc;
		this.value = value;
	}

	public String getDesc() {
		return desc;
	}

	public int getValue() {
		return value;
	}

}
