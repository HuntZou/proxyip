package jhinwins;

import jhinwins.NetFilter.Impl.CMNetFilter;
import jhinwins.core.Action;
import jhinwins.core.impl.SimpleProxyIpSpider;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Jhinwins on 2017/8/24  17:53.
 * Desc:
 */
public class Application {

    public static void main(String[] params) {
        runXici(10, 30, 2);
    }

    /**
     * 运行西刺代理
     *
     * @param loadSourceInterval 加载源数据间隔（分）
     * @param detectionInterval  检测首个ip间隔（秒）
     * @param filterCount        过滤器个数
     */
    public static void runXici(final int loadSourceInterval, final int detectionInterval, final int filterCount) {

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
                        Thread.sleep((int) ((loadSourceInterval + Math.random()) * 60 * 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        for (int i = 0; i < filterCount; i++) {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        action.doNetFilter(new CMNetFilter(), "originalProxyIpPool", "CMProxyIpPool");
                    }
                }
            }).start();
        }

        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(detectionInterval * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    action.detectionFirst(new CMNetFilter(), "CMProxyIpPool");
                }
            }
        }).start();
    }

}
