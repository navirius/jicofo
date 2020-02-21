package org.jitsi.jicofo.auth.model;

public class BaseRequestModel {
    String userIdRequest;

    public String getUserIdRequest()
    {
        return userIdRequest;
    }

    public void setUserIdRequest(String userIdRequest)
    {
        this.userIdRequest = userIdRequest;
    }
}
