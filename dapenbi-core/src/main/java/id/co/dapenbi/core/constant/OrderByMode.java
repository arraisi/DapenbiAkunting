package id.co.dapenbi.core.constant;

public enum OrderByMode {
	ASC("ASC"), DESC("DESC");

	private String value;

	private OrderByMode(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
