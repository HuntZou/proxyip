package jhinwins.core;

import jhinwins.NetFilter.AbstractNetFilter;
import jhinwins.cache.SortSetOpt;
import jhinwins.model.ProxyIp;
import jhinwins.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;

/**
 * Created by Jhinwins on 2017/8/18  17:04.
 * Desc:
 */
public class Action {
    @Autowired
    private SortSetOpt sortSetOpt;

    /**
     * 加载原始的代理ip数据
     */
    public void loadOriginalSource(FreeProxyIpSpider freeProxyIpSpider, String targetPool) {
        LinkedList<ProxyIp> proxyIps = freeProxyIpSpider.parseIpsFromHtml();
        for (ProxyIp proxyIp : proxyIps) {
            sortSetOpt.zadd(targetPool, System.currentTimeMillis(), JsonUtils.getBasicProxyIp(proxyIp));
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

    }

    /**
     * 单个ip网络过滤器
     *
     * @param abstractNetFilter 过滤器实例
     * @param proxyIp           被检测ip
     * @param targetPool        目标池
     */
    public void doNetFilter(AbstractNetFilter abstractNetFilter, ProxyIp proxyIp, String targetPool) {

    }

}
