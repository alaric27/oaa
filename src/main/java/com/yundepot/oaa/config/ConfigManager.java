package com.yundepot.oaa.config;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhaiyanan
 * @date 2019/5/27 14:54
 */
public class ConfigManager {

    private ConcurrentHashMap<ConfigOption<?>, Object> options = new ConcurrentHashMap<>();

    /**
     * 获取配置项的值
     * @param option
     * @param <T>
     * @return */
    public <T> T getValue(ConfigOption<T> option) {
        Object value = options.get(option);
        if(value == null) {
            value = option.getDefaultValue();
        }
        return value == null ? null : (T) value;
    }

    /**
     * 设置配置项
     * @param option
     * @param value
     * @param <T>
     * @return
     */
    public <T> ConfigManager option(ConfigOption<T> option, T value) {
        if (value == null) {
            options.remove(option);
            return this;
        }
        options.put(option, value);
        return this;
    }

}
