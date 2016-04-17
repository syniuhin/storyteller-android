package me.syniuhin.storyteller.net.service.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
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
public class StoryServiceProxy implements StoryService {
  private StoryService mRealSubject = null;
  private Context mContext;
  private final SharedPreferences mSp;
  private View mParentView;

  public StoryServiceProxy(StoryService realSubject, Context context,
                           View parentView) {
    mRealSubject = realSubject;
    mContext = context;
    mSp = PreferenceManager.getDefaultSharedPreferences(mContext);
    mParentView = parentView;
  }

  @Override
  public Observable<Response<BasicResponse>> uploadImage(
      @Part MultipartBody.Part file) {
    // Doesn't require authentication
    if (!checkInternetConnection())
      return null;
    return mRealSubject.uploadImage(file);
  }

  @Override
  public Observable<Response<Story>> generateStory(
      @Path("image_id") long imageId) {
    if (!checkInternetConnection())
      return null;
    if (!checkCredentialsAvailability() && !checkIfCredentialsFake())
      return null;
    return mRealSubject.generateStory(imageId);
  }

  @Override
  public Observable<Response<Story>> createStory(@Path("image_id") long imageId,
                                                 @Body Story story) {
    if (!checkInternetConnection())
      return null;
    if (!checkCredentialsAvailability())
      return null;
    return mRealSubject.createStory(imageId, story);
  }

  @Override
  public Observable<Response<Story.Multiple>> getStoryList() {
    if (!checkInternetConnection())
      return null;
    if (!checkCredentialsAvailability())
      return null;
    return mRealSubject.getStoryList();
  }

  @Override
  public Observable<Response<Story.Multiple>> getStoryListSince(
      @Path("unix_timestamp") long timestamp) {
    if (!checkInternetConnection())
      return null;
    if (!checkCredentialsAvailability())
      return null;
    return mRealSubject.getStoryListSince(timestamp);
  }

  @Override
  public Observable<Response<Story.Multiple>> getStoryListAfter(
      @Path("after_id") long afterId) {
    if (!checkInternetConnection())
      return null;
    if (!checkCredentialsAvailability())
      return null;
    return mRealSubject.getStoryListAfter(afterId);
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
    return BaseActivity.isDemoRunning(mContext);
  }
}
