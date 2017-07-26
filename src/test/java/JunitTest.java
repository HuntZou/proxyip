import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jhinwins.core.FreeProxyIpSpider;
import jhinwins.core.impl.SimpleProxyIpSpider;
import jhinwins.model.ProxyIp;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
    @Test
    public void T1() {
        SimpleProxyIpSpider simpleProxyIpSpider = new SimpleProxyIpSpider("http://www.kuaidaili.com/free/inha/2/") {
            @Override
            public Elements parseIPHome(Document html) {
                Elements elements = html.select("tr");
                System.out.println("ipHome size : " + elements.size());
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

        while (simpleProxyIpSpider.canUse(simpleProxyIpSpider.pull())) {
            System.out.println("bingo");
        }
    }

    @Test
    public void T2() {
        int count = 0;
        while (true) {
            System.out.println("had test: " + ++count);
            ProxyIp ip = new ProxyIp();
            ip.setIp("115.221.124.120");
            ip.setPort(48110);
            boolean canUse = FreeProxyIpSpider.canUse(ip);
            System.out.println("canUse:" + canUse);
        }
    }

    @Test
    public void T3() throws IOException {
        List<ProxyIp> proxyips = new ArrayList<ProxyIp>();

        CloseableHttpClient httpClient = null;
        BufferedReader reader = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("http://www.xdaili.cn/ipagent//freeip/getFreeIps");
            CloseableHttpResponse response = httpClient.execute(httpGet);
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String res = "";
            String buff;
            while ((buff = reader.readLine()) != null) {
                res += buff;
            }

            JSONObject res_json = JSON.parseObject(res);
            JSONArray ips = res_json.getJSONArray("rows");
            Iterator<Object> iterator = ips.iterator();

            while (iterator.hasNext()) {
                JSONObject ipItem = (JSONObject) iterator.next();
                String ip = ipItem.getString("ip");
                String port_str = ipItem.getString("port");
                Integer port = Integer.parseInt(port_str);

                ProxyIp proxyIp = new ProxyIp();
                proxyIp.setIp(ip);
                proxyIp.setPort(port);
                proxyips.add(proxyIp);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    // do nothing.
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    // do nothing.
                }
            }
        }

        System.out.println("size is " + proxyips.size());

    }
}
