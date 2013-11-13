package com.namomedia.streamadapterexample;

public class BaseItem {
  private final String title;
  private final int imageId;

  public BaseItem(String title, int imageId) {
    this.title = title;
    this.imageId = imageId;
  }

  public String getTitle() {
    return title;
  }

  public int getImageId() {
    return imageId;
  }
}
