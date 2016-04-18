package me.syniuhin.storyteller.net.service.api;

import me.syniuhin.storyteller.net.model.BasicResponse;
import me.syniuhin.storyteller.net.model.Story;
import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created with love, by infm dated on 4/18/16.
 */
public class StoryServiceDemoAdapter implements StoryService {

  private DemoAccessService mAdaptee;

  public StoryServiceDemoAdapter(final DemoAccessService adaptee) {
    mAdaptee = adaptee;
  }

  @Override
  public Observable<Response<BasicResponse>> uploadImage(
      @Part MultipartBody.Part file) {
    return mAdaptee.uploadImage(file);
  }

  @Override
  public Observable<Response<Story>> generateStory(long imageId) {
    return mAdaptee.generateStory(imageId);
  }

  @Override
  public Observable<Response<Story>> createStory(@Path("image_id") long imageId,
                                                 @Body Story story) {
    throw new IllegalStateException("Not supported in demo!");
  }

  @Override
  public Observable<Response<Story.Multiple>> getStoryList() {
    throw new IllegalStateException("Not supported in demo!");
  }

  @Override
  public Observable<Response<Story.Multiple>> getStoryListSince(
      @Path("unix_timestamp") long timestamp) {
    throw new IllegalStateException("Not supported in demo!");
  }

  @Override
  public Observable<Response<Story.Multiple>> getStoryListAfter(
      @Path("after_id") long afterId) {
    throw new IllegalStateException("Not supported in demo!");
  }
}
