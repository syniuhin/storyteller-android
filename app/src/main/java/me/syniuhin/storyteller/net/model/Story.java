package me.syniuhin.storyteller.net.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created with love, by infm dated on 4/15/16.
 */
public class Story {
  @SerializedName("id")
  private long id;

  @SerializedName("text")
  private String text;

  @SerializedName("story_type")
  private int storyType;

  @SerializedName("time_created")
  private String timeCreated;

  @SerializedName("user_id")
  private long userId;

  @SerializedName("picture_url")
  private String pictureUrl;

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public long getId() {
    return id;
  }

  public int getStoryType() {
    return storyType;
  }

  public String getTimeCreated() {
    return timeCreated;
  }

  public long getUserId() {
    return userId;
  }

  public String getPictureUrl() {
    return pictureUrl;
  }

  public static class Multiple {
    @SerializedName("stories")
    private List<Story> stories;

    public List<Story> getStories() {
      return stories;
    }
  }
}
