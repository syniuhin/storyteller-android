package me.syniuhin.storyteller.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import me.syniuhin.storyteller.R;
import me.syniuhin.storyteller.provider.story.StoryColumns;
import okhttp3.OkHttpClient;

/**
 * Created with love, by infm dated on 4/17/16.
 */
public class SinglePictureAdapter
    extends CursorRecyclerAdapter<SinglePictureAdapter.StoryItemHolder> {

  private Picasso.Builder picassoBuilder;

  public SinglePictureAdapter(Cursor cursor, Context context,
                              OkHttpClient httpClient) {
    super(cursor);
    picassoBuilder = new Picasso.Builder(context).downloader(
        new OkHttp3Downloader(httpClient));
  }

  @Override
  public void onBindViewHolderCursor(
      StoryItemHolder holder, Cursor cursor) {
    int textColumn = cursor.getColumnIndex(StoryColumns.TEXT);
    int imageUrlColumn = cursor.getColumnIndex(StoryColumns.PICTURE_URL);

    holder.storyTextView.setText(cursor.getString(textColumn));
    picassoBuilder.build()
                  .load(cursor.getString(imageUrlColumn))
                  .fit()
                  .centerCrop()
                  .into(holder.imageView);
  }

  @Override
  public StoryItemHolder onCreateViewHolder(
      ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext())
                           .inflate(R.layout.main_recview_card, parent, false);
    return new StoryItemHolder(v);
  }

  public static class StoryItemHolder extends RecyclerView.ViewHolder {
    TextView storyTextView;
    ImageView imageView;

    public StoryItemHolder(View itemView) {
      super(itemView);
      storyTextView = (TextView) itemView.findViewById(R.id.cardview_textview);
      imageView = (ImageView) itemView.findViewById(R.id.cardview_imageview);
    }
  }

}
