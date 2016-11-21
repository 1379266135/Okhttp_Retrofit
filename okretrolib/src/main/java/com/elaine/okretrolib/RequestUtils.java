package com.elaine.okretrolib;

import android.content.Context;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by elaine on 2016/11/18.
 */

public class RequestUtils {

    public static final int TIMEOUT = 30;
    public static int TYPE_NORMAL = 0;
    public static int TYPE_LIST = 1;
    public static int TYPE_MODEL = 2;
    public static int TYPE_POST = 3;

    public static OkHttpClient okHttpClient;
    private static HashMap<String, String> baseInfo;
    private static Retrofit retrofit;

    private static RequestUtils mInstance;

    public static RequestUtils getInstance() {

        if (null == mInstance) {
            return new RequestUtils();
        }
        return mInstance;
    }

    public <T> void loadByGet(final Class<T> tClass, String url, Map<String, String> map, final CallbackListener listener) {
        callEnqueue(TYPE_NORMAL, tClass, url, map, listener);
    }

    public <T> void loadByPost(final Class<T> tClass, String url, Map<String, String> map, final CallbackListener listener) {
        callEnqueue(TYPE_POST, tClass, url, map, listener);
    }

    public <T> void callEnqueue(final int type, final Class<T> tClass, String url, Map<String, String> map, final CallbackListener listener) {
        Call<JsonObject> call = null;
        if (map == null) {
            map = new HashMap<>();
        }
        if (baseInfo != null) {
            map.putAll(baseInfo);
        }

        try {
            if (type == TYPE_POST) {//POST
                call = ((RetrofitService) retrofit.create(RetrofitService.class)).postData(url, map);
            } else {//GET
                call = ((RetrofitService) retrofit.create(RetrofitService.class)).getData(url, map);
            }
        } catch (Exception e){
            Log.e("error", "请在application中，执行RequestUtils.init() 方法");
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    try {
                        JsonObject json = response.body();

                        if (json.get("status").getAsInt() >= 0) {

                            if (json.has("result") && json.get("result").isJsonArray()) { // 返回数据类型为--> List（集合）
                                String jsonStr = json.get("result").toString();
                                Log.e("Okhttp", "response data = " + jsonStr);
                                JSONArray array = new JSONArray(jsonStr);
                                ArrayList<T> datas = new ArrayList<T>();
                                for (int i = 0; i < array.length(); i++) {
                                    T bean = new GsonBuilder().create().fromJson(array.get(i).toString(), tClass);
                                    datas.add(bean);
                                }
                                listener.onSuccess(datas, json.get("status").getAsString());

                            } else if (json.has("result") && json.get("result").isJsonObject()) { // 返回数据类型为--> Object（单个实体bean）
                                T data = new GsonBuilder().create().fromJson(json.get("result"), tClass);
                                listener.onSuccess(data, json.get("status").getAsString());

                            } else {
                                String error = json.get("message").getAsString();
                                listener.onFail(error);
                            }

                        } else {
                            listener.onFail(json.get("message").getAsString());
                        }
                    } catch (Exception e) {
                        listener.onFail(e.getMessage());
                    }
                } else {
                    listener.onFail(response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                listener.onFail(t.getMessage());
            }
        });
    }

    public void uploadFile(String url, Map<String, RequestBody> map, final CallbackListener listener) {
        Call<JsonObject> call = ((RetrofitService) retrofit.create(RetrofitService.class)).uploadFile(url, map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    try {
                        JsonObject json = response.body();

                        if (json.get("status").getAsInt() >= 0) {
                            if (json.has("result") && json.get("result").isJsonObject()) {//单张图片 -返回图片路径

                                //                                listener.onSuccess(json.getAsJsonObject("result"), json.get("message").getAsString());

                                JsonObject result = json.getAsJsonObject("result");
                                if (result.has("url")) {
                                    listener.onSuccess(result.get("url").getAsString(), json.get("message").getAsString());

                                } else {
                                    listener.onSuccess(null, json.get("message").getAsString());
                                }
                            } else {
                                listener.onSuccess(null, json.get("message").getAsString());
                            }
                        } else {
                            listener.onFail(json.get("message").getAsString());
                        }
                    } catch (Exception e) {
                        listener.onFail(e.getMessage());
                    }
                } else {
                    listener.onFail(response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                listener.onFail(t.getMessage());
            }
        });
    }

    public RequestBody parseRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    public RequestBody parseImageRequestBody(File file) {
        return RequestBody.create(MediaType.parse("image/jpg"), file);
    }

    public String parseImageMapKey(String key, String fileName) {
        return key + "\"; filename=\"" + fileName;
    }

    public static void init(Context context, boolean isDebug) {
        if (baseInfo == null || baseInfo.size() < 4) {
            baseInfo = new HashMap<>();
            String channel = DevicesUtils.getChannelName(context);

            baseInfo.put("CHANNEL", channel);
            baseInfo.put("port", "AndroidPhone");
            baseInfo.put("os", DevicesUtils.getAndroidSystemVersion());
            baseInfo.put("version_code", String.valueOf(DevicesUtils.getAppVersion(context)));
            baseInfo.put("format", "json");
            baseInfo.put("client_sig", "andriodphone");
            baseInfo.put("COOKIE_CPS_ID", DevicesUtils.getCPSID(context).toString());
        }

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i("OkHttp", message);
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        if (isDebug) {
            okHttpBuilder.addNetworkInterceptor(loggingInterceptor);
            okHttpBuilder.addInterceptor(loggingInterceptor);//网络和日志拦截
        }

        okHttpBuilder.connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS);//设置请求超时

        okHttpClient = okHttpBuilder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://authentication.wangjiu.com")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
