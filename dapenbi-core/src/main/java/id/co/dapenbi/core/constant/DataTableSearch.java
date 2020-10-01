package id.co.dapenbi.core.constant;

public class DataTableSearch {
    private String condition;
    private String column;
    private String value;
    private String object;

    public DataTableSearch() {

    }

    public DataTableSearch(String _condition, String _column, String _value) {
        this.condition = _condition;
        this.column = _column;
        this.value = _value;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

}