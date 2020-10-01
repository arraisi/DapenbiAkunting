package id.co.dapenbi.basicsetup.constant;

public enum  AutoNumberTransactionCode {
    TRANSACTION_NO("TRANSACTION_NO");

    private String value;

    private AutoNumberTransactionCode(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

}
