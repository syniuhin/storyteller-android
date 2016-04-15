package me.syniuhin.storyteller.net.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created with love, by infm dated on 4/15/16.
 */
public class BasicResponse {
  @SerializedName("message")
  private String message;

  // Optional fields that will occur only in some of responses.
  @SerializedName("user_id")
  private long userId;

  @SerializedName("image_id")
  private long imageId;

  public String getMessage() {
    return message;
  }

  public long getUserId() {
    return userId;
  }

  public long getImageId() {
    return imageId;
  }
}
