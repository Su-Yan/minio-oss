package com.geo.miniooss.constant;

public enum IConstant {
    SUCCESS("000","成功"),
    FAILED("001","失败"),
    EXIST("002","已存在"),
    NOTEXIST("003","不存在"),
    UTF8("utf-8","utf-8");
    public String CODE;
    private String MESSAGE;

    IConstant(String code, String message){
        this.CODE = code;
        this.MESSAGE = message;
    }

    public String getMessage(String code) {
        for (IConstant i :
                IConstant.values()) {
            if (i.CODE.equals(code))
                return i.MESSAGE;
        }
        return "";
    }

    public static IConstant getByCode(String code){
        for (IConstant i :
                IConstant.values()) {
            if (i.CODE.equals(code))
                return i;
        }
        return null;
    }
}
