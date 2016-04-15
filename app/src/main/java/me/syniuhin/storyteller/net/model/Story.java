package me.syniuhin.storyteller.net.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created with love, by infm dated on 4/15/16.
 */
public class Story {
  @SerializedName("id")
  private long id;

  @SerializedName("story")
  private String text;

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
