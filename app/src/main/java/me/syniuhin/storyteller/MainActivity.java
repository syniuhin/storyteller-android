package me.syniuhin.storyteller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import me.syniuhin.storyteller.net.adapter.SinglePictureAdapter;
import me.syniuhin.storyteller.net.model.Story;
import me.syniuhin.storyteller.net.service.api.StoryService;
import me.syniuhin.storyteller.net.service.creator.BasicAuthClientCreator;
import me.syniuhin.storyteller.net.service.creator.BasicAuthServiceCreator;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

  private Toolbar mToolbar;
  private FloatingActionButton mFab;

  private ListView mListView;
  private SinglePictureAdapter mAdapter;

  private StoryService mStoryService = null;
  private CompositeSubscription mCompositeSubscription;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (!isLoggedIn()) {
      startLoginActivity();
    } else {
      findViews();
      setupViews();
      initService();
      loadStories();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mCompositeSubscription != null &&
        !mCompositeSubscription.isUnsubscribed())
      mCompositeSubscription.unsubscribe();
  }

  private void findViews() {
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    mFab = (FloatingActionButton) findViewById(R.id.fab);
    mListView = (ListView) findViewById(R.id.main_listview);
  }

  private void setupViews() {
    setSupportActionBar(mToolbar);

    mFab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startUploadingActivity();
      }
    });

    mAdapter = new SinglePictureAdapter(
        this, new BasicAuthClientCreator().createClient(this));
    mListView.setAdapter(mAdapter);
  }

  private void initService() {
    mStoryService = new BasicAuthServiceCreator().createInitializer(this)
                                                 .create(StoryService.class);
    mCompositeSubscription = new CompositeSubscription();
  }

  private void loadStories() {
    Observable<Response<Story.Multiple>> o = mStoryService.getStoryList();
    mCompositeSubscription.add(
        o.observeOn(AndroidSchedulers.mainThread())
         .subscribeOn(Schedulers.newThread())
         .subscribe(new Action1<Response<Story.Multiple>>() {
           @Override
           public void call(Response<Story.Multiple> multiple) {
             if (multiple.isSuccessful()) {
               mAdapter.clear();
               mAdapter.addAll(multiple.body().getStories());
             } else {
               Snackbar.make(mListView, "Unexpected error happened",
                             Snackbar.LENGTH_SHORT).show();
             }
           }
         }, new Action1<Throwable>() {
           @Override
           public void call(Throwable throwable) {
             throwable.printStackTrace();
             Snackbar.make(mListView, "Unexpected error happened",
                           Snackbar.LENGTH_SHORT).show();
           }
         })
    );
  }

  private void startLoginActivity() {
    startActivity(new Intent(this, LoginActivity.class));
    finish();
  }

  private void startUploadingActivity() {
    startActivity(new Intent(this, UploadingActivity.class));
  }

  private boolean isLoggedIn() {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    return sp.getBoolean("isLoggedIn", false);
  }

  private void logout() {
    PreferenceManager.getDefaultSharedPreferences(this)
                     .edit()
                     .putBoolean("isLoggedIn", false)
                     .putLong("userId", -1)
                     .commit();
    startLoginActivity();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
        return true;
      case R.id.action_logout:
        logout();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
