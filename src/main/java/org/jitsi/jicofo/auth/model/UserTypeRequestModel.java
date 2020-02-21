package org.jitsi.jicofo.auth.model;

import com.google.gson.annotations.SerializedName;

public class UserTypeRequestModel extends BaseRequestModel {
    @SerializedName("userId")
    String userId;
    @SerializedName("classroomId")
    String classroomId;

    public UserTypeRequestModel(String userId, String classroomId)
    {
        this.userId = userId;
        this.classroomId = classroomId;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }
}
