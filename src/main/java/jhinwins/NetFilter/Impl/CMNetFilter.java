package jhinwins.NetFilter.Impl;

import jhinwins.NetFilter.AbstractNetFilter;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Created by Jhinwins on 2017/8/21  10:41.
 * Desc:
 */
public class CMNetFilter extends AbstractNetFilter {
    public PostMethod setReq(PostMethod httpPost) {
        try {
            httpPost.setURI(new URI("https://music.163.com/weapi/song/enhance/player/url?csrf_token="));
        } catch (URIException e) {
            logger.error("CMNetFilter.PostMethod,解析uri出错" + e.getMessage());
        }
        //设置所需要的加密参数
        String encSecKey = "21d9b6dd5ff2e852c38b47894aef0d728fd52cbd5c1d27f1c19732f61cf717d89cb5db5f4e05fcd19be528ef6178d693648c2705827963b4f9d77c0211f70ad50dd63c045f5a6fd6674b6338fa5ee6518c652f3d3391fba59fe54c5428cf57e23f928e8feb5cb64161df324f7f922c08688f99e8d9a852a20a7510f40fb5353e";
        String params = "GevewJNZ6FZTjW3Ys+OMNbl5dw7B3hMdo7bFwOcSQqNRi8S/3C/yfKpsrFzZzOG+aa7yoKPMmlbe5mSLDTysMUojfg6/Pl0E9WL2kM0guiQ=";

        httpPost.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=gbk");
        NameValuePair[] param = {new NameValuePair("encSecKey", encSecKey), new NameValuePair("params", params)};
        httpPost.setRequestBody(param);

        return httpPost;
    }
}
