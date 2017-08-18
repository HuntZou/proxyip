package jhinwins.core;

import com.alibaba.fastjson.JSON;
import jhinwins.model.ProxyIp;
import jhinwins.utils.IpUtils;
import jhinwins.cache.RedisPool;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by Jhinwins on 2017/8/14  13:31.
 * Desc:使用代理ip之前，您必须首先调用init方法，建议在应用初始化的阶段就调用该方法
 */
public class Resource {

    private static Logger logger = Logger.getLogger(Resource.class);
    private static FreeProxyIpSpider freeProxyIpSpider;
    /**
     * ip池中存储最少个数
     */
    private static int MIN_POLL_COUNT = 5;
    /**
     * ip池中存储最多个数（但实际可能在短时间内大于该值）
     */
    private static int MAX_POLL_COUNT = 20;
    /**
     * pull的最大递归次数
     */
    private static int MAX_RECURSION_COUNT = 50;
    /**
     * 最少多少个检测周期更换一次ip源
     */
    private static int MIN_DETECTION_COUNT = 10;
    /**
     * 默认检测超时时间
     */
    private static int DEFAULT_FILTER_CONNECTION_TIME_OUT = 3 * 1000;
    /**
     * 代理ip检测次数
     */
    private static int detectionCount = 0;
    /**
     * 代理ip网络过滤类型
     */
    public static String NET_FILTER_TYPE_GENERAL = "general";
    public static String NET_FILTER_TYPE_CM = "cloudMusic";
    /**
     * 网络过滤后的ip池key
     */
    private static String AFTER_NET_FILTER_PROXY_IP_POLL = "afterNetFilterPoll";


    public static void init(FreeProxyIpSpider freeProxyIpSpider) {
        Resource.freeProxyIpSpider = freeProxyIpSpider;
        logger.info("代理ip类初始化完成");
        detectionIpPool();
    }


    /**
     * 保证pull的有效性
     */
    private static void detectionIpPool() {
        //每隔一段时间检测ip池第一个是否可用
        //如果可用则保持使用该ip，不可用则移除，切换到下一个ip
        new Thread(new Runnable() {
            public void run() {
                while (true) {

                    ++detectionCount;

                    //检测ip池数量 少于最少数量就加载
                    if (RedisPool.zcard(AFTER_NET_FILTER_PROXY_IP_POLL) < MIN_POLL_COUNT) {
                        logger.info("ip池中数量过少，准备加载");
                        loadIP2Redis(loadIP());
                        detectionCount = 0;
                    }
                    //每过一段时间重新加载ip源
                    if (detectionCount > MIN_DETECTION_COUNT) {
                        logger.info("重新加载ip源");
                        loadIP2Redis(loadIP());
                        detectionCount = 0;
                    }


                    long preT = System.currentTimeMillis();
                    //检测首个ip是否可用
                    ProxyIp proxyIp = pull();
                    while (proxyIp != null && IpUtils.canCMUse(proxyIp) == IpUtils.PROXY_IP_CANT_USE) {
                        RedisPool.zrem(AFTER_NET_FILTER_PROXY_IP_POLL, JSON.toJSONString(proxyIp));
                        logger.info("检测首个ip " + JSON.toJSONString(proxyIp) + " 不可用，已移除");
                    }
                    logger.info("一个当前使用ip检测循环结束，ip池size:" + RedisPool.zcard(AFTER_NET_FILTER_PROXY_IP_POLL) + "------检测耗时：" + (System.currentTimeMillis() - preT));
                    try {
                        Thread.sleep(1000 * 30 + (int) (Math.random() * 20000));
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage());
                        continue;
                    }
                }
            }
        }).start();
    }


    /**
     * 当前pull的递归数量
     */
    private static int recursionCount = 0;

    /**
     * 拉取一个最优的代理ip
     *
     * @return
     */
    public static ProxyIp pull() {
        //如果递归次数过多则直接返回空
        if ((++recursionCount) > MAX_RECURSION_COUNT) {
            recursionCount = 0;
            return null;
        }

        Set<String> zrange = RedisPool.zrange(AFTER_NET_FILTER_PROXY_IP_POLL, 0, 0);
        if (zrange != null && zrange.size() > 0) {
            String proxy_str = zrange.iterator().next();
            logger.info("pull获取ip:" + proxy_str);
            ProxyIp proxyIp = null;
            try {
                proxyIp = JSON.parseObject(proxy_str, ProxyIp.class);
            } catch (Exception e) {
                RedisPool.zrem(AFTER_NET_FILTER_PROXY_IP_POLL, proxy_str);
                logger.error("单个代理ip解析出错,已移除:" + e.getMessage());
            }
            return proxyIp;
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
        return pull();
    }

    /**
     * 加载需过滤ip到redis
     * 返回被加载的代理ip
     */
    private static List<ProxyIp> loadIP2Redis(List<ProxyIp> list) {
        List<ProxyIp> proxyIps = null;
        if (list != null && list.size() > 0) {
            proxyIps = filterNet(list, DEFAULT_FILTER_CONNECTION_TIME_OUT, NET_FILTER_TYPE_CM);
            detectionCount = 0;
        }
        return proxyIps;
    }

    /**
     * 从代理ip网站爬取代理ip
     */
    private static LinkedList<ProxyIp> loadIP() {
        LinkedList<ProxyIp> proxyIps = loadIP(Integer.MAX_VALUE);
        return proxyIps;
    }

    private static LinkedList<ProxyIp> loadIP(int limitCount) {
        long preT = System.currentTimeMillis();
        LinkedList<ProxyIp> proxyIps = freeProxyIpSpider.parseIpsFromHtml(limitCount);
        logger.info("total load:" + proxyIps.size() + ",use time:" + (System.currentTimeMillis() - preT));
        return proxyIps;
    }

    /**
     * 过滤所有不可连通外网的ip
     *
     * @return 返回过滤后的集合
     */
    private static LinkedList<ProxyIp> filterNet(List<ProxyIp> originalPoll, int connectionTimeOut) {
        return filterNet(originalPoll, connectionTimeOut, NET_FILTER_TYPE_GENERAL);
    }

    private static LinkedList<ProxyIp> filterNet(List<ProxyIp> originalPoll, int connectionTimeOut, String type) {
        return filterNet(originalPoll, connectionTimeOut, type, MAX_POLL_COUNT);
    }

    private static LinkedList<ProxyIp> filterNet(List<ProxyIp> originalPoll, int connectionTimeOut, String type, int limitCount) {
        logger.info("start filter proxy ip connectionTimeOut:" + connectionTimeOut + ",type:" + type + ",limitCount:" + limitCount);

        if (originalPoll == null || originalPoll.size() == 0) {
            return null;
        }
        //之所以拷贝一份是因为防止下面修改时间后对源数据造成影响
        originalPoll.subList(originalPoll.size() > limitCount ? limitCount : originalPoll.size(), originalPoll.size()).clear();
        LinkedList<ProxyIp> copyOriginalPoll = new LinkedList<ProxyIp>(originalPoll);
        ;

        Iterator<ProxyIp> iterator = copyOriginalPoll.iterator();
        while (iterator.hasNext()) {
            ProxyIp proxyIp = iterator.next();
            //为了防止存放在redis中的ip重复,但是如果时间不同的话也不会去重
            proxyIp.setAcquireTime(null);

            long conneT = Integer.MAX_VALUE;
            if (NET_FILTER_TYPE_GENERAL.equals(type)) {
                conneT = IpUtils.canUse(proxyIp);
            } else if (NET_FILTER_TYPE_CM.equals(type)) {
                conneT = IpUtils.canCMUse(proxyIp);
            }

            if (conneT == IpUtils.PROXY_IP_CANT_USE || conneT > connectionTimeOut) {
                iterator.remove();
            } else {
                RedisPool.zadd(AFTER_NET_FILTER_PROXY_IP_POLL, conneT, JSON.toJSONString(proxyIp));
            }
        }
        logger.info(AFTER_NET_FILTER_PROXY_IP_POLL + "  过滤后的ip池数量: " + copyOriginalPoll.size());

        //清除多余的代理ip
        RedisPool.zremrangeByRank(AFTER_NET_FILTER_PROXY_IP_POLL, MAX_POLL_COUNT, RedisPool.zcard(AFTER_NET_FILTER_PROXY_IP_POLL));

        return copyOriginalPoll;
    }

}
