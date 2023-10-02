package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
      Toast.makeText(getApplicationContext(), "Item Clicked44444444444", Toast.LENGTH_LONG).show();
      setContentView(R.layout.product_details);
      final TextView nameTextView = (TextView) findViewById(R.id.display_name);
      nameTextView.setText(product.getProductName());
      final TextView priceTextView = (TextView) findViewById(R.id.display_price);
      priceTextView.setText(product.getPrice());
      final TextView descriptionTextView = findViewById(R.id.display_description);
      descriptionTextView.setText(product.getDescription());
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
