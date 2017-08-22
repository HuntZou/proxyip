package jhinwins.Exception;

/**
 * Created by Jhinwins on 2017/8/21  9:03.
 * Desc:
 */
public class NoProxyIpException extends Exception{
    public NoProxyIpException(){
        super("没有可用ip异常");
    }
}
