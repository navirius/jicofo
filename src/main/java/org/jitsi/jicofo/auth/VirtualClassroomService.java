package org.jitsi.jicofo.auth;

import org.jitsi.jicofo.auth.model.UserLoginRequestModel;
import org.jitsi.jicofo.auth.model.UserLoginResponModel;
import org.jitsi.jicofo.auth.model.UserTypeRequestModel;
import org.jitsi.jicofo.auth.model.UserTypeResponseModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface VirtualClassroomService {
    @POST("/get-user-type")
    Call<UserTypeResponseModel> getUserType(@Body UserTypeRequestModel body);
    @POST("/user-login")
    Call<UserLoginResponModel> userLogin(@Body UserLoginRequestModel body);
}
