package com.toutiao.melon.api.stream;

/**
 * 数据流数据值
 */
public class Value {

    /** 数据字段名 */
    private String name;

    /** 数据字段值 */
    private Object value;

    public Value() {
    }

    public Value(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Value{"
                + "name='" + name + '\''
                + ", value=" + value
                + '}';
    }
}
