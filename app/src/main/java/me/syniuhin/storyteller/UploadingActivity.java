package me.syniuhin.storyteller;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;
import com.squareup.picasso.Picasso;
import me.syniuhin.storyteller.net.model.BasicResponse;
import me.syniuhin.storyteller.net.model.Story;
import me.syniuhin.storyteller.net.service.creator.BasicAuthServiceCreator;
import me.syniuhin.storyteller.net.service.api.StoryService;
import me.syniuhin.storyteller.net.util.FileUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import java.io.File;

public class UploadingActivity extends AppCompatActivity {

  private static final int EXT_ST_PERMISSION_REQUEST = 1728;
  private static final int RESULT_IMAGE_PICK = 9874;

  private ViewSwitcher mViewSwitcher;
  private ImageView mImageViewSingle;
  private EditText mStoryEditText;
  private ProgressBar mProgressView;

  private FloatingActionButton mFab;

  private Uri mImageUri;
  private long mImageId;
  private Story mStory;

  private StoryService mStoryService = null;
  private CompositeSubscription mCompositeSubscription;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_uploading);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    findViews();
    setupViews();
    initService();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mCompositeSubscription != null &&
        !mCompositeSubscription.isUnsubscribed())
      mCompositeSubscription.unsubscribe();
  }

  private void findViews() {
    mFab = (FloatingActionButton) findViewById(R.id.fab);
    mViewSwitcher = (ViewSwitcher) findViewById(R.id.uploading_view_switcher);
    mImageViewSingle = (ImageView) findViewById(
        R.id.uploading_imageview_single);
    mStoryEditText = (EditText) findViewById(R.id.uploading_edit_text);
    mProgressView = (ProgressBar) findViewById(R.id.uploading_progressbar);
  }

  private void setupViews() {
    setFabToPick();
  }

  private void initService() {
    mStoryService = new BasicAuthServiceCreator().createInitializer(this)
                                                 .create(StoryService.class);
    mCompositeSubscription = new CompositeSubscription();
  }

  private void askForStoragePermission() {
    if (!isStoragePermissionGranted())
      ActivityCompat.requestPermissions(
          UploadingActivity.this,
          new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
          EXT_ST_PERMISSION_REQUEST);
  }

  private void checkStoragePermission() {
    if (isStoragePermissionGranted()) {
      pickImage();
    } else {
      Snackbar.make(mViewSwitcher, R.string.ext_storage_permission_rationale,
                    Snackbar.LENGTH_LONG)
              .setAction("Grant", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  askForStoragePermission();
                }
              })
              .show();
    }
  }

  private boolean isStoragePermissionGranted() {
    return ContextCompat
        .checkSelfPermission(this,
                             Manifest.permission.READ_EXTERNAL_STORAGE) ==
        PackageManager.PERMISSION_GRANTED;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[],
                                         int[] grantResults) {
    switch (requestCode) {
      case EXT_ST_PERMISSION_REQUEST: {
        // If request is cancelled, the result arrays are empty.
/*
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

          // permission was granted, yay! Do the
          // contacts-related task you need to do.

        } else {

          // permission denied, boo! Disable the
          // functionality that depends on this permission.
        }
*/
        checkStoragePermission();
        break;
      }

      // other 'case' lines to check for other
      // permissions this app might request
    }
  }

  private void pickImage() {
    Intent i = new Intent(Intent.ACTION_PICK,
                          MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
    startActivityForResult(i, RESULT_IMAGE_PICK);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RESULT_IMAGE_PICK &&
        resultCode == RESULT_OK &&
        data != null) {
      ClipData clipdata = data.getClipData();
      if (clipdata == null) {
        // Error
        Snackbar.make(mViewSwitcher, R.string.error_picking_picture,
                      Snackbar.LENGTH_SHORT)
                //.setAction("Action", null)
                .show();
      } else {
        // Fetch 1st photo only
        final Uri uri = clipdata.getItemAt(0).getUri(); // A lot of them...
        mImageUri = uri;

        mViewSwitcher.showNext();

        setFabToUpload();
        displayImage(uri);
      }
    }
  }

  private void displayImage(final Uri uri) {
    Picasso.with(this)
           .load(uri)
           .resize(400, 400)
           .centerCrop()
           .into(mImageViewSingle);
  }

  private void setFabToUpload() {
    mFab.setImageDrawable(
        getResources().getDrawable(R.drawable.ic_file_upload_white_24dp,
                                   this.getTheme()));
    mFab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final File file = new File(
            FileUtils.getPath(UploadingActivity.this, mImageUri));
        uploadPicture(file);
      }
    });
  }

  private void setFabToPick() {
    mFab.setImageDrawable(
        getResources().getDrawable(R.drawable.ic_add_white_24dp,
                                   this.getTheme()));
    mFab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (isStoragePermissionGranted())
          pickImage();
        else
          askForStoragePermission();
      }
    });
  }

  private void setFabToAccept() {
    mFab.setImageDrawable(
        getResources().getDrawable(R.drawable.ic_done_white_24dp,
                                   this.getTheme()));
    mFab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        saveStory();
      }
    });
  }

  private void showProgress(final boolean show) {
    int mediumAnimTime = getResources().getInteger(
        android.R.integer.config_mediumAnimTime);

    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    mProgressView.animate().setDuration(mediumAnimTime).alpha(
        show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
      }
    });
  }

  private void uploadPicture(final File file) {
    showProgress(true);
    RequestBody requestFile =
        RequestBody.create(MediaType.parse("multipart/form-data"), file);
    MultipartBody.Part body =
        MultipartBody.Part.createFormData("image", file.getName(), requestFile);

    Observable<Response<BasicResponse>> o = mStoryService.uploadImage(body);
    mCompositeSubscription.add(
        o.observeOn(AndroidSchedulers.mainThread())
         .subscribeOn(Schedulers.newThread())
         .subscribe(new Action1<Response<BasicResponse>>() {
           @Override
           public void call(Response<BasicResponse> response) {
             if (response.isSuccessful()) {
               mImageId = response.body().getImageId();
               requestStory();
             } else {
               showProgress(false);
               Snackbar.make(mViewSwitcher, "Unexpected error occurred",
                             Snackbar.LENGTH_SHORT).show();
             }
           }
         }, new Action1<Throwable>() {
           @Override
           public void call(Throwable throwable) {
             showProgress(false);
             throwable.printStackTrace();
             Snackbar.make(mViewSwitcher, "Unexpected error occurred",
                           Snackbar.LENGTH_SHORT).show();
           }
         })
    );
  }

  private void requestStory() {
    Observable<Response<Story>> o = mStoryService.getStory(mImageId);
    mCompositeSubscription.add(
        o.observeOn(AndroidSchedulers.mainThread())
         .subscribeOn(Schedulers.newThread())
         .subscribe(new Action1<Response<Story>>() {
           @Override
           public void call(Response<Story> response) {
             showProgress(false);
             if (response.isSuccessful()) {
               mStory = response.body();
               mStoryEditText.setVisibility(View.VISIBLE);
               mStoryEditText.setText(mStory.getText());
               mStoryEditText.requestFocus();
               setFabToAccept();
             } else {
               Snackbar.make(mViewSwitcher, "Unexpected error occurred",
                             Snackbar.LENGTH_SHORT).show();
             }
           }
         }, new Action1<Throwable>() {
           @Override
           public void call(Throwable throwable) {
             showProgress(false);
             throwable.printStackTrace();
             Snackbar.make(mViewSwitcher, "Unexpected error occurred",
                           Snackbar.LENGTH_SHORT).show();
           }
         })
    );
  }

  private void saveStory() {
    showProgress(true);
    mStory.setText(mStoryEditText.getText().toString());
    Observable<Response<Story>> o =
        mStoryService.createStory(mImageId, mStory);
    mCompositeSubscription.add(
        o.observeOn(AndroidSchedulers.mainThread())
         .subscribeOn(Schedulers.newThread())
         .subscribe(new Action1<Response<Story>>() {
           @Override
           public void call(Response<Story> response) {
             showProgress(false);
             if (response.isSuccessful()) {
               finish();
             } else {
               Snackbar.make(mViewSwitcher, "Unexpected error occurred",
                             Snackbar.LENGTH_SHORT).show();
             }
           }
         }, new Action1<Throwable>() {
           @Override
           public void call(Throwable throwable) {
             Snackbar.make(mViewSwitcher, "Unexpected error occurred",
                           Snackbar.LENGTH_SHORT).show();
           }
         })
    );
  }
}
