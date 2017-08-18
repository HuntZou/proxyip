import jhinwins.core.Resource;
import jhinwins.core.impl.SimpleProxyIpSpider;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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

    @Test
    public void tSpring() {
    }

}
