package id.co.dapenbi.audittrail.constant;

public enum ActionType {
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    VALIDATE("VALIDATE");


    private String value;

    private ActionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
