package id.co.dapenbi.basicsetup.constant;

public enum ParameterType {
	DATE("DATE"), 
	TEXT("TEXT"), 
	NUMBER("NUMBER"),
	DROPDOWN("DROPDOWN");

	private String value;

	private ParameterType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
