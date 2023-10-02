package com.example.myapplication;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Product implements Serializable {
  private long id;
  private final String productName;
  private final String productPrice;
  private int productImage;
  private String thumbnail;
  private String imagePath;
  private final String productDescription;

  public Product(String productName, String productPrice, int productImage,
      String productDescription, String imagePAth) {
    this.productName = productName;
    this.productPrice = productPrice;
    this.productImage = productImage;
    this.productDescription = productDescription;
    this.imagePath = imagePAth;
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

  public String getImage() {
    return imagePath;
  }
}
