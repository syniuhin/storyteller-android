package me.syniuhin.storyteller.net.service.creator;

import android.content.Context;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Created with love, by infm dated on 4/16/16.
 */
abstract public class ClientCreator {
  protected OkHttpClient.Builder httpClientBuilder =
      new OkHttpClient.Builder()
          .connectTimeout(300, TimeUnit.SECONDS)
          .readTimeout(300, TimeUnit.SECONDS);

  abstract public OkHttpClient createClient(Context context);
}
