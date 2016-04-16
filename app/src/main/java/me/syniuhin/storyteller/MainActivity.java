package me.syniuhin.storyteller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import me.syniuhin.storyteller.net.adapter.SinglePictureAdapter;

public class MainActivity extends AppCompatActivity {

  private Toolbar mToolbar;
  private FloatingActionButton mFab;

  private ListView mListView;
  private SinglePictureAdapter mAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (!isLoggedIn()) {
      startLoginActivity();
    } else {
      findViews();
      setupViews();
    }
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

    mAdapter = new SinglePictureAdapter(this);
    mListView.setAdapter(mAdapter);
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
