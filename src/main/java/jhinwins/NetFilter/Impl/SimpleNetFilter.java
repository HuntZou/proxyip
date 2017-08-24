package jhinwins.NetFilter.Impl;

import jhinwins.NetFilter.AbstractNetFilter;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Created by Jhinwins on 2017/8/21  10:02.
 * Desc:
 */
public class SimpleNetFilter extends AbstractNetFilter {
    public PostMethod setReq(PostMethod httpPost) {
        try {
            httpPost.setURI(new URI("http://59.110.143.71:80/CMSpider4web/testProxyip"));
        } catch (URIException e) {
        }
        return httpPost;
    }
}
