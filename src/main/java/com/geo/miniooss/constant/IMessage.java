package com.geo.miniooss.constant;

public enum IMessage {
    SUCCESS("000","成功"),
    FAILED("001","失败"),
    EXIST("002","已存在"),
    NOTEXIST("003","不存在");
    public String CODE;
    private String MESSAGE;

    IMessage(String code,String message){
        this.CODE = code;
        this.MESSAGE = message;
    }

    public String getMessage(String code) {
        for (IMessage i :
                IMessage.values()) {
            if (i.CODE.equals(code))
                return i.MESSAGE;
        }
        return "";
    }

    public static IMessage getByCode(String code){
        for (IMessage i :
                IMessage.values()) {
            if (i.CODE.equals(code))
                return i;
        }
        return null;
    }
}
