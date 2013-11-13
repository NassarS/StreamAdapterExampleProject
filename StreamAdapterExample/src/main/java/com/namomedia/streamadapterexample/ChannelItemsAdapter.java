package com.namomedia.streamadapterexample;

import android.content.Context;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.namomedia.android.NamoAdData;
import com.namomedia.android.NamoPositionAdjuster;
import com.namomedia.android.NamoStreamAdapter;

import java.util.ArrayList;


public class ChannelItemsAdapter extends BaseAdapter {
  private final ImageLoader imageLoader;
  private final Context context;
  private final ChannelItemsCache cache;
  private final ArrayList<LayoutContainer> items = new ArrayList<LayoutContainer>();
  private final NamoStreamAdapter streamAdapter;

  public ChannelItemsAdapter(Context context, ImageLoader imageLoader, ChannelItemsCache cache,
      NamoStreamAdapter streamAdapter) {
    this.imageLoader = imageLoader;
    this.context = context;
    this.cache = cache;
    this.streamAdapter = streamAdapter;
  }

  static class LayoutContainer {
    int layoutId;
    ArrayList<BaseItem> mItems = new ArrayList<BaseItem>();
  }

  private int getPanesPerRow() {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    float widthDp = displayMetrics.widthPixels / displayMetrics.density;
    return (int)widthDp / 280 + 1;
  }

  public static class ViewHolder {
    View root;
    ImageView image;
    TextView text;
    BaseItem item;
  }


  @Override
  public int getCount() {
    return items.size();
  }

  @Override
  public LayoutContainer getItem(int position) {
    return items.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  static class AdItem extends BaseItem {
    private NamoAdData adData;

    public AdItem(NamoAdData adData) {
      super(adData.getText(), 0);
      this.adData = adData;
    }

    public NamoAdData getAdData() {
      return adData;
    }
  }

  private ArrayList<LayoutContainer> buildItemsArray(ArrayList<BaseItem> sourceItems) {
    ArrayList<LayoutContainer> tempItems = new ArrayList<LayoutContainer>();
    LayoutContainer curLayout = null;
    int panesPerRow = getPanesPerRow();

    NamoPositionAdjuster adjuster = streamAdapter.getPositionAdjuster();
    adjuster.setOriginalCount(sourceItems.size());

    for (int i = 0; i < adjuster.getAdjustedCount(); ++i) {
      if (curLayout == null) {
        curLayout = new LayoutContainer();
        curLayout.layoutId = R.layout.list_item;
        tempItems.add(curLayout);
      }

      BaseItem item;
      if (adjuster.isAd(i)) {
        item = new AdItem(adjuster.getAdData(i));
      } else {
        item = sourceItems.get(adjuster.getOriginalPosition(i));
      }

      curLayout.mItems.add(item);
      if (curLayout.mItems.size() == panesPerRow) {
        curLayout = null;
      }
    }
    return tempItems;
  }

  public void updateAvailableItems() {
    ArrayList<BaseItem> baseItems = new ArrayList<BaseItem>();
    for (int i = 0; i < cache.size(); ++i) {
      baseItems.add(cache.getItem(i));
    }
    items.clear();
    items.addAll(buildItemsArray(baseItems));
    notifyDataSetChanged();
  }

  @Override
  public boolean isEnabled(int position) {
    return false;
  }

  private final int[] paneIds = {
    R.id.pane1,
    R.id.pane2,
    R.id.pane3,
    R.id.pane4
  };

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutContainer layoutContainer = getItem(position);

    if (convertView == null || convertView.getId() != layoutContainer.layoutId) {
      convertView = LayoutInflater.from(context).inflate(layoutContainer.layoutId, null);
    }

    int count = layoutContainer.mItems.size();
    int panesPerRow = getPanesPerRow();
    for (int i = 0; i < 4; i++) {
      View pane = convertView.findViewById(paneIds[i]);
      if (pane == null) {
        continue;
      }
      if (i >= panesPerRow) {
        pane.setVisibility(View.GONE);
        continue;
      }
      if (i >= count) {
        pane.setVisibility(View.INVISIBLE);
        continue;
      }

      clearPane(pane);
      ViewHolder vh = (ViewHolder) pane.getTag();
      vh.item = layoutContainer.mItems.get(i);
      layoutPane(pane);
    }
    return convertView;
  }

  private void layoutPane(View pane) {
    ViewHolder vh = (ViewHolder) pane.getTag();
    BaseItem item = vh.item;

    if (item instanceof AdItem) {
      AdItem adItem = (AdItem)vh.item;
      streamAdapter.placeAdView(adItem.adData, vh.root, null);
    } else {
      vh.text.setText(Html.fromHtml(item.getTitle()));
      imageLoader.download(item.getImageId(), vh.image);
    }
  }

  protected void clearPane(View pane) {
    ViewHolder vh = (ViewHolder) pane.getTag();
    if (vh == null) {
      vh = new ViewHolder();
      vh.root = pane;
      vh.text = (TextView) pane.findViewById(R.id.text);
      vh.image = (ImageView) pane.findViewById(R.id.image);
      vh.image.setTag(vh);
      pane.setTag(vh);
    }
    vh.image.setImageBitmap(null);
  }
}
