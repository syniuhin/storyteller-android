package me.syniuhin.storyteller.provider.story;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import me.syniuhin.storyteller.provider.base.AbstractSelection;

/**
 * Selection for the {@code story} table.
 */
public class StorySelection extends AbstractSelection<StorySelection> {
    @Override
    protected Uri baseUri() {
        return StoryColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code StoryCursor} object, which is positioned before the first entry, or null.
     */
    public StoryCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new StoryCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public StoryCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code StoryCursor} object, which is positioned before the first entry, or null.
     */
    public StoryCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new StoryCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public StoryCursor query(Context context) {
        return query(context, null);
    }


    public StorySelection id(long... value) {
        addEquals("story." + StoryColumns._ID, toObjectArray(value));
        return this;
    }

    public StorySelection idNot(long... value) {
        addNotEquals("story." + StoryColumns._ID, toObjectArray(value));
        return this;
    }

    public StorySelection orderById(boolean desc) {
        orderBy("story." + StoryColumns._ID, desc);
        return this;
    }

    public StorySelection orderById() {
        return orderById(false);
    }

    public StorySelection storyType(int... value) {
        addEquals(StoryColumns.STORY_TYPE, toObjectArray(value));
        return this;
    }

    public StorySelection storyTypeNot(int... value) {
        addNotEquals(StoryColumns.STORY_TYPE, toObjectArray(value));
        return this;
    }

    public StorySelection storyTypeGt(int value) {
        addGreaterThan(StoryColumns.STORY_TYPE, value);
        return this;
    }

    public StorySelection storyTypeGtEq(int value) {
        addGreaterThanOrEquals(StoryColumns.STORY_TYPE, value);
        return this;
    }

    public StorySelection storyTypeLt(int value) {
        addLessThan(StoryColumns.STORY_TYPE, value);
        return this;
    }

    public StorySelection storyTypeLtEq(int value) {
        addLessThanOrEquals(StoryColumns.STORY_TYPE, value);
        return this;
    }

    public StorySelection orderByStoryType(boolean desc) {
        orderBy(StoryColumns.STORY_TYPE, desc);
        return this;
    }

    public StorySelection orderByStoryType() {
        orderBy(StoryColumns.STORY_TYPE, false);
        return this;
    }

    public StorySelection text(String... value) {
        addEquals(StoryColumns.TEXT, value);
        return this;
    }

    public StorySelection textNot(String... value) {
        addNotEquals(StoryColumns.TEXT, value);
        return this;
    }

    public StorySelection textLike(String... value) {
        addLike(StoryColumns.TEXT, value);
        return this;
    }

    public StorySelection textContains(String... value) {
        addContains(StoryColumns.TEXT, value);
        return this;
    }

    public StorySelection textStartsWith(String... value) {
        addStartsWith(StoryColumns.TEXT, value);
        return this;
    }

    public StorySelection textEndsWith(String... value) {
        addEndsWith(StoryColumns.TEXT, value);
        return this;
    }

    public StorySelection orderByText(boolean desc) {
        orderBy(StoryColumns.TEXT, desc);
        return this;
    }

    public StorySelection orderByText() {
        orderBy(StoryColumns.TEXT, false);
        return this;
    }

    public StorySelection timeCreated(String... value) {
        addEquals(StoryColumns.TIME_CREATED, value);
        return this;
    }

    public StorySelection timeCreatedNot(String... value) {
        addNotEquals(StoryColumns.TIME_CREATED, value);
        return this;
    }

    public StorySelection timeCreatedLike(String... value) {
        addLike(StoryColumns.TIME_CREATED, value);
        return this;
    }

    public StorySelection timeCreatedContains(String... value) {
        addContains(StoryColumns.TIME_CREATED, value);
        return this;
    }

    public StorySelection timeCreatedStartsWith(String... value) {
        addStartsWith(StoryColumns.TIME_CREATED, value);
        return this;
    }

    public StorySelection timeCreatedEndsWith(String... value) {
        addEndsWith(StoryColumns.TIME_CREATED, value);
        return this;
    }

    public StorySelection orderByTimeCreated(boolean desc) {
        orderBy(StoryColumns.TIME_CREATED, desc);
        return this;
    }

    public StorySelection orderByTimeCreated() {
        orderBy(StoryColumns.TIME_CREATED, false);
        return this;
    }

    public StorySelection pictureUrl(String... value) {
        addEquals(StoryColumns.PICTURE_URL, value);
        return this;
    }

    public StorySelection pictureUrlNot(String... value) {
        addNotEquals(StoryColumns.PICTURE_URL, value);
        return this;
    }

    public StorySelection pictureUrlLike(String... value) {
        addLike(StoryColumns.PICTURE_URL, value);
        return this;
    }

    public StorySelection pictureUrlContains(String... value) {
        addContains(StoryColumns.PICTURE_URL, value);
        return this;
    }

    public StorySelection pictureUrlStartsWith(String... value) {
        addStartsWith(StoryColumns.PICTURE_URL, value);
        return this;
    }

    public StorySelection pictureUrlEndsWith(String... value) {
        addEndsWith(StoryColumns.PICTURE_URL, value);
        return this;
    }

    public StorySelection orderByPictureUrl(boolean desc) {
        orderBy(StoryColumns.PICTURE_URL, desc);
        return this;
    }

    public StorySelection orderByPictureUrl() {
        orderBy(StoryColumns.PICTURE_URL, false);
        return this;
    }
}
