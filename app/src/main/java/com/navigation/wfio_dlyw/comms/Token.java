package com.navigation.wfio_dlyw.comms;

import java.util.Date;

public class Token{
    private static Token instance;

    // Global variable
    private String value = null;
    private String type;
    private int id;
    private String email;

    // Restrict the constructor from being instantiated
    private Token(){}

    public void setValue(String data){
        this.value = data;
    }
    public String getValue(){
        return this.value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static synchronized Token getInstance(){
        if(instance==null){
            instance=new Token();
        }
        return instance;
    }
}
