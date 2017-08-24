package jhinwins.NetFilter;

import com.sun.deploy.net.HttpResponse;
import jhinwins.NetWork.HttpClientFactory;
import jhinwins.model.ProxyIp;
import jhinwins.utils.NetUtils;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public abstract PostMethod setReq(PostMethod httpPost);

    /**
     * 执行过滤的方法
     *
     * @param proxyIp
     * @return http响应
     */
    public PostMethod doFilter(ProxyIp proxyIp) {

        if (proxyIp == null) {
            return null;
        }

        HttpClient httpClient = HttpClientFactory.getProxyHttpClient();

        PostMethod httpPost = new PostMethod("http://59.110.143.71:80/CMSpider4web/testProxyip");
        try {
            //模拟浏览器请求头
            List<Header> headers = new ArrayList<Header>();
            headers.add(new Header("User-Agent", NetUtils.getUserAgent()));
            httpClient.getHostConfiguration().getParams().setParameter("http.default-headers", headers);

            //使用代理ip
            httpClient.getHostConfiguration().setProxy(proxyIp.getIp(), proxyIp.getPort());

            httpPost = setReq(httpPost);

            //判断设置是否有效
            if (httpPost == null || httpPost.getURI() == null || httpPost.getURI().getHost() == null || httpPost.getURI().getHost().length() == 0) {
                return null;
            }

            try {
                System.out.println(Thread.currentThread().getName() + "执行httpClient.execute方法");
                httpClient.executeMethod(httpPost);
                System.out.println(Thread.currentThread().getName() + "执行httpClient.execute方法完毕");
                return httpPost;
            } catch (IOException e) {
                System.out.println(Thread.currentThread().getName() + "执行httpClient.execute方法发生异常" + e.getMessage());
                return null;
            }
        } catch (URIException e) {
            logger.error("AbstractNetFilter.doFilter发生异常" + e.getMessage());
        } finally {
            if (httpPost != null)
                httpPost.releaseConnection();
        }
        return null;
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
