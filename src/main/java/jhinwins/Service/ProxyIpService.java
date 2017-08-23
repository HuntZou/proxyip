package jhinwins.Service;

import jhinwins.cache.SortSetOpt;
import jhinwins.model.ProxyIp;
import jhinwins.utils.JsonUtils;

import java.util.Set;

/**
 * Created by Jhinwins on 2017/8/18  16:03.
 * Desc:
 */
public class ProxyIpService {

    private static SortSetOpt sortSetOpt = new SortSetOpt();

    public static ProxyIp pull(String targetPool) {
        Set<String> cmProxyIpPool = sortSetOpt.zrange(targetPool, 0, 0);
        return cmProxyIpPool != null && cmProxyIpPool.size() > 0 ? JsonUtils.getBasicProxyIp(cmProxyIpPool.iterator().next()) : null;
    }

    public static synchronized ProxyIp remove(String targetPool, ProxyIp proxyIp) {
        String member = JsonUtils.getBasicProxyIp(proxyIp);
        Long rmCount = sortSetOpt.zrem(targetPool, member);
        if (rmCount > 0) {
            return proxyIp;
        }
        return null;
    }
}
