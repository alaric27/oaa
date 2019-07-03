package com.yundepot.oaa.protocol;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zhaiyanan
 * @date 2019/5/16 09:13
 */
public class ProtocolManager {

    private final ConcurrentMap<ProtocolCode, Protocol> protocols = new ConcurrentHashMap<>();

    public Protocol getProtocol(ProtocolCode protocolCode){
        return protocols.get(protocolCode);
    }

    public void registerProtocol(Protocol protocol) {
        if (null == protocol) {
            throw new RuntimeException("Protocol should not be null!");
        }

        Protocol exists = protocols.putIfAbsent(protocol.getProtocolCode(), protocol);
        if (exists != null) {
            throw new RuntimeException("Protocol for code: " + protocol.getProtocolCode() + " already exists!");
        }
    }
}
