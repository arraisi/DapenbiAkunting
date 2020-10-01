package id.co.dapenbi.audittrail.constant;

public enum ModuleCode {
    MASTER_BANK("MASTER_BANK");


    private String value;

    private ModuleCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
