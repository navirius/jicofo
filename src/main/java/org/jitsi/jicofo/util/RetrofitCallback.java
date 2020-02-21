package org.jitsi.jicofo.util;

import com.google.gson.Gson;
import org.jitsi.utils.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketTimeoutException;

public class RetrofitCallback<T> implements Callback<T> {

    private final static Logger logger = Logger.getLogger(RetrofitInstance.class);
    private final Callback<T> callback;


    public RetrofitCallback(Callback<T> callback) {
        this.callback = callback;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {

        logger.debug("onResponse :" + new Gson().toJson(response));
        logger.debug("onResponse :" + response.raw().request().url().toString());
        if (response.isSuccessful())
            HandleAppErr(call, response);
        else
            HandleErr(response);
        callback.onResponse(call, response);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t)
    {
        logger.debug("On Failure" + t);
        HandleFailureException(t);
        callback.onFailure(call, t);
    }

    /**
     * Handle Failure Case Exceptions Here SocketTimeoutException & Connection Error
     *
     * @param t
     */
    private void HandleFailureException(Throwable t) {
        StringWriter errors = new StringWriter();
        t.printStackTrace(new PrintWriter(errors));
        if (t instanceof SocketTimeoutException)
        {
            logger.error("Oops something went wrong, please try again. SocketTimeoutException");
        }
        else {
            t.printStackTrace();
        }
    }

    /**
     * App Level Error Handling
     *
     * @param response
     */
    private void HandleErr(Response<T> response) {
        int statusCode = response.code();

        logger.debug("STATUS CODE =" + statusCode);
        switch (statusCode) {
            case 404:
                logger.error("Error : Status Code - 404");
                break;
            case 500:
                logger.error("Error : Status Code - 500");
                break;
            default:
                logger.error("Error : Status Code - "+statusCode);
                break;
        }
    }

    /**
     * Handle Internal Project Response Codes
     *
     * @param call     CallBack to Handle Response Call
     * @param response return Response body
     */
    private void HandleAppErr(Call<T> call, Response<T> response) {
        String ErrMsg = "";
        String statusCode = null;
        try
        {
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(new Gson().toJson(response.body()));
            //JSONObject obj = new JSONObject(new Gson().toJson(response.body()));
            if (obj.containsKey("responseStatus") && obj.get("responseDetails") != null)
                ErrMsg = obj.get("responseDetails").toString();
            if (obj.containsKey("responseStatus") && obj.get("responseStatus") != null) {
                statusCode = obj.get("responseStatus").toString();
                logger.error( "HandleAppErr : " + statusCode + " " + ErrMsg);
                switch (Integer.parseInt(statusCode)) {
                    case 0:
                        break;
                    case 100:
                        if(ErrMsg!=null && !ErrMsg.isEmpty()) {
                            logger.error("HandleAppErr: " + ErrMsg);
                        }
                        break;
                    default:
                        logger.error("HandleAppError: "+ErrMsg);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}