package com.toutiao.melon.api.stream;

/**
 * 数据流数据字段
 */
public class Field {

    /** 数据字段名 */
    private String fieldName;

    /** 数据字段类型 */
    private FieldType fieldType;

    public Field() {
    }

    public Field(String fieldName, FieldType fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    @Override
    public String toString() {
        return "Field{"
                + "fieldName='" + fieldName + '\''
                + ", fieldType=" + fieldType
                + '}';
    }
}
