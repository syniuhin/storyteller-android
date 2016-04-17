package me.syniuhin.storyteller.net.service.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import me.syniuhin.storyteller.BaseActivity;
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
public class StoryServiceProxyBridge implements StoryService {
  private StoryService mImpl = null;
  private Context mContext;
  private final SharedPreferences mSp;
  private View mParentView;

  public StoryServiceProxyBridge(StoryService impl, Context context,
                                 View parentView) {
    mImpl = impl;
    mContext = context;
    mSp = mContext.getSharedPreferences(BaseActivity.PREFS_KEY,
                                        Context.MODE_PRIVATE);
    mParentView = parentView;
  }

  public void setImplementation(StoryService impl) {
    mImpl = impl;
  }

  @Override
  public Observable<Response<BasicResponse>> uploadImage(
      @Part MultipartBody.Part file) {
    // Doesn't require authentication
    if (!checkInternetConnection())
      return null;
    return mImpl.uploadImage(file);
  }

  @Override
  public Observable<Response<Story>> generateStory(long imageId) {
    if (!checkInternetConnection())
      return null;
    if (!checkCredentialsAvailability() && !checkIfCredentialsFake())
      return null;
    return mImpl.generateStory(imageId);
  }

  @Override
  public Observable<Response<Story>> createStory(@Path("image_id") long imageId,
                                                 @Body Story story) {
    if (!checkInternetConnection())
      return null;
    if (!checkCredentialsAvailability())
      return null;
    return mImpl.createStory(imageId, story);
  }

  @Override
  public Observable<Response<Story.Multiple>> getStoryList() {
    if (!checkInternetConnection())
      return null;
    if (!checkCredentialsAvailability())
      return null;
    return mImpl.getStoryList();
  }

  @Override
  public Observable<Response<Story.Multiple>> getStoryListSince(
      @Path("unix_timestamp") long timestamp) {
    if (!checkInternetConnection())
      return null;
    if (!checkCredentialsAvailability())
      return null;
    return mImpl.getStoryListSince(timestamp);
  }

  @Override
  public Observable<Response<Story.Multiple>> getStoryListAfter(
      @Path("after_id") long afterId) {
    if (!checkInternetConnection())
      return null;
    if (!checkCredentialsAvailability())
      return null;
    return mImpl.getStoryListAfter(afterId);
  }

  private boolean checkInternetConnection() {
    ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(
        Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    if (netInfo != null && netInfo.isConnectedOrConnecting())
      return true;
    Snackbar.make(mParentView, "Internet connection error",
                  Snackbar.LENGTH_LONG).show();
    return false;
  }

  private boolean checkCredentialsAvailability() {
    if (TextUtils.isEmpty(mSp.getString("basicAuthHeader", "")) ||
        checkIfCredentialsFake()) {
      if (checkIfCredentialsFake())
        Snackbar.make(mParentView, "Available only for registered users",
                      Snackbar.LENGTH_LONG).show();
      else
        Snackbar.make(mParentView, "You shouldn't see this!",
                      Snackbar.LENGTH_LONG).show();
      return false;
    }
    return true;
  }

  private boolean checkIfCredentialsFake() {
    return BaseActivity.isDemoRunning(mSp);
  }
}
