
package com.mission.center.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mission.center.error.ServerCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWrapper<T> {
    private Integer code;
    private String desc;
    private T data;

    public ResponseWrapper() {
        this.code  = ServerCode.SUCCESS.getCode();
        this.desc = ServerCode.SUCCESS.getMsg();
    }

    public static <T> ResponseWrapper<T> success(T data) {
        return ResponseWrapper.<T>builder().data(data)
                .code(ServerCode.SUCCESS.getCode())
                .desc(ServerCode.SUCCESS.getMsg())
                .build();
    }

    public static <T> ResponseWrapper<T> success(Integer code, T data) {
        return ResponseWrapper.<T>builder()
                .code(code)
                .data(data)
                .build();
    }

    public void build(Integer code,String message) {
        this.code = code;
        this.desc = message;
    }
    public static <T> ResponseWrapper<T> fail(ServerCode serverCode) {
        return fail(serverCode.getCode(), serverCode.getMsg());
    }
    
    public static <T> ResponseWrapper<T> fail(String message) {
        return fail(ServerCode.SYSTEM_ERROR.getCode(), message);
    }

    public static <T> ResponseWrapper<T> fail(Integer code, String message) {
        return ResponseWrapper.<T>builder()
                .code(code)
                .desc(message)
                .build();
    }
}