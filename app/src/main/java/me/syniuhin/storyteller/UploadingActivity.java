package me.syniuhin.storyteller;

import android.Manifest;
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
import android.widget.ViewSwitcher;
import com.squareup.picasso.Picasso;

public class UploadingActivity extends AppCompatActivity {

  private static final int EXT_ST_PERMISSION_REQUEST = 1728;
  private static final int RESULT_IMAGE_PICK = 9874;

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
        if (isStoragePermissionGranted())
          pickImage();
        else
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
        final Uri uri = clipdata.getItemAt(0).getUri();

        mViewSwitcher.showNext();

        displayImage(uri);
      }
    }
  }

  private void displayImage(final Uri uri) {
    Picasso.with(this)
           .load(uri)
           .fit()
           .into(mImageViewSingle);
    mStoryEditText.requestFocus();
  }
}
