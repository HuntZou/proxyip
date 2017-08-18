import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jhinwins.core.FreeProxyIpSpider;
import jhinwins.core.Resource;
import jhinwins.core.impl.SimpleProxyIpSpider;
import jhinwins.model.ProxyIp;
import jhinwins.utils.IpUtils;
import jhinwins.utils.RedisUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Jhinwins on 2017/7/24  17:17.
 * Desc:
 */
public class JunitTest {

    @Test
    public void T1() throws InterruptedException {
        SimpleProxyIpSpider simpleProxyIpSpider2 = new SimpleProxyIpSpider("http://www.xicidaili.com/nn/") {
            @Override
            public Elements parseIPHome(Document html) {
                Elements elements = html.select("tr");
                return elements;
            }

            @Override
            public String parseIP(Element element) {
                return element.child(1).text();
            }

            @Override
            public Integer parsePort(Element element) {
                return Integer.parseInt(element.child(2).text());
            }

//            @Override
//            public String parseAnonLevel(Element element) {
//                return element.select("td[data-title='匿名度']").first().text();
//            }
        };
        Resource.init(simpleProxyIpSpider2);

        for (int i = 0; i < 10; i++) {
            Resource.pull();
            Thread.sleep(3000);
        }

        while (true) {
        }
    }

    @Test
    public void tJedis() throws InterruptedException {

        while (true) {
            System.out.println("线程数：" + Thread.getAllStackTraces().size());
            Thread.sleep(1000);
        }

    }

}
