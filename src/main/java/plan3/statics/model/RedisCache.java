package plan3.statics.model;

import static java.util.Objects.requireNonNull;
import plan3.pure.redis.JedisUtil;
import redis.clients.jedis.Jedis;

import com.google.common.hash.HashCode;

public class RedisCache implements Cache {
    private final JedisUtil jedis;

    public RedisCache(final JedisUtil jedis) {
        this.jedis = requireNonNull(jedis);
    }

    @Override
    public Location get(final Location location) {
        return location.withRevision(read(location));
    }

    @Override
    public boolean hasId(final Location location) {
        try(Jedis nonTx = this.jedis.nonTx()) {
            return nonTx.exists(key(location));
        }
    }

    @Override
    public boolean exists(final Location location) {
        return location.revision().equals(get(location));
    }

    @Override
    public void put(final Content content) {
        try(Jedis nonTx = this.jedis.nonTx()) {
            nonTx.set(key(content), content.where().revision().toString());
        }
    }

    @Override
    public void remove(final Location location) {
        try(Jedis nonTx = this.jedis.nonTx()) {
            nonTx.del(key(location));
        }
    }

    private Revision read(final Location path) {
        try(Jedis nonTx = this.jedis.nonTx()) {
            return new Revision(HashCode.fromString(nonTx.get(key(path))));
        }
    }
}
