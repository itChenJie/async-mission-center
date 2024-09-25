package com.mission.center.error;

public enum ServerCode {
    SUCCESS(200, "成功"), SYSTEM_ERROR(5000, "[mc001]系统异常"),
    PARAM_ERROR(50001, "参数错误"),
    DOWNLOAD_EXECUTE_REFLECT_ERROR(50002, "下载反射执行异常"),PARAM_ILLEGAL_ERROR(50003, "参数不合法异常");
    private Integer code;
    private String msg;
    
    public static boolean isSuccess(String code) {
    	return ServerCode.SUCCESS.getCode().equals(code);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private ServerCode() {
    }

    private ServerCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }


}
