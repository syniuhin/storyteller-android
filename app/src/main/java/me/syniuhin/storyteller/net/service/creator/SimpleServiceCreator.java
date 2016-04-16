package me.syniuhin.storyteller.net.service.creator;

import android.content.Context;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created with love, by infm dated on 4/16/16.
 */
public class SimpleServiceCreator extends ServiceCreator {
  @Override
  public Retrofit createInitializer(Context context) {
    OkHttpClient client = new SimpleClientCreator().createClient(context);
    return retrofitBuilder.client(client).build();
  }
}
