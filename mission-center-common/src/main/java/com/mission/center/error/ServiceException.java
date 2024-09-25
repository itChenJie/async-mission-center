package com.mission.center.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceException extends RuntimeException{
    private static final Logger log = LoggerFactory.getLogger(ServiceException.class);
    private int code;

    public ServiceException(ServerCode serverCode) {
        super(serverCode.getMsg());
        this.code = serverCode.getCode();
        log.warn(this.toString());
    }

    public ServiceException(String message, Throwable cause) {
        super(message);
        this.code = ServerCode.SYSTEM_ERROR.getCode();
        log.warn(this.toString(), cause);
    }

    public ServiceException(String message) {
        super(message);
        this.code = ServerCode.SYSTEM_ERROR.getCode();
        log.warn(this.toString());
    }

    public ServiceException(int code,String message) {
        super(message);
        this.code = code;
        log.warn(this.toString());
    }

    public ServiceException(String message, int code, Throwable cause) {
        super(message);
        this.code = code;
        log.warn(this.toString(), cause);
    }

    public String toString() {
        return "ServiceException{code=" + this.code + ", message=" + super.getMessage() + "}";
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
