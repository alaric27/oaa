package com.yundepot.oaa.config;

/**
 * @author zhaiyanan
 * @date 2019/5/24 18:43
 */
public class ConfigOption <T>{
    private final String key;
    private T defaultValue;

    private ConfigOption(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public static <T> ConfigOption<T> valueOf(String key) {
        return new ConfigOption<>(key, null);
    }

    public static <T> ConfigOption<T> valueOf(String key, T defaultValue) {
        return new ConfigOption<>(key, defaultValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConfigOption<?> that = (ConfigOption<?>) o;
        return this.key != null ? this.key.equals(that.getKey()) : that.key == null;
    }

    @Override
    public int hashCode() {
        return key !=null ? key.hashCode() : 0;
    }
}
