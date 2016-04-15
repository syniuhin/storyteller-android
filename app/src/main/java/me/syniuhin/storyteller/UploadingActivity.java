package me.syniuhin.storyteller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

public class UploadingActivity extends AppCompatActivity {

  private static final int EXT_ST_PERMISSION_REQUEST = 1728;

  private ViewSwitcher mViewSwitcher;
  private ImageView mImageViewSingle;
  private EditText mStoryEditText;

  private FloatingActionButton mFab;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_uploading);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    findViews();
    setupViews();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  private void findViews() {
    mFab = (FloatingActionButton) findViewById(R.id.fab);
    mViewSwitcher = (ViewSwitcher) findViewById(R.id.uploading_view_switcher);
    mImageViewSingle = (ImageView) findViewById(
        R.id.uploading_imageview_single);
    mStoryEditText = (EditText) findViewById(R.id.uploading_edit_text);
  }

  private void setupViews() {
    mFab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        askForStoragePermission();
      }
    });
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
}
