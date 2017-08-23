package jhinwins.NetWork;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * Created by Jhinwins on 2017/8/11  10:54.
 * Desc:
 */
public class HttpClientFactory {
    private static CloseableHttpClient httpClient;
    private final static Object syLock = new Object();

    /**
     * 获取单例httpclient实例
     */
    public static synchronized CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (syLock) {
                if (httpClient == null) {
                    //创建多线程安全的httpclient
                    Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("http", PlainConnectionSocketFactory.getSocketFactory())
                            .register("https", SSLConnectionSocketFactory.getSocketFactory())
                            .build();
                    PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

                    RequestConfig defaultRequestConfig = RequestConfig.custom()
                            .setSocketTimeout(5000)
                            .setConnectTimeout(5000)
                            .setConnectionRequestTimeout(5000)
                            .setStaleConnectionCheckEnabled(true)
                            .build();

                    HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
                        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                            return false;
                        }
                    };

                    httpClient = HttpClients.custom().setConnectionManager(httpClientConnectionManager).setDefaultRequestConfig(defaultRequestConfig).setRetryHandler(myRetryHandler).build();
                }
            }
        }
        return httpClient;
    }

}
