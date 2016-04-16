package me.syniuhin.storyteller.net.service;

import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * Created with love, by infm dated on 4/16/16.
 */
public class SimpleServiceCreator extends ServiceCreator {
  @Override
  public Retrofit createInitializer(Context context) {
    httpClientBuilder
        .addInterceptor(new HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY));
    OkHttpClient client = httpClientBuilder.build();
    return retrofitBuilder.client(client).build();
  }
}
