package com.maint.system.model;

import java.util.Date;

public class ValidateCode {
    private Integer id;

    private String telEmail;

    private Date createDate;

    private String yzm;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTelEmail() {
        return telEmail;
    }

    public void setTelEmail(String telEmail) {
        this.telEmail = telEmail == null ? null : telEmail.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getYzm() {
        return yzm;
    }

    public void setYzm(String yzm) {
        this.yzm = yzm == null ? null : yzm.trim();
    }
}