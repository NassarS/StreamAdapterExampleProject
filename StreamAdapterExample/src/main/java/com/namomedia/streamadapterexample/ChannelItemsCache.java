package com.namomedia.streamadapterexample;

import java.util.ArrayList;

public class ChannelItemsCache {
  private ArrayList<BaseItem> items;

  public ChannelItemsCache() {
    items = new ArrayList<BaseItem>();
    for (int i = 0; i < 20; ++i) {
      BaseItem item = new BaseItem("When life gives you lemons, try this cocktail: " + i,
          R.drawable.lemonade);
      items.add(item);
    }
  }

  public BaseItem getItem(int position) {
    return items.get(position);
  }

  public int size() {
    return items.size();
  }
}
