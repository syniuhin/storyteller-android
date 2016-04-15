package me.syniuhin.storyteller.net.service;

import me.syniuhin.storyteller.net.model.BasicResponse;
import me.syniuhin.storyteller.net.model.Story;
import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.*;
import rx.Observable;

/**
 * Created with love, by infm dated on 4/15/16.
 */
public interface StoryService {
  @Multipart
  @POST("image/upload")
  Observable<Response<BasicResponse>> uploadImage(
      @Part MultipartBody.Part file);

  @GET("image/{image_id}/story")
  Observable<Response<Story>> getStory(@Path("image_id") long imageId);
}