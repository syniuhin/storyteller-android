package me.syniuhin.storyteller;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import me.syniuhin.storyteller.net.model.Story;
import me.syniuhin.storyteller.net.service.creator.BasicAuthClientCreator;
import me.syniuhin.storyteller.provider.story.StoryColumns;
import me.syniuhin.storyteller.provider.story.StoryCursor;
import me.syniuhin.storyteller.provider.story.StorySelection;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class DetailActivity extends BaseActivity {

  private long mStoryId;
  private ImageView mImageView;
  private TextView mStoryTextView;

  private Picasso.Builder mPicassoBuilder;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    mStoryId = getIntent().getLongExtra("storyLocalId", 0);
    initService();
    findViews();
    setupViews();
  }

  @Override
  protected void findViews() {
    mImageView = (ImageView) findViewById(R.id.detail_imageview);
    mStoryTextView = (TextView) findViewById(R.id.detail_story_textview);
  }

  @Override
  protected void setupViews() {
    mPicassoBuilder = BasicAuthClientCreator.createPicassoBuilder(this);
    Observable<Story> o = Observable.create(
        new Observable.OnSubscribe<Story>() {
          @Override
          public void call(Subscriber<? super Story> subscriber) {
            Story story = loadSingleStory(mStoryId);
            subscriber.onNext(story);
            subscriber.onCompleted();
          }
        });
    compositeSubscription.add(
        o.observeOn(AndroidSchedulers.mainThread())
         .subscribeOn(Schedulers.newThread())
         .subscribe(new Action1<Story>() {
           @Override
           public void call(Story story) {
             if (story != null && story.getText() != null && story
                 .getPictureUrl() != null) {
               mPicassoBuilder.build()
                              .load(story.getPictureUrl())
                              .memoryPolicy(MemoryPolicy.NO_CACHE)
                              .resize(1080, 0)
                              .into(mImageView);
               mStoryTextView.setText(story.getText());
             } else {
               Snackbar.make(mImageView, "Data integrity error",
                             Snackbar.LENGTH_SHORT).show();
             }
           }
         }, new Action1<Throwable>() {
           @Override
           public void call(Throwable throwable) {
             Snackbar.make(mImageView, "Unexpected error happened",
                           Snackbar.LENGTH_SHORT).show();
           }
         })
    );
  }

  @Override
  protected void initService() {
    compositeSubscription = new CompositeSubscription();
  }

  /**
   * Attention: It's blocking, so avoid calling it on main thread.
   *
   * @return Story object from a db.
   */
  private Story loadSingleStory(final long storyId) {
    StorySelection where = new StorySelection();
    where.id(storyId);
    Cursor c = getContentResolver().query(
        StoryColumns.CONTENT_URI,
        new String[] {
            StoryColumns.PICTURE_URL,
            StoryColumns.TEXT
        },
        where.sel(),
        where.args(),
        null);
    Story story = null;
    if (c != null && c.moveToNext()) {
      StoryCursor sc = new StoryCursor(c);
      story = new Story();
      story.setText(sc.getText());
      story.setPictureUrl(sc.getPictureUrl());
    }
    return story;
  }
}
