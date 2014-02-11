package com.namomedia.streamadapterexample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.namomedia.android.Namo;
import com.namomedia.android.NamoAdData;
import com.namomedia.android.NamoAdListener;
import com.namomedia.android.NamoAdPlacer;
import com.namomedia.android.NamoAdViewBinder;
import com.namomedia.android.NamoPositionAdjuster;


public class MainActivity extends Activity {

  NamoAdPlacer adPlacer;
  ListView listView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setTitle("Stream Adapter Example");
    setContentView(R.layout.activity_main);

    Namo.initialize(this, "app-33eb91c1ea5cfea6699");
    listView = (ListView) findViewById(R.id.list);
    adPlacer = Namo.createAdPlacer(this);
    adPlacer.registerAdViewBinder(new NamoAdViewBinder() {
      @Override
      public String getFormatIdentifier() {
        return "example_card";
      }

      @Override
      public View createView(ViewGroup parent) {
        // Not needed, because the list adapter always passes a view when calling placeAd.
        return null;
      }

      @Override
      public void bindAdData(View view, NamoAdData adData) {
        AdViewHolder viewHolder = (AdViewHolder) view.getTag();
        if (viewHolder == null) {
          viewHolder = new AdViewHolder();
          viewHolder.advertiserName = (TextView) view.findViewById(R.id.namo_advertiser_name);
          viewHolder.adText = (TextView) view.findViewById(R.id.namo_ad_text);
          viewHolder.adImage = (ImageView) view.findViewById(R.id.namo_ad_image);
          viewHolder.advertiserIcon = (ImageView) view.findViewById(R.id.namo_advertiser_icon);
        }
        adData.loadAdvertiserName().into(viewHolder.advertiserName);
        adData.loadText().into(viewHolder.adText);
        adData.loadImage().into(viewHolder.adImage);
        adData.loadAdvertiserIcon().into(viewHolder.advertiserIcon);
      }
    });

    adPlacer.setAdListener(new NamoAdListener() {
      @Override
      public void onAdsLoaded(NamoPositionAdjuster adjuster) {
        NamoAdapter adapter = new NamoAdapter(MainActivity.this, adPlacer);
        listView.setAdapter(adapter);
      }
    });
    adPlacer.requestAds();
  }

  static class AdViewHolder {
    TextView advertiserName;
    ImageView advertiserIcon;
    TextView adText;
    ImageView adImage;
  }

  static class NamoAdapter extends BaseAdapter {

    private NamoAdPlacer placer;

    private LayoutInflater inflater;
    private NamoPositionAdjuster adjuster;

    public NamoAdapter(Context context, NamoAdPlacer placer) {
      this.placer = placer;
      this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      this.adjuster = placer.getPositionAdjuster();
    }

    @Override
    public int getCount() {
      if (this.placer == null || this.placer.getPositionAdjuster() == null) {
        return 0;
      }
      return 2;
    }

    @Override
    public Object getItem(int position) {
      return null;
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
      if (view == null) {
        view = inflater.inflate(R.layout.ad_row, null);
      }

      // Could also put left ad and right ad in another view holder if you want.
      placeAdAtPosition(position * 2, view.findViewById(R.id.leftAd));
      placeAdAtPosition(position * 2 + 1, view.findViewById(R.id.rightAd));
      return view;
    }

    private void placeAdAtPosition(int position, View view) {
      NamoAdData ad = this.adjuster.getAdData(position);

      if (ad == null) {
        placer.clearAd(view);
      } else {
        placer.placeAd(ad, view, /* parent */ null);
      }
    }
  }
}
