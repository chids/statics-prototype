package plan3.statics;

import plan3.statics.model.Coordinator;

import plan3.statics.model.Cache;
import plan3.statics.model.Content;
import plan3.statics.model.Lock;
import plan3.statics.model.Static;
import plan3.statics.model.RedisCache;
import plan3.statics.model.RedisLock;
import plan3.statics.model.S3Storage;
import plan3.statics.model.Storage;
import plan3.pure.redis.JedisUtil;
import plan3.pure.util.Timeout;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

public class Mainer implements Observer {

    private static final String AWSSECRET = "...";
    private static final String AWSKEY = "...";
    private static final String REDISURL = "...";

    public Mainer(final Storage storage, final Cache cache, final Lock lock) throws Exception {
        final Coordinator coordinator = new Coordinator(cache, storage, lock, this);
        final Content version1 = new Content("domain", "type", "id", "content");
        final Static revision1 = coordinator.add(version1);
        final Content version2 = version1.update("second content");
        System.err.println(coordinator.update(revision1, version2));
    }

    public static void main(final String[] args) throws Exception {
        final JedisUtil jedis = redis();
        final Timeout timeout = new Timeout(2, TimeUnit.SECONDS);
        new Mainer(new S3Storage(s3(), "marten-gustafson-test"), new RedisCache(jedis), new RedisLock(jedis, timeout))
                .toString();
    }

    private static JedisUtil redis() {
        return new JedisUtil(
                REDISURL);
    }

    private static AmazonS3 s3() {
        final AWSCredentials key = new BasicAWSCredentials(AWSKEY, AWSSECRET);
        final AmazonS3Client client = new AmazonS3Client(new StaticCredentialsProvider(key));
        client.setRegion(Region.getRegion(Regions.EU_WEST_1));
        return client;
    }

    @Override
    public void update(final Observable o, final Object arg) {
        System.err.println(o + "\t" + arg);
    }
}
