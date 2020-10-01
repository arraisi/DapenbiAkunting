package id.co.dapenbi.core.constant;

public enum SubmitMode {
	INSERT("INSERT"), UPDATE("UPDATE");
	
	private String value;
	
	private SubmitMode(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
}
