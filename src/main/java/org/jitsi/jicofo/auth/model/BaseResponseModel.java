package org.jitsi.jicofo.auth.model;

public class BaseResponseModel {
    boolean status;
    String message;
    String userIdRequest;

    public boolean isStatus()
    {
        return status;
    }

    public void setStatus(boolean status)
    {
        this.status = status;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getUserIdRequest()
    {
        return userIdRequest;
    }

    public void setUserIdRequest(String userIdRequest)
    {
        this.userIdRequest = userIdRequest;
    }
}
