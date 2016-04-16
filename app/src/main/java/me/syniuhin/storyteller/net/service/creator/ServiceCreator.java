package me.syniuhin.storyteller.net.service.creator;

import android.content.Context;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created with love, by infm dated on 4/16/16.
 */
abstract public class ServiceCreator {
  public static final String API_BASE_URL = "http://77.47.204.144:4000" +
      "/storyteller/";

  protected Retrofit.Builder retrofitBuilder =
      new Retrofit.Builder()
          .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
          .addConverterFactory(GsonConverterFactory.create())
          .baseUrl(API_BASE_URL);

  abstract public Retrofit createInitializer(Context context);
}
