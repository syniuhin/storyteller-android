package me.syniuhin.storyteller.net.service;

import android.content.Context;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created with love, by infm dated on 4/16/16.
 */
abstract public class ServiceCreator {
  public static final String API_BASE_URL = "http://77.47.204.144:4000" +
      "/storyteller/";

  protected OkHttpClient.Builder httpClientBuilder =
      new OkHttpClient.Builder()
          .connectTimeout(5, TimeUnit.SECONDS);

  protected Retrofit.Builder retrofitBuilder =
      new Retrofit.Builder()
          .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
          .addConverterFactory(GsonConverterFactory.create())
          .baseUrl(API_BASE_URL);

  abstract public Retrofit createInitializer(Context context);
}
