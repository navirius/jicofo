package org.jitsi.jicofo.auth.model;

import com.google.gson.annotations.SerializedName;

public class UserTypeResponseModel extends BaseResponseModel{
    @SerializedName("userType")
    String userType;
    @SerializedName("userId")
    String userId;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
