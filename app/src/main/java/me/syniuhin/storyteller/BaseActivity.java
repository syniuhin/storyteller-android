package me.syniuhin.storyteller;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import rx.subscriptions.CompositeSubscription;

import java.util.HashMap;

/**
 * Created with love, by infm dated on 4/16/16.
 */
abstract public class BaseActivity extends AppCompatActivity {
  private static final HashMap<String, String> DEMO_CREDENTIALS =
      new HashMap<>();

  static {
    DEMO_CREDENTIALS.put("username", "foo@example.com");
    DEMO_CREDENTIALS.put("password", "bar");
  }

  public static final String DEMO_HEADER_BASIC = Base64.encodeToString(
      (DEMO_CREDENTIALS.get(
          "username") + ":" + DEMO_CREDENTIALS.get(
          "password")).getBytes(), Base64.NO_WRAP);

  protected CompositeSubscription compositeSubscription = null;

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (compositeSubscription != null &&
        !compositeSubscription.isUnsubscribed())
      compositeSubscription.unsubscribe();
    if (isDemoRunning())
      logout();
  }

  abstract protected void findViews();

  abstract protected void setupViews();

  abstract protected void initService();

  protected void handleUnexpectedError(View v) {
    Snackbar.make(v, "Unexpected error happened", Snackbar.LENGTH_SHORT).show();
  }

  protected boolean isDemoRunning() {
    return BaseActivity.isDemoRunning(this);
  }

  public static boolean isDemoRunning(Context context) {
    return PreferenceManager.getDefaultSharedPreferences(context)
                            .getString("basicAuthHeader", "")
                            .equals(DEMO_HEADER_BASIC);
  }

  protected void logout() {
    PreferenceManager.getDefaultSharedPreferences(this)
                     .edit()
                     .putBoolean("isLoggedIn", false)
                     .putLong("userId", -1)
                     .putString("basicAuthHeader", "")
                     .commit();
  }
}
