package me.syniuhin.storyteller.net.service;

import android.content.Context;
import android.preference.PreferenceManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

import java.io.IOException;

/**
 * Created with love, by infm dated on 4/16/16.
 */
public class BasicAuthServiceCreator extends ServiceCreator {
  @Override
  public Retrofit createInitializer(Context context) {
    httpClientBuilder
        .addInterceptor(new HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY));

    // Get header
    final String basic = "Basic " +
        PreferenceManager.getDefaultSharedPreferences(context)
                         .getString("basicAuthHeader", "");

    httpClientBuilder.addInterceptor(new Interceptor() {
      @Override
      public Response intercept(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder requestBuilder =
            original.newBuilder()
                    .header("Authorization", basic)
                    .header("Accept", "application/json")
                    .method(original.method(), original.body());

        Request request = requestBuilder.build();
        return chain.proceed(request);
      }
    });

    OkHttpClient client = httpClientBuilder.build();
    return retrofitBuilder.client(client).build();
  }
}
