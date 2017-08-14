package jhinwins.utils;

import jhinwins.model.ProxyIp;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

/**
 * Created by Jhinwins on 2017/8/14  14:44.
 * Desc:
 */
public class IpUtils {

    /**
     * 该方法用来检测代理ip是否可用
     *
     * @return
     */
    public static boolean canUse(ProxyIp proxyIp) {
        //检测原理：是否可以通过此代理ip访问网络资源

        CloseableHttpClient httpClient = HttpClients.createDefault();
        //设置超时时间
        try {
            //代理主机
            HttpGet httpGet = new HttpGet("http://59.110.143.71:80/CMSpider4web/testProxyip");

            HttpHost proxyHost = new HttpHost(proxyIp.getIp(), proxyIp.getPort());
            RequestConfig config = RequestConfig.custom().setProxy(proxyHost).setConnectionRequestTimeout(1000).setConnectTimeout(1000).build();
            httpGet.setConfig(config);

            CloseableHttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (httpClient != null)
                    httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: 2017/7/24
            }
        }
    }
}
