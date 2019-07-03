package com.yundepot.oaa.config;

/**
 * @author zhaiyanan
 * @date 2019/5/28 18:56
 */
public class GlobalConfigManager {
    private static final ConfigManager CONFIG_MANAGER = new ConfigManager();

    public static  <T> T getValue(ConfigOption<T> option) {
        return CONFIG_MANAGER.getValue(option);
    }
}
