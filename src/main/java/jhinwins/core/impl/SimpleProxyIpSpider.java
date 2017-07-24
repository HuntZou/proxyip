package jhinwins.core.impl;

import jhinwins.core.FreeProxyIpSpider;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Jhinwins on 2017/7/21  14:34.
 * Desc:
 */
public abstract class SimpleProxyIpSpider extends FreeProxyIpSpider {
    public SimpleProxyIpSpider(String providerUrl) {
        super(providerUrl);
    }

    /**
     * 需要复写  解析一条完整的ip
     *
     * @param html 请求的html页面
     * @return 返回完整ip所在的环境
     */
    public abstract Elements parseIPHome(Document html);

    /**
     * 需要复写  解析ip
     *
     * @param html 请求的html页面
     * @return 返回ip地址
     */
    public abstract String parseIP(Element html);

    /**
     * 需要复写  解析端口号
     *
     * @param html 请求的html页面
     * @return 返回端口号
     */
    public abstract Integer parsePort(Element html);

    /**
     * 需要复写  解析匿名度
     *
     * @param html 请求的html页面
     * @return 返回匿名度
     */
    public String parseAnonLevel(Element html) {
        return null;
    }

    /**
     * 需要复写  解析类型
     *
     * @param html 请求的html页面
     * @return 返回类型
     */
    public String parseType(Element html) {
        return null;
    }

    /**
     * 需要复写  解析支持类型
     *
     * @param html 请求的html页面
     * @return 返回支持类型
     */
    public String parseSupType(Element html) {
        return null;
    }

    /**
     * 需要复写  解析ip所在位置
     *
     * @param html 请求的html页面
     * @return 返回ip所在位置
     */
    public String parseLocation(Element html) {
        return null;
    }

    /**
     * 需要复写  解析响应时间
     *
     * @param html 请求的html页面
     * @return 返回响应时间
     */
    public Long parseSpeed(Element html) {
        return null;
    }

    /**
     * 需要复写  解析ip已经存活时间
     *
     * @param html 请求的html页面
     * @return 返回ip已经存活时间
     */
    public Long parseAge(Element html) {
        return null;
    }

    /**
     * 需要复写  解析上次验证到现在的时间
     *
     * @param html 请求的html页面
     * @return 返回上次验证到现在的时间
     */
    public Long parseLastverify(Element html) {
        return null;
    }
}
