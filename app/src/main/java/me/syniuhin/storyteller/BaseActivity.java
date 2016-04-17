package me.syniuhin.storyteller;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import rx.subscriptions.CompositeSubscription;

/**
 * Created with love, by infm dated on 4/16/16.
 */
abstract public class BaseActivity extends AppCompatActivity {
  protected CompositeSubscription compositeSubscription = null;

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (compositeSubscription != null &&
        !compositeSubscription.isUnsubscribed())
      compositeSubscription.unsubscribe();
  }

  abstract protected void findViews();

  abstract protected void setupViews();

  abstract protected void initService();

  protected void handleUnexpectedError(View v) {
    Snackbar.make(v, "Unexpected error happened", Snackbar.LENGTH_SHORT).show();
  }
}
