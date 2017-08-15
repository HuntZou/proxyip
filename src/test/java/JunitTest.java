import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jhinwins.core.FreeProxyIpSpider;
import jhinwins.core.Resource;
import jhinwins.core.impl.SimpleProxyIpSpider;
import jhinwins.model.ProxyIp;
import jhinwins.utils.IpUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jhinwins on 2017/7/24  17:17.
 * Desc:
 */
public class JunitTest {
    private static Logger logger = Logger.getLogger(JunitTest.class);
    @Test
    public void T1() throws InterruptedException {
        SimpleProxyIpSpider simpleProxyIpSpider = new SimpleProxyIpSpider("http://www.kuaidaili.com/free/inha/2/") {
            @Override
            public Elements parseIPHome(Document html) {
                Elements elements = html.select("tr");
                return elements;
            }

            @Override
            public String parseIP(Element element) {
                return element.child(0).text();
            }

            @Override
            public Integer parsePort(Element element) {
                return Integer.parseInt(element.child(1).text());
            }

//            @Override
//            public String parseAnonLevel(Element element) {
//                return element.select("td[data-title='匿名度']").first().text();
//            }
        };

        Resource.init(simpleProxyIpSpider);

        while (true) {
            ProxyIp ip = Resource.pull();
            System.out.println(ip.getIp() + ":" + ip.getPort());
            Thread.sleep(1000);
        }
    }

    @Test
    public void T2() {
        logger.info("this is info");
        logger.debug("this is debug");
    }

}
