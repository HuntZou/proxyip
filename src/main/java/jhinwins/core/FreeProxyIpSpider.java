package jhinwins.core;


import jhinwins.model.ProxyIp;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jhinwins on 2017/7/21  10:55.
 * Desc:
 */
public abstract class FreeProxyIpSpider {

    /**
     * ip池中ip最少数量
     */
    public static int MIN_IP_COUNT = 5;
    /**
     * ip池中ip添加到的数量
     */
    public static int MIN_ADD_IP_COUNT = 15;
    /**
     * 代理ip池
     */
    private List<ProxyIp> ipList = new ArrayList<ProxyIp>();

    /**
     * 是否正在从网页上ip
     */
    private boolean pullLoading = false;

    /**
     * 被爬取网页的url
     */
    private String providerUrl = null;

    public FreeProxyIpSpider(String providerUrl) {
        this.providerUrl = providerUrl;
    }

    public List<ProxyIp> getIpList() {
        return ipList;
    }

    /**
     * 获取指定url的html
     *
     * @return
     * @throws IOException
     */
    public String getHtml() {
        String html = "";

        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpGet provider = new HttpGet(providerUrl);
            provider.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
            CloseableHttpResponse httpResponse = httpClient.execute(provider);
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "gbk"));
            String buff;
            while ((buff = reader.readLine()) != null) {
                html += buff;
            }
        } catch (IOException e) {
            throw new RuntimeException("获取html时发生异常");
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    //do nothing
                }
            }
        }

        return html;
    }

    /**
     * 从html源码中解析出代理ip对象
     *
     * @return 不会返回空
     */
    private List<ProxyIp> parseIpsFromHtml(String html) {
        List<ProxyIp> list = new ArrayList<ProxyIp>();
        //使用jsoup解析html
        Document document = Jsoup.parse(html);

        Elements ipHomes = parseIPHome(document);
        for (Element ipHome : ipHomes) {
            ProxyIp proxyIp = new ProxyIp();
            try {
                proxyIp.setAcquireTime(System.currentTimeMillis());
                proxyIp.setAge(parseAge(ipHome));
                proxyIp.setAnonLevel(parseAnonLevel(ipHome));
                proxyIp.setIp(parseIP(ipHome));
                proxyIp.setLastverify(parseLastverify(ipHome));
                proxyIp.setLocation(parseLocation(ipHome));
                proxyIp.setPort(parsePort(ipHome));
                proxyIp.setSpeed(parseSpeed(ipHome));
                proxyIp.setSupType(parseSupType(ipHome));
                proxyIp.setType(parseType(ipHome));
            } catch (Exception e) {
                continue;
            }
            list.add(proxyIp);
        }
        System.out.println("add ip count: " + list.size());
        return list;
    }

    /**
     * 异步向ip池里添加ip
     *
     * @return
     */
    private void addList() {
        if (!pullLoading) {
            pullLoading = true;
            new Thread(new Runnable() {
                public void run() {
                    List<ProxyIp> proxyIps = parseIpsFromHtml(getHtml());
                    ipList.addAll(proxyIps);
                    System.out.println("load ip count: " + proxyIps.size());
                    pullLoading = false;
                }
            }).start();
        }
    }

    /**
     * 拉取一个代理ip对象
     * 并将已经拉取的ip对象移除
     *
     * @return 返回队列中的第一个，如果队列为空则返回空
     */
    public ProxyIp pull() {
        return pull("");
    }

    public ProxyIp pull(String anonLevel) {
        System.out.println("pull method excute, pool size is : " + ipList.size());
        if (ipList != null && ipList.size() > 0) {
            ProxyIp proxyIp = ipList.get(0);
            ipList.remove(0);
            System.out.println("proxyIp.getAnonLevel():" + proxyIp.getAnonLevel() + "---" + "anonLevel:" + anonLevel);
            if ((proxyIp.getAnonLevel() == null ? true : proxyIp.getAnonLevel().contains(anonLevel + "")) && canUse(proxyIp)) {
                return proxyIp;
            }
        }
        //如果ip数量少于最少数量时就从网页上获取
        if (ipList.size() < MIN_IP_COUNT) {
            addList();
        }
        //延时50毫秒再抓取
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return pull(anonLevel);
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
    public abstract String parseAnonLevel(Element html);

    /**
     * 需要复写  解析类型
     *
     * @param html 请求的html页面
     * @return 返回类型
     */
    public abstract String parseType(Element html);

    /**
     * 需要复写  解析支持类型
     *
     * @param html 请求的html页面
     * @return 返回支持类型
     */
    public abstract String parseSupType(Element html);

    /**
     * 需要复写  解析ip所在位置
     *
     * @param html 请求的html页面
     * @return 返回ip所在位置
     */
    public abstract String parseLocation(Element html);

    /**
     * 需要复写  解析响应时间
     *
     * @param html 请求的html页面
     * @return 返回响应时间
     */
    public abstract Long parseSpeed(Element html);

    /**
     * 需要复写  解析ip已经存活时间
     *
     * @param html 请求的html页面
     * @return 返回ip已经存活时间
     */
    public abstract Long parseAge(Element html);

    /**
     * 需要复写  解析上次验证到现在的时间
     *
     * @param html 请求的html页面
     * @return 返回上次验证到现在的时间
     */
    public abstract Long parseLastverify(Element html);

    /**
     * 该方法用来检测代理ip是否可用
     *
     * @return
     */
    public boolean canUse(ProxyIp proxyIp) {
        System.out.println("start test ip: " + proxyIp);

        //检测原理：是否可以通过此代理ip访问网络资源

        CloseableHttpClient httpClient = HttpClients.createDefault();
        //设置超时时间
        try {
            //代理主机
            HttpHost proxyHost = new HttpHost(proxyIp.getIp(), proxyIp.getPort());
            RequestConfig config = RequestConfig.custom().setProxy(proxyHost).setConnectionRequestTimeout(1000).setConnectTimeout(1000).build();
            HttpGet httpGet = new HttpGet("https://www.baidu.com/duty/index.html");
            httpGet.setConfig(config);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: 2017/7/24  
            }
        }
    }

}
