package me.syniuhin.storyteller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import me.syniuhin.storyteller.R;
import me.syniuhin.storyteller.net.model.Story;
import okhttp3.OkHttpClient;

/**
 * Created with love, by infm dated on 4/16/16.
 */
public class SinglePictureAdapterOld extends ArrayAdapter<Story> {
  // TODO: Get read of this or prove it can\'t cause leaks.
  private Picasso.Builder picassoBuilder;

  public SinglePictureAdapterOld(Context context, OkHttpClient httpClient) {
    super(context, R.layout.single_picture_item);
    picassoBuilder = new Picasso.Builder(context).downloader(
        new OkHttp3Downloader(httpClient));
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View v = convertView;
    ItemHolder holder;

    if (v == null) {
      Context context = getContext();
      LayoutInflater inflater = LayoutInflater.from(context);
      v = inflater.inflate(R.layout.single_picture_item, parent, false);

      holder = new ItemHolder();
      holder.storyTextView =
          (TextView) v.findViewById(R.id.list_item_textview);
      holder.imageView =
          (ImageView) v.findViewById(R.id.list_item_imageview);
    } else {
      holder = (ItemHolder) v.getTag();
    }

    Story item = getItem(position);
    holder.storyTextView.setText(item.getText());
    picassoBuilder.build()
           .load(item.getPictureUrl())
           .resize(50, 50)
           .centerCrop()
           .into(holder.imageView);
    v.setTag(holder);
    return v;
  }

  private static class ItemHolder {
    TextView storyTextView;
    ImageView imageView;
  }
}
