package org.jitsi.jicofo.auth.model;

import com.google.gson.annotations.SerializedName;

public class UserLoginResponModel extends BaseResponseModel
{
    @SerializedName("userType")
    String userType;
    @Override
    public boolean isStatus()
    {
        return status;
    }

    @Override
    public void setStatus(boolean status)
    {
        this.status = status;
    }

    public String getUserType()
    {
        return userType;
    }

    public void setUserType(String userType)
    {
        this.userType = userType;
    }
}
