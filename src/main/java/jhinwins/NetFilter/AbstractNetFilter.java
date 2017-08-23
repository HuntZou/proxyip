package jhinwins.NetFilter;

import jhinwins.NetWork.HttpClientFactory;
import jhinwins.model.ProxyIp;
import jhinwins.utils.NetUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by Jhinwins on 2017/8/18  15:28.
 * Desc:
 */
public abstract class AbstractNetFilter {
    public Logger logger = Logger.getLogger(AbstractNetFilter.class);
    private int connectionRequestTimeout = 1000;
    private int connectTimeout = 3000;
    private int socketTimeout = 3000;

    /**
     * 设置请求的uri   请求体等内容
     *
     * @param httpPost
     * @return
     */
    public abstract HttpPost setReq(HttpPost httpPost);

    /**
     * 执行过滤的方法
     *
     * @param proxyIp
     * @return http响应
     */
    public HttpResponse doFilter(ProxyIp proxyIp) {

        if (proxyIp == null) {
            return null;
        }

        CloseableHttpClient httpClient = HttpClientFactory.getHttpClient();

        HttpPost httpPost = new HttpPost("http://59.110.143.71:80/CMSpider4web/testProxyip");
        try {
            //模拟浏览器请求头
            httpPost.setHeader("User-Agent", NetUtils.getUserAgent());

            //使用代理ip
            HttpHost proxyHost = new HttpHost(proxyIp.getIp(), proxyIp.getPort());
            RequestConfig requestConfig = RequestConfig.custom().setProxy(proxyHost).setSocketTimeout(getSocketTimeout()).setConnectTimeout(getConnectTimeout()).setConnectionRequestTimeout(getConnectionRequestTimeout()).build();
            httpPost.setConfig(requestConfig);

            httpPost = setReq(httpPost);

            //判断设置是否有效
            if (httpPost == null || httpPost.getURI() == null || httpPost.getURI().getHost() == null || httpPost.getURI().getHost().length() == 0) {
                return null;
            }

            HttpResponse response;
            try {
                System.out.println(Thread.currentThread().getName() + "执行httpClient.execute方法");
                response = httpClient.execute(httpPost);
                System.out.println(Thread.currentThread().getName() + "执行httpClient.execute方法完毕");
                return response;
            } catch (IOException e) {
                System.out.println(Thread.currentThread().getName() + "执行httpClient.execute方法发生异常" + e.getMessage());
                return null;
            }
        } finally {
            if (httpPost != null)
                httpPost.releaseConnection();
        }
    }

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }
}
