package com.namomedia.streamadapterexample;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.namomedia.android.Namo;
import com.namomedia.android.NamoAdData;
import com.namomedia.android.NamoAdViewBinder;
import com.namomedia.android.NamoStreamAdapter;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setTitle("Stream Adapter Example");
    setContentView(R.layout.activity_main);

    ListView listView = (ListView) findViewById(android.R.id.list);
    Namo.initialize(this, "a1-namo-android");

    NamoStreamAdapter namoAdapter = Namo.createStreamAdapter(this);
    final com.namomedia.streamadapterexample.ChannelItemsAdapter adapter =
        new com.namomedia.streamadapterexample.ChannelItemsAdapter(this, new ImageLoader(), new com.namomedia.streamadapterexample.ChannelItemsCache(), namoAdapter);
    listView.setAdapter(adapter);
    adapter.updateAvailableItems();

    namoAdapter.getPositionAdjuster().setOriginalCount(adapter.getCount());
    namoAdapter.registerDataSetObserver(new DataSetObserver() {
      @Override
      public void onChanged() {
        adapter.updateAvailableItems();
        super.onChanged();
      }
    });

    namoAdapter.registerAdViewBinder(new NamoAdViewBinder() {
      @Override
      public String getFormatIdentifier() {
        return "example_card";
      }

      @Override
      public View createView(ViewGroup parent) {
        // We always reuse views
        return null;
      }

      @Override
      public void bindAdData(View view, NamoAdData adData) {
        com.namomedia.streamadapterexample.ChannelItemsAdapter.ViewHolder vh = (com.namomedia.streamadapterexample.ChannelItemsAdapter.ViewHolder) view.getTag();
        adData.loadImage().into(vh.image);
        vh.text.setText(adData.getText());
      }
    });
    namoAdapter.setAdPlacement(2, 4, 10);
    namoAdapter.requestAds();
  }
}
