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
    public Location get(final Located item) {
        Location path = item.where();
        return path.withRevision(read(path));
    }

    @Override
    public boolean hasId(final Located item) {
        try(Jedis nonTx = this.jedis.nonTx()) {
            return nonTx.exists(key(item.where()));
        }
    }

    @Override
    public boolean exists(final Located item) {
        Location path = item.where();
        return path.revision().equals(get(path));
    }

    @Override
    public void put(final Content content) {
        try(Jedis nonTx = this.jedis.nonTx()) {
            nonTx.set(key(content), content.where().revision().toString());
        }
    }

    @Override
    public void remove(final Located item) {
        try(Jedis nonTx = this.jedis.nonTx()) {
            nonTx.del(key(item.where()));
        }
    }

    private Revision read(final Location path) {
        try(Jedis nonTx = this.jedis.nonTx()) {
            return new Revision(HashCode.fromString(nonTx.get(key(path))));
        }
    }
}
