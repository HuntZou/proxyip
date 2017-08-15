package jhinwins.core;

import com.alibaba.fastjson.JSONObject;
import jhinwins.model.ProxyIp;
import jhinwins.utils.IpUtils;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

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
     * pull的最大递归次数
     */
    private static int MAX_RECURSION_COUNT = 50;
    /**
     * 最少多少个检测周期更换一次ip源
     */
    private static int MIN_DETECTION_COUNT = 30;
    /**
     * 代理ip池
     */
    private static LinkedList<ProxyIp> proxyIpPool = new LinkedList<ProxyIp>();
    /**
     * 代理ip检测次数
     */
    private static int detectionCount = 0;

    public static void init(FreeProxyIpSpider freeProxyIpSpider) {
        Resource.freeProxyIpSpider = freeProxyIpSpider;
        logger.info("代理ip类初始化完成");
    }

    /**
     * 保证pull的有效性
     */
    static {
        //每隔一段时间检测ip池第一个是否可用
        //如果可用则保持使用该ip，不可用则移除，切换到下一个ip
        new Thread(new Runnable() {
            public void run() {
                while (true) {

                    ++detectionCount;

                    //检测ip池数量   并且每过一段时间重新加载ip
                    if (proxyIpPool.size() < MIN_POLL_COUNT) {
                        List<ProxyIp> proxyIps = loadIP();
                        if (proxyIps != null && proxyIps.size() > 0) {
                            proxyIpPool.addAll(proxyIps);
                            detectionCount = 0;
                        }
                    }
                    if (detectionCount > MIN_DETECTION_COUNT) {
                        detectionCount = 0;
                        LinkedList<ProxyIp> proxyIps = loadIP();
                        if (proxyIps != null && proxyIps.size() > 0) {
                            proxyIpPool = proxyIps;
                        }
                    }


                    long preT = System.currentTimeMillis();
                    //检测首个ip是否可用
                    while (proxyIpPool.peek() != null && !IpUtils.canCMUse(proxyIpPool.peek())) {
                        proxyIpPool.removeFirst();
                    }
                    logger.info("一个当前使用ip检测循环结束，ip池size:" + proxyIpPool.size() + "------耗时：" + (System.currentTimeMillis() - preT));
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
     * 当前递归数量
     */
    private static int recursionCount = 0;

    /**
     * 拉取一个基本可用的代理ip
     *
     * @return
     */
    public static ProxyIp pull() {
        //如果递归次数过多则直接返回空
        if ((++recursionCount) > MAX_RECURSION_COUNT) {
            recursionCount = 0;
            return null;
        }

        if (proxyIpPool.size() > 0) {
            //获取ip池的第一个
            recursionCount = 0;
            return proxyIpPool.peek();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
        return pull();
    }

    public static String getInfo() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("currentIP", proxyIpPool.peek().getIp() + ":" + proxyIpPool.peek().getPort());


        return jsonObject.toJSONString();
    }

    /**
     * 从代理ip网站爬取代理ip
     */
    private static LinkedList<ProxyIp> loadIP() {
        LinkedList<ProxyIp> proxyIps = freeProxyIpSpider.parseIpsFromHtml();
        return proxyIps;
    }

}
