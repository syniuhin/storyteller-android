package me.syniuhin.storyteller.net.service.creator;

import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created with love, by infm dated on 4/16/16.
 */
public class SimpleClientCreator extends ClientCreator {
  @Override
  public OkHttpClient createClient(Context context) {
    httpClientBuilder
        .addInterceptor(new HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY));
    return httpClientBuilder.build();
  }
}
