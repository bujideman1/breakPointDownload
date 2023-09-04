package com.easypan.entity.vo;


import com.easypan.entity.enums.ResponseCodeEnum;
import lombok.Data;

@Data
public class ResponseVO<T> {
    private String status;
    private Integer code;
    private String info;
    private T data;

    public ResponseVO() {
        this.code= ResponseCodeEnum.CODE_200.getCode();
        this.info=ResponseCodeEnum.CODE_200.getMsg();
    }
    public static ResponseVO okResult(){
        ResponseVO responseVO = new ResponseVO();
        return responseVO;
    }
    public static ResponseVO okResult(Integer code,String info){
        ResponseVO responseVO = new ResponseVO();
        responseVO.ok(code,null,info);
        return responseVO;
    }
    public static ResponseVO<?> okResult(Object data){
        ResponseVO responseVO=okResult(ResponseCodeEnum.CODE_200.getCode(),ResponseCodeEnum.CODE_200.getMsg());
        if(data!=null){
            responseVO.setData(data);
        }
        return responseVO;
    }
    private ResponseVO<?> ok(Integer code, T data, String info) {
        this.setCode(code);
        this.setData(data);
        this.setInfo(info);
        return this;
    }


}

