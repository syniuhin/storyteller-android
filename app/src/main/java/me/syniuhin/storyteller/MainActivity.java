package me.syniuhin.storyteller;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import me.syniuhin.storyteller.adapter.SinglePictureAdapter;
import me.syniuhin.storyteller.net.model.Story;
import me.syniuhin.storyteller.net.service.api.StoryService;
import me.syniuhin.storyteller.net.service.api.StoryServiceProxy;
import me.syniuhin.storyteller.net.service.creator.BasicAuthServiceCreator;
import me.syniuhin.storyteller.provider.story.StoryColumns;
import me.syniuhin.storyteller.provider.story.StoryContentValues;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends BaseActivity implements
    LoaderManager.LoaderCallbacks<Cursor>,
    SinglePictureAdapter.StoryItemHolder.ClickCallback {

  private Toolbar mToolbar;
  private FloatingActionButton mFab;

  private SwipeRefreshLayout mSwipeRefresh;
  private RecyclerView mRecyclerView;
  private SinglePictureAdapter mAdapter;
  private RecyclerView.LayoutManager mLayoutManager;

  private StoryService mStoryService = null;


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
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    loadStories();
  }

  protected void findViews() {
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    mFab = (FloatingActionButton) findViewById(R.id.fab);
    mRecyclerView = (RecyclerView) findViewById(R.id.main_recyclerview);
    mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.main_swipenrefresh);
  }

  protected void setupViews() {
    setSupportActionBar(mToolbar);

    mFab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startUploadingActivity();
      }
    });

    mRecyclerView.setHasFixedSize(true);

    mLayoutManager = new LinearLayoutManager(this);
    mRecyclerView.setLayoutManager(mLayoutManager);

    mAdapter = new SinglePictureAdapter(
        null, this, this);
    mRecyclerView.setAdapter(mAdapter);

    mSwipeRefresh.setOnRefreshListener(
        new SwipeRefreshLayout.OnRefreshListener() {
          @Override
          public void onRefresh() {
            loadStories();
          }
        });

    getLoaderManager().initLoader(0, null, this);
  }

  protected void initService() {
    mStoryService = new StoryServiceProxy(
        new BasicAuthServiceCreator().createInitializer(this)
                                     .create(StoryService.class));
    compositeSubscription = new CompositeSubscription();
  }

  private void loadStories() {
    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(
        this);
    final long afterId = sp.getLong("storiesAfterId", 0);
    Observable<Response<Story.Multiple>> o =
        mStoryService.getStoryListAfter(afterId);
    compositeSubscription.add(
        o.observeOn(AndroidSchedulers.mainThread())
         .subscribeOn(Schedulers.newThread())
         .subscribe(new Action1<Response<Story.Multiple>>() {
           @Override
           public void call(Response<Story.Multiple> multiple) {
             long maxId = afterId;
             if (multiple.isSuccessful()) {
               for (Story s : multiple.body().getStories()) {
                 StoryContentValues cv = new StoryContentValues();
                 cv.putStoryType(s.getStoryType())
                   .putPictureUrl(s.getPictureUrl())
                   .putText(s.getText())
                   .putTimeCreated(s.getTimeCreated());
                 MainActivity.this.getContentResolver()
                                  .insert(cv.uri(), cv.values());
                 maxId = Math.max(maxId, s.getId());
               }
               sp.edit()
                 .putLong("storiesAfterId", maxId)
                 .apply();
             } else {
               Snackbar.make(mRecyclerView, "Unexpected error happened",
                             Snackbar.LENGTH_SHORT).show();
             }
             showProgress(false);
           }
         }, new Action1<Throwable>() {
           @Override
           public void call(Throwable throwable) {
             throwable.printStackTrace();
             Snackbar.make(mRecyclerView, "Unexpected error happened",
                           Snackbar.LENGTH_SHORT).show();
             showProgress(false);
           }
         })
    );
  }

  private void showProgress(boolean show) {
    mSwipeRefresh.setRefreshing(show);
  }

  private void startLoginActivity() {
    startActivity(new Intent(this, LoginActivity.class));
    finish();
  }

  private void startUploadingActivity() {
    startActivity(new Intent(this, UploadingActivity.class));
  }

  private void startDetailActivity(final ImageView caller, final long realId) {
    Intent i = new Intent(this, DetailActivity.class);
    i.putExtra("storyLocalId", realId);
    final String transitionName = getString(R.string.transition_story_picture);
    ActivityOptionsCompat options = ActivityOptionsCompat
        .makeSceneTransitionAnimation(this, caller, transitionName);
    ActivityCompat.startActivity(this, i, options.toBundle());
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

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(this, StoryColumns.CONTENT_URI, null, null, null,
                            null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mAdapter.swapCursor(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    mAdapter.swapCursor(null);
  }

  @Override
  public void onImage(ImageView caller, long realId) {
    startDetailActivity(caller, realId);
  }
}
