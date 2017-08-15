package jhinwins.utils;

import com.alibaba.fastjson.JSONObject;
import jhinwins.core.Resource;
import jhinwins.model.ProxyIp;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Jhinwins on 2017/8/14  14:44.
 * Desc:
 */
public class IpUtils {
    /**
     * 连接超时
     */
    private static int CONNECTION_TIME = 10 * 1000;
    private static int CM_CONNECTION_TIME = 10 * 1000;

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
            RequestConfig config = RequestConfig.custom().setProxy(proxyHost).setConnectionRequestTimeout(3000).setConnectTimeout(CONNECTION_TIME).build();
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

    /**
     * 检测网易云是否可以使用
     *
     * @param proxyIp
     * @return
     */
    public static boolean canCMUse(ProxyIp proxyIp) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //设置超时时间
        try {
            HttpPost httpPost = new HttpPost("https://music.163.com/weapi/song/enhance/player/url?csrf_token=");

            //代理主机
            HttpHost proxyHost = new HttpHost(proxyIp.getIp(), proxyIp.getPort());
            RequestConfig config = RequestConfig.custom().setProxy(proxyHost).setConnectionRequestTimeout(3000).setConnectTimeout(CM_CONNECTION_TIME).build();
            httpPost.setConfig(config);
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.41 Safari/535.1 QQBrowser/6.9.11079.201");

            //设置所需要的加密参数
            String encSecKey = "77324bd21ff660dbf514815e87ce249354173832bcb0ecfe5723e95a79a207655e44c3868612b7836fd9c6a1e2ab9d4ae94731e5483ec7267bfff75286945c24dd4de8b2a07f19c8b9a121090bfb2aea8eef12a391d1e72477c471d852d9aec4079cd2047b1b51de6f666d86ca541ebfb70465fc0c4927a29396c9839c40217b";
            String params = "KFMjESiKsvQ%2F5lxgub9pLeow1DeXBI75Op7omiEfH7cLFZQZZ%2Ft07RYMFBw8GRrFKpw1G1NHFBfAbflbApB4JbDKKNnHJm0pVMXACY%2BX4%2FQ%3D";
            StringEntity stringEntity = new StringEntity("encSecKey=" + encSecKey + "&params=" + params);
            stringEntity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(stringEntity);

            long preT = System.currentTimeMillis();
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String entity = EntityUtils.toString(response.getEntity());

            int statusCode = response.getStatusLine().getStatusCode();

            System.out.println("检测ip：" + proxyIp.getIp() + ":" + proxyIp.getPort() + "----耗时：" + (System.currentTimeMillis() - preT) + "----statusCode:" + statusCode + "----entity:" + entity);

            if (statusCode == 200) {
                JSONObject parseObject = JSONObject.parseObject(entity);
                if (parseObject.getJSONArray("data").size() > 0 && 299757 == (parseObject.getJSONArray("data").getJSONObject(0).getInteger("id"))) {
                    return true;
                } else {
                    return false;
                }
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
