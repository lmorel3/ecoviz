package org.ecoviz.domain.dto;

public class ValueDto {

    String value;

    public ValueDto() {

    }

    public ValueDto(String value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    public static ValueDto of(Object o) {
        return new ValueDto(String.valueOf(o));
    }

}