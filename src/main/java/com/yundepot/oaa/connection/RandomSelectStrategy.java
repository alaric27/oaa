package com.yundepot.oaa.connection;

import java.util.List;
import java.util.Random;

/**
 * @author zhaiyanan
 * @date 2019/6/8 18:31
 */
public class RandomSelectStrategy implements ConnectionSelectStrategy{

    private static final int MAX_TIMES = 5;
    private final Random random = new Random();


    @Override
    public Connection select(List<Connection> connectionList) {

        if (connectionList == null || connectionList.isEmpty()) {
            return null;
        }

        int size = connectionList.size();
        int tries = 0;
        Connection result = null;
        while ((result == null || !result.isFine()) && tries++ < MAX_TIMES) {
            result = connectionList.get(this.random.nextInt(size));
        }

        if (result != null && !result.isFine()) {
            result = null;
        }
        return result;
    }
}
