package me.syniuhin.storyteller.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import me.syniuhin.storyteller.R;
import me.syniuhin.storyteller.net.service.creator.BasicAuthClientCreator;
import me.syniuhin.storyteller.provider.story.StoryCursor;

/**
 * Created with love, by infm dated on 4/17/16.
 */
public class SinglePictureAdapter
    extends CursorRecyclerAdapter<SinglePictureAdapter.StoryItemHolder> {

  private Picasso.Builder mPicassoBuilder;
  private StoryItemHolder.ClickCallback mCallback;

  public SinglePictureAdapter(Cursor cursor, Context context,
                              StoryItemHolder.ClickCallback callback) {
    super(cursor);
    mPicassoBuilder = BasicAuthClientCreator.createPicassoBuilder(context);
    mCallback = callback;
  }

  @Override
  public void onBindViewHolderCursor(
      StoryItemHolder holder, Cursor cursor) {
    StoryCursor sc = new StoryCursor(cursor);
    holder.storyTextView.setText(sc.getText());
    holder.timeTextView.setText(sc.getTimeCreated());
    holder.realId = sc.getId();
    mPicassoBuilder.build()
                   .load(sc.getPictureUrl())
                   .fit()
                   .centerCrop()
                   .into(holder.imageView);
  }

  @Override
  public StoryItemHolder onCreateViewHolder(
      ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext())
                           .inflate(R.layout.main_recview_card, parent, false);
    StoryItemHolder holder = new StoryItemHolder(v, 0, mCallback);
    holder.imageView.setOnClickListener(holder);
    return holder;
  }

  public static class StoryItemHolder extends RecyclerView.ViewHolder implements
      View.OnClickListener {
    TextView storyTextView;
    TextView timeTextView;
    ImageView imageView;
    long realId;

    ClickCallback listener;

    public StoryItemHolder(View itemView, long realId, ClickCallback listener) {
      super(itemView);
      storyTextView = (TextView) itemView.findViewById(
          R.id.cardview_story_textview);
      timeTextView = (TextView) itemView.findViewById(
          R.id.cardview_time_textview);
      imageView = (ImageView) itemView.findViewById(R.id.cardview_imageview);
      this.realId = realId;
      this.listener = listener;
    }

    @Override
    public void onClick(View v) {
      if (v instanceof ImageView)
        listener.onImage((ImageView) v, realId);
    }

    public interface ClickCallback {
      void onImage(ImageView caller, long realId);
    }
  }

}
