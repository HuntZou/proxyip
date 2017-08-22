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

    private static int flag = 0;

    public static ProxyIp pull() {
        Set<String> cmProxyIpPool = null;
        switch (flag) {
//            case 0:
//                cmProxyIpPool = sortSetOpt.zrange("CMProxyIpPool", 0, 0);
//                break;
//            case 1:
//                cmProxyIpPool = sortSetOpt.zrange("CMProxyIpPool", 1, 1);
//                break;
//            case 2:
//                cmProxyIpPool = sortSetOpt.zrange("CMProxyIpPool", 2, 2);
//                break;
            default:
                cmProxyIpPool = sortSetOpt.zrange("CMProxyIpPool", 0, 0);
                break;
        }
        if ((cmProxyIpPool == null || cmProxyIpPool.size() == 0) && flag != 0) {
            cmProxyIpPool = sortSetOpt.zrange("CMProxyIpPool", 0, 0);
        }
        flag = flag > 2 ? 0 : ++flag;
        return cmProxyIpPool != null && cmProxyIpPool.size() > 0 ? JsonUtils.getBasicProxyIp(cmProxyIpPool.iterator().next()) : null;
    }
}
