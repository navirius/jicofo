package org.jitsi.jicofo.auth.model;

import com.google.gson.annotations.SerializedName;

public class UserLoginRequestModel extends BaseRequestModel
{
    public UserLoginRequestModel(String userId, String password, String sessionId, String conferenceId, String roomName)
    {
        this.userId = userId;
        this.password = password;
        this.sessionId = sessionId;
        this.conferenceId = conferenceId;
        this.roomName = roomName;

    }
    @SerializedName("userId")
    String userId;
    @SerializedName("userPassword")
    String password;
    @SerializedName("sessionId")
    String sessionId;
    @SerializedName("conferenceId")
    String conferenceId;
    @SerializedName("roomName")
    String roomName;
    public String getConferenceId()
    {
        return conferenceId;
    }

    public void setConferenceId(String conferenceId)
    {
        this.conferenceId = conferenceId;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

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
