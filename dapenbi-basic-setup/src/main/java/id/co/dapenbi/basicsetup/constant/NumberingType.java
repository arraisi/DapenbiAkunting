package id.co.dapenbi.basicsetup.constant;

public enum NumberingType {
	STANDARD("STANDARD"), COMBINATION("COMBINATION");

	private String value;

	private NumberingType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
