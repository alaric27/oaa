package com.yundepot.oaa.protocol;

import java.util.Objects;

/**
 * 协议编码
 * @author zhaiyanan
 * @date 2019/5/16 09:06
 */
public class ProtocolCode {
    private byte code;
    private byte version;

    private ProtocolCode(byte code, byte version) {
        this.code = code;
        this.version = version;
    }

    public byte getCode() {
        return code;
    }

    public void setCode(byte code) {
        this.code = code;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public static ProtocolCode getProtocolCode(byte code, byte version) {
        return new ProtocolCode(code, version);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProtocolCode that = (ProtocolCode) o;
        return code == that.code && version == that.version;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, version);
    }

    @Override
    public String toString() {
        return "ProtocolCode{" +
                "code=" + code +
                ", version=" + version +
                '}';
    }
}
