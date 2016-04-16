package me.syniuhin.storyteller.provider.story;

import android.net.Uri;
import android.provider.BaseColumns;

import me.syniuhin.storyteller.provider.StorytellerProvider;
import me.syniuhin.storyteller.provider.story.StoryColumns;

/**
 * A generated story.
 */
public class StoryColumns implements BaseColumns {
    public static final String TABLE_NAME = "story";
    public static final Uri CONTENT_URI = Uri.parse(StorytellerProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * Integer story type. 0: for a story generated about single image; 1: multiple images (e.g. album); 2: location based multiple images (e.g. trip).
     */
    public static final String STORY_TYPE = "story_type";

    public static final String TEXT = "text";

    public static final String TIME_CREATED = "time_created";

    public static final String PICTURE_URL = "picture_url";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            STORY_TYPE,
            TEXT,
            TIME_CREATED,
            PICTURE_URL
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(STORY_TYPE) || c.contains("." + STORY_TYPE)) return true;
            if (c.equals(TEXT) || c.contains("." + TEXT)) return true;
            if (c.equals(TIME_CREATED) || c.contains("." + TIME_CREATED)) return true;
            if (c.equals(PICTURE_URL) || c.contains("." + PICTURE_URL)) return true;
        }
        return false;
    }

}
