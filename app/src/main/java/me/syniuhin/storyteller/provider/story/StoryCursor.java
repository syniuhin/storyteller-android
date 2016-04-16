package me.syniuhin.storyteller.provider.story;

import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import me.syniuhin.storyteller.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code story} table.
 */
public class StoryCursor extends AbstractCursor implements StoryModel {
    public StoryCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(StoryColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Integer story type. 0: for a story generated about single image; 1: multiple images (e.g. album); 2: location based multiple images (e.g. trip).
     */
    public int getStoryType() {
        Integer res = getIntegerOrNull(StoryColumns.STORY_TYPE);
        if (res == null)
            throw new NullPointerException("The value of 'story_type' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code text} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getText() {
        String res = getStringOrNull(StoryColumns.TEXT);
        if (res == null)
            throw new NullPointerException("The value of 'text' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code time_created} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getTimeCreated() {
        String res = getStringOrNull(StoryColumns.TIME_CREATED);
        if (res == null)
            throw new NullPointerException("The value of 'time_created' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code picture_url} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getPictureUrl() {
        String res = getStringOrNull(StoryColumns.PICTURE_URL);
        if (res == null)
            throw new NullPointerException("The value of 'picture_url' in the database was null, which is not allowed according to the model definition");
        return res;
    }
}
