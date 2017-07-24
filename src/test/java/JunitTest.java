import jhinwins.core.impl.SimpleProxyIpSpider;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

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
}
