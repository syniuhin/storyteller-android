package me.syniuhin.storyteller.net.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by infm on 4/15/16.
 */
public class Message {
  @SerializedName("message")
  private String message;

  // Optional fields that will occur sometimes
  @SerializedName("user_id")
  private long userId;

  public String getMessage() {
    return message;
  }

  public long getUserId() {
    return userId;
  }
}
