package com.example.myapplication;

import java.io.Serializable;

public class Product implements Serializable {
  private final String productId;
  private final String productName;
  private final String productPrice;
  private final String productImageURL;
  private final String productThumbnailPath;
  private final String productDescription;

  public Product(String productId, String productName, String productPrice, String productImageURL,
      String productDescription, String prductThumbnailPath) {
    this.productId = productId;
    this.productName = productName;
    this.productPrice = productPrice;
    this.productImageURL = productImageURL;
    this.productDescription = productDescription;
    this.productThumbnailPath = prductThumbnailPath;
  }

  public String getProductId() {
    return productId;
  }

  public String getProductName() {
    return productName;
  }

  public String getProductPrice() {
    return productPrice;
  }

  public String getProductDescription() {
    return productDescription;
  }

  public String getProductThumbnailPath() {
    return productThumbnailPath;
  }

  public String getProductImageURL() {
    return productImageURL;
  }
}
