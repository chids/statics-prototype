package plan3.statics.model.impl;

import plan3.statics.model.Lock;

import plan3.pure.redis.JedisUtil;
import plan3.pure.util.Timeout;
import redis.clients.jedis.Jedis;

import java.time.Clock;
import java.util.concurrent.TimeUnit;

public class RedisLock extends Lock {

    private final JedisUtil jedis;
    private final Clock clock;

    public RedisLock(final JedisUtil jedis, final Timeout timeout) {
        super(timeout);
        this.jedis = jedis;
        this.clock = Clock.systemUTC();
    }

    @Override
    public boolean lock(final String key) {
        try(Jedis nonTx = this.jedis.nonTx()) {
            if(1 == nonTx.setnx(key, this.clock.instant().toString())) {
                nonTx.expire(key, (int)super.timeout.to(TimeUnit.SECONDS));
                return true;
            }
        }
        return false;
    }

    @Override
    public void unlock(final String key) {
        try(Jedis nonTx = this.jedis.nonTx()) {
            nonTx.del(key);
        }
    }
}
