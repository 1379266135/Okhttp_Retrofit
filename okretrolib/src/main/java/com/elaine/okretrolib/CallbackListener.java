package com.elaine.okretrolib;

import java.util.List;

/**
 * Created by elaine on 2016/11/18.
 */

public interface CallbackListener<T> {

    void onSuccess(T data, String msg);
    void onSuccess(List<T> data, String msg);
    void onFail(String msg);

}
