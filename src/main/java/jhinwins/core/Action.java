package jhinwins.core;

import jhinwins.NetFilter.AbstractNetFilter;
import jhinwins.cache.SortSetOpt;
import jhinwins.model.ProxyIp;
import jhinwins.utils.JsonUtils;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

/**
 * Created by Jhinwins on 2017/8/18  17:04.
 * Desc:核心启动类
 */
@Component
public class Action {
    private Logger logger = Logger.getLogger(Action.class);
    private SortSetOpt sortSetOpt = new SortSetOpt();

    /**
     * 加载原始的代理ip数据
     */
    public void loadOriginalSource(FreeProxyIpSpider freeProxyIpSpider) {
        LinkedList<ProxyIp> proxyIps = freeProxyIpSpider.parseIpsFromHtml();
        if (proxyIps != null && proxyIps.size() > 0)
            for (ProxyIp proxyIp : proxyIps) {
                sortSetOpt.zadd("originalProxyIpPool", System.currentTimeMillis(), JsonUtils.getBasicProxyIp(proxyIp));
            }
    }

    /**
     * 网络过滤器
     *
     * @param abstractNetFilter 过滤器实例
     * @param originalPool      源数据池
     * @param targetPool        目标池
     */
    public void doNetFilter(AbstractNetFilter abstractNetFilter, String originalPool, String targetPool) {
        while (sortSetOpt.zcard(originalPool) > 0) {
            String proxyIp_str = sortSetOpt.zpop(originalPool);
            ProxyIp proxyIp = JsonUtils.getBasicProxyIp(proxyIp_str);
            doNetFilter(abstractNetFilter, proxyIp, targetPool);
        }
    }

    /**
     * 单个ip网络过滤器
     *
     * @param abstractNetFilter 过滤器实例
     * @param proxyIp           被检测ip
     * @param targetPool        目标池
     */
    public Long doNetFilter(AbstractNetFilter abstractNetFilter, ProxyIp proxyIp, String targetPool) {
        if (proxyIp == null) return -1L;
        Long preT = System.currentTimeMillis();
        PostMethod httpResponse = abstractNetFilter.doFilter(proxyIp);
        Long aftT = System.currentTimeMillis();

        Long responseT = aftT - preT;

        if (httpResponse != null && 200 == httpResponse.getStatusLine().getStatusCode()) {
            sortSetOpt.zadd(targetPool, responseT, JsonUtils.getBasicProxyIp(proxyIp));
            return responseT;
        }
        return new Long(-1);
    }

    /**
     * 检测首个ip是否可用,不可用则丢弃
     *
     * @param key
     */
    public Long detectionFirst(AbstractNetFilter abstractNetFilter, String key) {
        String first = sortSetOpt.zgetFirst(key);
        Long respT = doNetFilter(abstractNetFilter, JsonUtils.getBasicProxyIp(first), key);
        if (respT < 0) {
            sortSetOpt.zrem(key, first);
        }
        return respT;
    }

}
