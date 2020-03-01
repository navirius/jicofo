package org.jitsi.jicofo.util;

//import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import org.jitsi.jicofo.auth.AbstractAuthAuthority;
import org.jitsi.utils.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RetrofitInstance {
    private static Retrofit retrofitRegistration = null;
    private static Retrofit retrofit = null;
    private static Retrofit retrofitChat = null;
    private static Retrofit retrofitImage = null;
    private static OkHttpClient.Builder builder = null;
    private static final String TAG = RetrofitInstance.class.getSimpleName();
    private final static Logger logger = Logger.getLogger(RetrofitInstance.class);

    /**
     * @return Retrofit Instance
     */
    public static Retrofit getInstance() {

        if (retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create()))
                    .baseUrl(AppConstants.VCLASROOM_SERVICE_URL)
                    .client(getHeaders().build())
                    .build();
        }
        return retrofit;
    }
    public static Retrofit createInstance() {
        Retrofit  retrofit2 = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create()))
                .baseUrl(AppConstants.VCLASROOM_SERVICE_URL)
                .build();

        return retrofit2;
    }


    /**
     * @return Interceptor , Added Authorization Interceptor
     */
    private static OkHttpClient.Builder getHeaders() {
        builder = new OkHttpClient().newBuilder()
                .connectTimeout(AppConstants.AppTimeOut, TimeUnit.SECONDS)
                .readTimeout(AppConstants.AppTimeOut, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.AppTimeOut, TimeUnit.SECONDS)
                //.addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(createLoggingBodyInterceptor())
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        /**
                         * If user Authenticated Add Authorization Header
                         * else Proceed with original Request
                         */
                        RequestBody requestBody = JsonMaker(original.body());
                        Request.Builder requestBuilder = original.newBuilder();

                        if (requestBody != null) {
                            logger.debug("intercept: " + new Gson().toJson(requestBody));
                            original = requestBuilder
                                    .header("content-type", "application/json")
                                    .method(original.method(), requestBody)
                                    .build();
                        }
                        return chain.proceed(original);
                    }
                });

        builder = UnsafeOkHttpClient.getUnsafeOkHttpClientBuilder(builder);
        return builder;
    }

    private static RequestBody JsonMaker(RequestBody requestBody) {
        String jsonStr = new Gson().toJson(requestBody);
        logger.debug("RequestBody_JsonMaker : " + jsonStr);
        try {
            JSONObject obj;
            if (RequestOriginal(requestBody).equals("") || RequestOriginal(requestBody).equals("did not work") || RequestOriginal(requestBody).length() == 0)
                obj = new JSONObject();
            else
            {
                JSONParser parser = new JSONParser();
                obj = (JSONObject) parser.parse(RequestOriginal(requestBody));
            }
                //obj = new JSONObject(RequestOriginal(requestBody));
//            obj.put("requestCategory", requestCategory);
//            obj.put("requestFrom", requestFrom);
//            obj.put("requestRole", requestRole);
//            obj.put("requestMsisdn", AppUtils.getMsisdn(mSharedPreferences));
//            obj.put("requestRecCategory",AppUtils.getUserRecCategory(mSharedPreferences));
//            obj.put("requestAliasName",AppUtils.getAliasName(mSharedPreferences));
            obj.put("appDetails", getFooter());
            jsonStr = new Gson().toJson(obj);
            logger.debug("RequestBody_JsonMakerResult : "+jsonStr);
            return RequestBody.create(requestBody.contentType(), obj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static JSONObject getFooter(){
        JSONObject footer = new JSONObject();
        footer.put("appName", "jicofo");
        footer.put("appLanguage", "EN");
        footer.put("appVersion", "1.0");
        return footer;
    }

    private static String RequestOriginal(RequestBody request) {
        try {
            RequestBody copy = request;
            Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (IOException e) {
            return "did not work";
        }
    }

    private static HttpLoggingInterceptor createLoggingBodyInterceptor()
    {
        HttpLoggingInterceptor loggin=new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger()
        {
            @Override
            public void log(String message)
            {
                logger.debug("HTTPLogging : "+message);
            }
        });
        loggin.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggin;
    }
    private static String bodyToString(final RequestBody request)
    {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if(copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        }
        catch (final IOException e) {
            return "did not work";
        }
    }
}
