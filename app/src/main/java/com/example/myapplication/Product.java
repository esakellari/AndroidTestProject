package com.example.myapplication;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Product implements Serializable {
  private long id;
  private final String productName;
  private final String productPrice;
  private String productImage;
  private String thumbnail;
  private String thumbnailPath;
  private final String productDescription;

  public Product(String productName, String productPrice, String productImage,
      String productDescription, String imagePAth) {
    this.productName = productName;
    this.productPrice = productPrice;
    this.productImage = productImage;
    this.productDescription = productDescription;
    this.thumbnailPath = imagePAth;
  }

  public String getProductName() {
    return productName;
  }

  public String getPrice() {
    return productPrice;
  }

  public String getDescription() {
    return productDescription;
  }

  public String getThumbnailPath() {
    return thumbnailPath;
  }
}
