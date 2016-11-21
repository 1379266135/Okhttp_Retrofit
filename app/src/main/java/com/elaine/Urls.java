package com.elaine;

/**
 * Created by elaine on 2016/11/17.
 */

public interface Urls {
    String authentication = "http://authentication.wangjiu.com";

    interface User{
        /** HTTP安全认证接口 */
        String generateSession = "/api/list/generateSession.json";
    }
}
