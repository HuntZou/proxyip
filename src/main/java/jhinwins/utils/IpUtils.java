package jhinwins.utils;

import com.alibaba.fastjson.JSONObject;
import jhinwins.core.Resource;
import jhinwins.model.ProxyIp;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jhinwins on 2017/8/14  14:44.
 * Desc:
 */
public class IpUtils {
    private static Logger logger = Logger.getLogger(IpUtils.class);
    /**
     * 连接超时
     */
    private static int CONNECTION_TIME = 3 * 1000;
    private static int CM_CONNECTION_TIME = 3 * 1000;
    /**
     * 检测链接超时线程池
     */
    private static List<Thread> detectionTimeOutThreads = new ArrayList<Thread>();


    /**
     * ip不可用
     */
    public static int PROXY_IP_CANT_USE = -1;

    /**
     * 该方法用来检测代理ip是否可用
     *
     * @return
     */
    public static long canUse(ProxyIp proxyIp) {
        logger.info("检测ip：" + proxyIp.getIp());
        //检测原理：是否可以通过此代理ip访问网络资源

        CloseableHttpClient httpClient = HttpClients.createDefault();
        //设置超时时间
        try {
            //代理主机
            HttpGet httpGet = new HttpGet("http://59.110.143.71:80/CMSpider4web/testProxyip");

            HttpHost proxyHost = new HttpHost(proxyIp.getIp(), proxyIp.getPort());
            RequestConfig config = RequestConfig.custom().setProxy(proxyHost).setConnectionRequestTimeout(3000).setConnectTimeout(CONNECTION_TIME).setSocketTimeout(3000).build();
            httpGet.setConfig(config);

            long preT = System.currentTimeMillis();
            System.out.println("准备执行execute");
            CloseableHttpResponse response = httpClient.execute(httpGet);
            System.out.println("执行完毕");
            long connT = System.currentTimeMillis() - preT;
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return connT;
            } else {
                return PROXY_IP_CANT_USE;
            }
        } catch (Exception e) {
            logger.error("检测该ip" + proxyIp.getIp() + "不可用" + e.getMessage());
            return PROXY_IP_CANT_USE;
        } finally {
            try {
                if (httpClient != null)
                    httpClient.close();
            } catch (IOException e) {
                logger.error("检测ip发生重大异常" + e.getMessage());
                // TODO: 2017/7/24
            }
        }
    }

    static boolean flag = false;

    /**
     * 检测网易云是否可以使用
     *
     * @param proxyIp
     * @return
     */
    public static long canCMUse(ProxyIp proxyIp) {
        final CloseableHttpClient httpClient = HttpClients.createDefault();
        //设置超时时间
        try {
            HttpPost httpPost = new HttpPost("https://music.163.com/weapi/song/enhance/player/url?csrf_token=");

            //代理主机
            HttpHost proxyHost = new HttpHost(proxyIp.getIp(), proxyIp.getPort());
            RequestConfig config = RequestConfig.custom().setProxy(proxyHost).setConnectionRequestTimeout(1000).setConnectTimeout(CM_CONNECTION_TIME).setSocketTimeout(3000).build();
            httpPost.setConfig(config);
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.41 Safari/535.1 QQBrowser/6.9.11079.201");

            //设置所需要的加密参数
            String encSecKey = "77324bd21ff660dbf514815e87ce249354173832bcb0ecfe5723e95a79a207655e44c3868612b7836fd9c6a1e2ab9d4ae94731e5483ec7267bfff75286945c24dd4de8b2a07f19c8b9a121090bfb2aea8eef12a391d1e72477c471d852d9aec4079cd2047b1b51de6f666d86ca541ebfb70465fc0c4927a29396c9839c40217b";
            String params = "KFMjESiKsvQ%2F5lxgub9pLeow1DeXBI75Op7omiEfH7cLFZQZZ%2Ft07RYMFBw8GRrFKpw1G1NHFBfAbflbApB4JbDKKNnHJm0pVMXACY%2BX4%2FQ%3D";
            StringEntity stringEntity = new StringEntity("encSecKey=" + encSecKey + "&params=" + params);
            stringEntity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(stringEntity);

            final long preT = System.currentTimeMillis();
            flag = true;
            System.out.println("准备execute");
            Thread detectionTimeOutThread = new Thread(new Runnable() {
                public void run() {
                    while (flag) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                        if ((System.currentTimeMillis() - preT) > CM_CONNECTION_TIME) {
                            System.out.println(Thread.getAllStackTraces().size() + ":" + Thread.currentThread().getName() + "帮助关闭一个httpclient " + (System.currentTimeMillis() - preT));
                            try {
                                httpClient.close();
                            } catch (IOException e) {
                                logger.error("测试网易云ip时关闭httpclient出现异常" + e.getMessage());
                            } finally {
                                flag = false;
                            }
                        }
                    }
                }
            });
            detectionTimeOutThreads.add(detectionTimeOutThread);
            detectionTimeOutThread.start();
            CloseableHttpResponse response = httpClient.execute(httpPost);
            flag = false;
            System.out.println("完成");
            long conneT = System.currentTimeMillis() - preT;
            String entity = EntityUtils.toString(response.getEntity());

            int statusCode = response.getStatusLine().getStatusCode();

            logger.info("检测ip：" + proxyIp.getIp() + ":" + proxyIp.getPort() + "----耗时：" + conneT + "----statusCode:" + statusCode);

            if (statusCode == 200) {
                JSONObject parseObject = JSONObject.parseObject(entity);
                if (parseObject.getJSONArray("data").size() > 0 && 299757 == (parseObject.getJSONArray("data").getJSONObject(0).getInteger("id"))) {
                    return conneT;
                } else {
                    return PROXY_IP_CANT_USE;
                }
            } else {
                return PROXY_IP_CANT_USE;
            }
        } catch (Exception e) {
            logger.error("检测该ip" + proxyIp + "不可用" + e.getMessage());
            flag = false;
            return PROXY_IP_CANT_USE;
        } finally {
            flag = false;
            for (Thread detectionTimeOutThread : detectionTimeOutThreads) {
                if(detectionTimeOutThread.isAlive()){
                    detectionTimeOutThread.interrupt();
                }
            }
            try {
                if (httpClient != null)
                    httpClient.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
                // TODO: 2017/7/24
            }
        }
    }

}
