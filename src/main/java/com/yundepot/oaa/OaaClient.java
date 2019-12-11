package com.yundepot.oaa;

import com.yundepot.oaa.common.AbstractLifeCycle;
import com.yundepot.oaa.config.ConfigManager;
import com.yundepot.oaa.config.ConfigOption;
import com.yundepot.oaa.config.Configurable;
import com.yundepot.oaa.config.GenericOption;
import com.yundepot.oaa.connection.ClientConnectionEventHandler;

/**
 * @author zhaiyanan
 * @date 2019/5/28 16:09
 */
public abstract class OaaClient extends AbstractLifeCycle implements Client {

    protected final ConfigManager configManager;
    protected final ClientConnectionEventHandler connectionEventHandler;

    public OaaClient() {
        configManager = new ConfigManager();
        configManager.option(GenericOption.CONNECTION_MANAGE, true);
        connectionEventHandler = new ClientConnectionEventHandler(configManager);
    }

    @Override
    public <T> T getValue(ConfigOption<T> option) {
        return this.configManager.getValue(option);
    }

    @Override
    public <T> Configurable option(ConfigOption<T> option, T value) {
        configManager.option(option, value);
        return this;
    }
}
