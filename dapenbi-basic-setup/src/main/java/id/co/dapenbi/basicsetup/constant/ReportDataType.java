package id.co.dapenbi.basicsetup.constant;

public enum ReportDataType {
	DATE("DATE"), 
	STRING("STRING"), 
	LONG("LONG"),
	INTEGER("INTEGER"),
	BIGDECIMAL("BIGDECIMAL");

	private String value;

	private ReportDataType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
