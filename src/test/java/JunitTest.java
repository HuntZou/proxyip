import jhinwins.NetFilter.Impl.CMNetFilter;
import jhinwins.NetFilter.Impl.SimpleNetFilter;
import jhinwins.Service.ProxyIpService;
import jhinwins.cache.SortSetOpt;
import jhinwins.core.Action;
import jhinwins.core.impl.SimpleProxyIpSpider;
import jhinwins.model.ProxyIp;
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
    public void tJedis() throws InterruptedException {

        while (true) {
            System.out.println("线程数：" + Thread.getAllStackTraces().size());
            Thread.sleep(1000);
        }

    }

    @Test
    public void tSpring() {

        final SimpleProxyIpSpider simpleProxyIpSpider = new SimpleProxyIpSpider("http://www.xicidaili.com/nn/") {
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

        final Action action = new Action();
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    action.loadOriginalSource(simpleProxyIpSpider);
                    try {
                        Thread.sleep(5 * 60 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        for (int i = 0; i < 2; i++) {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        action.doNetFilter(new CMNetFilter(), "originalProxyIpPool", "CMProxyIpPool");
                    }
                }
            }).start();
        }

        while (true) {
        }
    }

    @Test
    public void tPull() {
        for (int j = 0; j < 3; j++) {
            new Thread(new Runnable() {
                public void run() {
                    for (int i = 0; i < 1000; i++) {
                        ProxyIp pull = ProxyIpService.pull();
                        System.out.println(Thread.currentThread().getName() + " : " + pull.getIp());
                    }
                }
            }).start();
        }
        while (true) {
        }
    }

}
