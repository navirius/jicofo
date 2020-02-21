package org.jitsi.jicofo.auth;

import org.jitsi.jicofo.auth.model.UserTypeRequestModel;
import org.jitsi.jicofo.auth.model.UserTypeResponseModel;
import retrofit2.Call;
import retrofit2.http.Body;

public interface VirtualClassroomService {
    Call<UserTypeResponseModel> getUserType(@Body UserTypeRequestModel body);
}
