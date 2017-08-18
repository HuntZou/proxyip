package jhinwins.cache;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Set;

/**
 * Created by Jhinwins on 2017/8/18  14:19.
 * Desc:
 */
@Component
public class SortSetOpt {

    private Logger logger = Logger.getLogger(SortSetOpt.class);

    @Autowired
    private Jedis jedis;


    public Long zadd(String key, double score, String member) {
        boolean broken = false;
        Long zadd = null;
        try {
            zadd = jedis.zadd(key, score, member);
        } catch (JedisException e) {
            logger.error("jedis zadd error:" + e.getMessage());
            broken = RedisPool.handleJedisException(e);
        } finally {
            RedisPool.closeResource(jedis, broken);
        }
        return zadd;

    }

    public Long zremrangeByRank(String key, long start, long end) {
        boolean broken = false;
        Long aLong = null;
        try {
            aLong = jedis.zremrangeByRank(key, start, end);
        } catch (JedisException e) {
            logger.error("jedis zremrangeByRank error:" + e.getMessage());
            broken = RedisPool.handleJedisException(e);
        } finally {
            RedisPool.closeResource(jedis, broken);
        }
        return aLong;

    }

    public Long zcard(String key) {
        boolean broken = false;
        Long zcard = null;
        try {
            zcard = jedis.zcard(key);
        } catch (JedisException e) {
            logger.error("jedis zcard error:" + e.getMessage());
            broken = RedisPool.handleJedisException(e);
        } finally {
            RedisPool.closeResource(jedis, broken);
        }
        return zcard;

    }

    public Long zrem(String key, String... members) {
        boolean broken = false;
        Long zrem = null;
        try {
            zrem = jedis.zrem(key, members);
        } catch (JedisException e) {
            logger.error("jedis zrem error:" + e.getMessage());
            broken = RedisPool.handleJedisException(e);
        } finally {
            RedisPool.closeResource(jedis, broken);
        }
        return zrem;
    }

    public Set<String> zrange(String key, long start, long end) {
        boolean broken = false;
        Set<String> zrange = null;
        try {
            zrange = jedis.zrange(key, start, end);
        } catch (JedisException e) {
            logger.error("jedis zrange error:" + e.getMessage());
            broken = RedisPool.handleJedisException(e);
        } finally {
            RedisPool.closeResource(jedis, broken);
        }
        return zrange;
    }


}
