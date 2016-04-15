package me.syniuhin.storyteller.net.service;

import me.syniuhin.storyteller.net.model.BasicResponse;
import me.syniuhin.storyteller.net.model.User;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;


/**
 * infm created it with love on 4/15/16. Enjoy ;)
 */
public interface UserService {
  @POST("user/login")
  Observable<Response<BasicResponse>> login(@Body User user);

  @POST("user/register")
  Observable<Response<BasicResponse>> register(@Body User user);
}
