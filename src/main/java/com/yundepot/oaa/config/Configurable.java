package com.yundepot.oaa.config;

/**
 * @author zhaiyanan
 * @date 2019/5/27 15:36
 */
public interface Configurable {

    /**
     * 获取配置项的值
     * @param option
     * @param <T>
     * @return
     */
    <T> T getValue(ConfigOption<T> option);


    /**
     * 设置配置项的值
     * @param option
     * @param value
     * @param <T>
     * @return
     */
    <T> Configurable option(ConfigOption<T> option, T value);
}
