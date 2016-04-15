package me.syniuhin.storyteller.net.service;

import android.util.Base64;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by infm on 4/15/16.
 */
public class ServiceGenerator {

  public static final String API_BASE_URL = "http://77.47.204.144:4000" +
      "/storyteller/";

  private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

  private static Retrofit.Builder builder =
      new Retrofit.Builder()
          .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
          .addConverterFactory(GsonConverterFactory.create())
          .baseUrl(API_BASE_URL);

  public static <S> S createService(Class<S> serviceClass) {
    return createService(serviceClass, null, null);
  }

  public static <S> S createService(Class<S> serviceClass, String username,
                                    String password) {
    if (username != null && password != null) {
      String credentials = username + ":" + password;
      final String basic =
          "Basic " + Base64.encodeToString(credentials.getBytes(),
                                           Base64.NO_WRAP);

      httpClient.addInterceptor(new Interceptor() {
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
    }

    httpClient.connectTimeout(5, TimeUnit.SECONDS);
    httpClient.addInterceptor(new HttpLoggingInterceptor().setLevel(
        HttpLoggingInterceptor.Level.BODY));
    OkHttpClient client = httpClient.build();
    Retrofit retrofit = builder.client(client).build();
    return retrofit.create(serviceClass);
  }
}
