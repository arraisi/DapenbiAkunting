package id.co.dapenbi.basicsetup.constant;

public enum IncrementResetMode {
	YEARLY("YEARLY"), MONTHLY("MONTHLY"), DAILY("DAILY");
	
	private String value;
	
	private IncrementResetMode(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
}
