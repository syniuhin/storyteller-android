package me.syniuhin.storyteller.provider.story;

import me.syniuhin.storyteller.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A generated story.
 */
public interface StoryModel extends BaseModel {

    /**
     * Integer story type. 0: for a story generated about single image; 1: multiple images (e.g. album); 2: location based multiple images (e.g. trip).
     */
    int getStoryType();

    /**
     * Get the {@code text} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getText();

    /**
     * Get the {@code time_created} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getTimeCreated();

    /**
     * Get the {@code picture_url} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getPictureUrl();
}
