package com.navigation.wfio_dlyw.comms;

import java.util.Date;

public class Token {
    private String value;
    private Date created;
    public Token(){
        this.value = null;
        this.created = new Date();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
