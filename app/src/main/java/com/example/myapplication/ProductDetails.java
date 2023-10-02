package com.example.myapplication;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class ProductDetails extends AppCompatActivity {
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Product product = null;

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    if (getIntent().hasExtra(MainActivity.NEXT_SCREEN)) {
      product = (Product) getIntent().getSerializableExtra(
          MainActivity.NEXT_SCREEN);
    }
    if (product != null) {
      setContentView(R.layout.product_details);
      final TextView nameTextView = (TextView) findViewById(R.id.display_name);
      nameTextView.setText(product.getProductName());
      final TextView priceTextView = (TextView) findViewById(R.id.display_price);
      priceTextView.setText(product.getProductPrice());
      final TextView descriptionTextView = findViewById(R.id.display_description);
      descriptionTextView.setText(product.getProductDescription());
      final ImageView imageView = findViewById(R.id.product_image);

      String imagePath = null;
      try {
        imagePath = new ImageDownloadTask(this.getApplicationContext())
            .execute(product.getProductImageURL(),
                     product.getProductId() +
                     "image.PNG").get();
      } catch (ExecutionException | InterruptedException e) {
        e.printStackTrace();
      }
      File imageFile = new File(imagePath);
      if (imageFile.exists()) {
        imageView.setImageBitmap(
            BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
      }
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
    }
    return super.onOptionsItemSelected(item);
  }
}
