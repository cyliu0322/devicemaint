package com.maint.common.constants;

public enum LoginType {

	WEB("Web"),  MOBILE("Mobile");
    private String type;
    private LoginType(String type) {
        this.type = type;
    }
    @Override
    public String toString() {
        return this.type.toString();
    }
}
