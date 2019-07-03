package com.yundepot.oaa.common;

/**
 * @author zhaiyanan
 * @date 2019/5/23 14:46
 */
public enum ResponseStatus {
    SUCCESS((short) 0),
    SERVER_EXCEPTION((short) 1),
    TIMEOUT((short) 2),
    CLIENT_SEND_EXCEPTION((short) 3),
    CODEC_EXCEPTION((short) 4),
    CONNECTION_CLOSED((short) 5),
    ;

    private short code;

    ResponseStatus(short code) {
        this.code = code;
    }

    public short getValue() {
        return code;
    }


    public static ResponseStatus valueOf(short value) {
        switch (value) {
            case 0x0000:
                return SUCCESS;
            case 0x0002:
                return SERVER_EXCEPTION;
            case 0x0007:
                return TIMEOUT;
            case 0x0008:
                return CLIENT_SEND_EXCEPTION;
            case 0x0009:
                return CODEC_EXCEPTION;
            case 0x0010:
                return CONNECTION_CLOSED;
        }
        throw new IllegalArgumentException("Unknown status value ," + value);
    }
}
