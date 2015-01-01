package com.citi.ets;

public enum SortMethod {

    DEFAULT("default");

    private String method;

    private SortMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

}
