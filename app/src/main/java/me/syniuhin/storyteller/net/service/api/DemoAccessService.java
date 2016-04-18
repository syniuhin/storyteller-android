package me.syniuhin.storyteller.net.service.api;

import me.syniuhin.storyteller.net.model.BasicResponse;
import me.syniuhin.storyteller.net.model.Story;
import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.*;
import rx.Observable;

/**
 * Created with love, by infm dated on 4/18/16.
 */
public interface DemoAccessService {
  @Multipart
  @POST("demo/image/upload")
  Observable<Response<BasicResponse>> uploadImage(
      @Part MultipartBody.Part file);

  @GET("demo/image/{image_id}/story")
  Observable<Response<Story>> generateStory(@Path("image_id") long imageId);
}
