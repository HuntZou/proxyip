package jhinwins.NetFilter.Impl;

import jhinwins.NetFilter.AbstractNetFilter;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.net.URI;

/**
 * Created by Jhinwins on 2017/8/21  10:41.
 * Desc:
 */
public class CMNetFilter extends AbstractNetFilter {
    public HttpPost setReq(HttpPost httpPost) {
        httpPost.setURI(URI.create("https://music.163.com/weapi/song/enhance/player/url?csrf_token="));
        //设置所需要的加密参数
        String encSecKey = "77324bd21ff660dbf514815e87ce249354173832bcb0ecfe5723e95a79a207655e44c3868612b7836fd9c6a1e2ab9d4ae94731e5483ec7267bfff75286945c24dd4de8b2a07f19c8b9a121090bfb2aea8eef12a391d1e72477c471d852d9aec4079cd2047b1b51de6f666d86ca541ebfb70465fc0c4927a29396c9839c40217b";
        String params = "KFMjESiKsvQ%2F5lxgub9pLeow1DeXBI75Op7omiEfH7cLFZQZZ%2Ft07RYMFBw8GRrFKpw1G1NHFBfAbflbApB4JbDKKNnHJm0pVMXACY%2BX4%2FQ%3D";
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity("encSecKey=" + encSecKey + "&params=" + params);
        } catch (UnsupportedEncodingException e) {
            logger.error("过滤CMip时发生不支持编码异常" + e.getMessage());
            return null;
        }
        stringEntity.setContentType("application/x-www-form-urlencoded");
        httpPost.setEntity(stringEntity);
        return httpPost;
    }
}
