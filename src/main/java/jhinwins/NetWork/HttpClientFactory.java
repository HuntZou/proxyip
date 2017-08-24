package jhinwins.NetWork;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

/**
 * Created by Jhinwins on 2017/8/11  10:54.
 * Desc:
 */
public class HttpClientFactory {
    private static HttpClient httpClient;
    private static HttpClient ProxyHttpClient;
    private final static Object syLock = new Object();

    /**
     * 获取单例httpclient实例
     */
    public static synchronized HttpClient getHttpClient() {
        return createHttpClient(httpClient);
    }

    public static synchronized HttpClient getProxyHttpClient() {
        return createHttpClient(ProxyHttpClient);
    }

    private static synchronized HttpClient createHttpClient(HttpClient httpClient) {
        if (httpClient == null) {
            synchronized (syLock) {
                if (httpClient == null) {
                    //创建多线程安全的httpclient
                    MultiThreadedHttpConnectionManager mthcm = new MultiThreadedHttpConnectionManager();
                    HttpConnectionManagerParams params = mthcm.getParams();
                    params.setConnectionTimeout(5000);
                    params.setSoTimeout(5000);
                    params.setDefaultMaxConnectionsPerHost(10);
                    params.setMaxTotalConnections(1000);
                    params.setParameter(HttpClientParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));

                    httpClient = new HttpClient(mthcm);
                }
            }
        }
        return httpClient;
    }

}
