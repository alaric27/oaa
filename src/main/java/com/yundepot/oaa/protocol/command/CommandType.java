package com.yundepot.oaa.protocol.command;

/**
 * @author zhaiyanan
 * @date 2022/6/16  13:58
 */
public enum CommandType {
    REQUEST((byte) 0),
    RESPONSE((byte) 1),
    ONE_WAY((byte) 2);

    private byte value;

    CommandType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }
}
