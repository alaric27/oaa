package com.yundepot.oaa.connection;

import com.yundepot.oaa.util.StringUtils;

/**
 * @author zhaiyanan
 * @date 2019/5/15 13:52
 */
public class Url {
    public static final char COLON = ':';

    private String originUrl;
    private String ip;
    private int port;
    private String uniqueKey;

    protected Url(String originUrl) {
        this.originUrl = originUrl;
    }

    public Url(String ip, int port) {
        this(ip + COLON + port);
        this.ip = ip;
        this.port = port;
        this.uniqueKey = this.originUrl;
    }

    public Url(String originUrl, String ip, int port) {
        this(originUrl);
        this.ip = ip;
        this.port = port;
        this.uniqueKey = ip + COLON + port;
    }

    public Url(String originUrl, String ip, int port, String uniqueKey) {
        this(originUrl, ip, port);
        this.uniqueKey = uniqueKey;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public static Url parse(String addr) {
        if (StringUtils.isBlank(addr)) {
            throw new IllegalArgumentException("url should not be blank! ");
        }
        String[] s = addr.split(":");
        Url url = new Url(s[0], Integer.parseInt(s[1]));
        return url;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Url url = (Url) obj;
        if (this.getOriginUrl().equals(url.getOriginUrl())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getOriginUrl() == null) ? 0 : this.getOriginUrl().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Origin url [" + this.originUrl + "], Unique key [" + this.uniqueKey + "].");
        return sb.toString();
    }

}
