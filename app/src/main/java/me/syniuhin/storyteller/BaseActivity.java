package me.syniuhin.storyteller;

import android.support.v7.app.AppCompatActivity;
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
}
