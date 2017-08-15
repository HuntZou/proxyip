package jhinwins.core;

import jhinwins.model.ProxyIp;
import jhinwins.utils.IpUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jhinwins on 2017/8/14  13:31.
 * Desc:使用代理ip之前，您必须首先调用init方法，建议在应用初始化的阶段就调用该方法
 */
public class Resource {
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
     * 代理ip池
     */
    private static LinkedList<ProxyIp> proxyIpPool = new LinkedList<ProxyIp>();

    public static void init(FreeProxyIpSpider freeProxyIpSpider) {
        Resource.freeProxyIpSpider = freeProxyIpSpider;
        System.out.println("代理ip类初始化完成");
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
                    System.out.println("检测ip开始");
                    long t = System.currentTimeMillis();
                    //检测ip池数量
                    if (proxyIpPool.size() < MIN_POLL_COUNT) {
                        loadIP();
                    }

                    //检测首个ip是否可用
                    while (proxyIpPool.peek() != null && !IpUtils.canCMUse(proxyIpPool.peek())) {
                        System.out.println("检测出一个无用ip");
                        proxyIpPool.removeFirst();
                    }
                    try {
                        Thread.sleep(1000 * 30 + (int) (Math.random() * 20));
                    } catch (InterruptedException e) {
                    }
                    System.out.println("检测结束，用时：" + (System.currentTimeMillis() - t) + "，池中ip数：" + proxyIpPool.size());
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
        }
        return pull();
    }

    /**
     * 从代理ip网站爬取代理ip
     */
    private static void loadIP() {
        List<ProxyIp> proxyIps = freeProxyIpSpider.parseIpsFromHtml();
        if (proxyIps != null && proxyIps.size() > 0) {
            proxyIpPool.addAll(proxyIps);
        }
    }

}
