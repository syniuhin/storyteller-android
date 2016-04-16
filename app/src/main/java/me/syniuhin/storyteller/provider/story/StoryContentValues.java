package me.syniuhin.storyteller.provider.story;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import me.syniuhin.storyteller.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code story} table.
 */
public class StoryContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return StoryColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable StorySelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable StorySelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Integer story type. 0: for a story generated about single image; 1: multiple images (e.g. album); 2: location based multiple images (e.g. trip).
     */
    public StoryContentValues putStoryType(int value) {
        mContentValues.put(StoryColumns.STORY_TYPE, value);
        return this;
    }


    public StoryContentValues putText(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("text must not be null");
        mContentValues.put(StoryColumns.TEXT, value);
        return this;
    }


    public StoryContentValues putTimeCreated(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("timeCreated must not be null");
        mContentValues.put(StoryColumns.TIME_CREATED, value);
        return this;
    }


    public StoryContentValues putPictureUrl(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("pictureUrl must not be null");
        mContentValues.put(StoryColumns.PICTURE_URL, value);
        return this;
    }

}
