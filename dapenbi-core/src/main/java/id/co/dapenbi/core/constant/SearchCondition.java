package id.co.dapenbi.core.constant;

public enum SearchCondition {
    EQUAL("EQUAL"),
    NOT_EQUAL("NOT_EQUAL"),
    GREATER_THAN("GREATER_THAN"),
    ALL("ALL");

    private String value;

    private SearchCondition(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
