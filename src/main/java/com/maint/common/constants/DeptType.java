package com.maint.common.constants;

/**
 * 
 * @author aisino
 *
 */
public enum DeptType {
	AREA("Area"),		// 区域
	POINT("Point");		// 维修点
	
    private String type;
    
    private DeptType(String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return this.type.toString();
    }
}
