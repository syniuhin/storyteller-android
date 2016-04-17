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
 * Created with love, by infm dated on 4/17/16.
 */
public class StoryServiceProxy implements StoryService {
  private StoryService mRealSubject = null;

  public StoryServiceProxy(StoryService realSubject) {
    mRealSubject = realSubject;
  }

  @Override
  public Observable<Response<BasicResponse>> uploadImage(
      @Part MultipartBody.Part file) {
    return mRealSubject.uploadImage(file);
  }

  @Override
  public Observable<Response<Story>> generateStory(
      @Path("image_id") long imageId) {
    return mRealSubject.generateStory(imageId);
  }

  @Override
  public Observable<Response<Story>> createStory(@Path("image_id") long imageId,
                                                 @Body Story story) {
    return mRealSubject.createStory(imageId, story);
  }

  @Override
  public Observable<Response<Story.Multiple>> getStoryList() {
    return mRealSubject.getStoryList();
  }

  @Override
  public Observable<Response<Story.Multiple>> getStoryListSince(
      @Path("unix_timestamp") long timestamp) {
    return mRealSubject.getStoryListSince(timestamp);
  }

  @Override
  public Observable<Response<Story.Multiple>> getStoryListAfter(
      @Path("after_id") long afterId) {
    return mRealSubject.getStoryListAfter(afterId);
  }
}
