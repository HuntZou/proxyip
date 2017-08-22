package jhinwins.NetFilter.Impl;

import jhinwins.NetFilter.AbstractNetFilter;
import org.apache.http.client.methods.HttpPost;

import java.net.URI;

/**
 * Created by Jhinwins on 2017/8/21  10:02.
 * Desc:
 */
public class SimpleNetFilter extends AbstractNetFilter {
    public HttpPost setReq(HttpPost httpPost) {
        httpPost.setURI(URI.create("http://59.110.143.71:80/CMSpider4web/testProxyip"));
        return httpPost;
    }
}
