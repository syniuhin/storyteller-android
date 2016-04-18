package me.syniuhin.storyteller.net.service.creator;

import android.content.Context;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import me.syniuhin.storyteller.BaseActivity;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;

/**
 * Created with love, by infm dated on 4/16/16.
 */
public class BasicAuthClientCreator extends ClientCreator {
  /**
   * @param context: Context to create Picasso.Builder and OkHttpClient.
   * @return Picasso builder for authenticated image loading.
   */
  public static Picasso.Builder createPicassoBuilder(Context context) {
    return new Picasso.Builder(context).downloader(
        new OkHttp3Downloader(
            new BasicAuthClientCreator().createClient(context)));
  }

  public OkHttpClient createClient(Context context) {
    httpClientBuilder
        .addInterceptor(new HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BASIC));

    // Get authentication header.
    final String basic = "Basic " +
        context.getSharedPreferences(BaseActivity.PREFS_KEY,
                                     Context.MODE_PRIVATE)
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

    return httpClientBuilder.build();
  }
}
