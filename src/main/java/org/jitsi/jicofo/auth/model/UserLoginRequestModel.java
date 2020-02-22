package org.jitsi.jicofo.auth.model;

import com.google.gson.annotations.SerializedName;

public class UserLoginRequestModel extends BaseRequestModel
{
    public UserLoginRequestModel(String userId, String password)
    {
        this.userId = userId;
        this.password=password;
    }
    @SerializedName("userId")
    String userId;
    @SerializedName("userPassword")
    String password;

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
