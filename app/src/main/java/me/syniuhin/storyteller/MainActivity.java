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

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (!isLoggedIn()) {
      startLoginActivity();
    } else {
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
      if (fab != null) {
        fab.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Snackbar.make(view, String.valueOf(isLoggedIn()),
                          Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
          }
        });
      }
    }
  }

  private void startLoginActivity() {
    startActivity(new Intent(this, LoginActivity.class));
    finish();
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
