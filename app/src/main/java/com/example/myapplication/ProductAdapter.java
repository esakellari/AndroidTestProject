package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
  private final Context context;
  private final ArrayList<Product> productArrayList;
  private OnClickListener onClickListener;

  public ProductAdapter(Context context, ArrayList<Product> courseModelArrayList,
      OnClickListener onClickListener) {
    this.context = context;
    this.productArrayList = courseModelArrayList;
    this.onClickListener = onClickListener;
  }

  @NonNull
  @Override
  public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
    Product product = productArrayList.get(position);
    holder.productNameTextView.setText(product.getProductName());
    holder.productPriceTextView.setText(product.getPrice());

    File imgFile = new File(product.getImage());
    if (imgFile.exists()) {

      Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

      // ImageView myImage = (ImageView) findViewById(R.id.imageviewTest);

      // myImage.setImageBitmap(myBitmap);
      holder.productImageView.setImageBitmap(myBitmap);
    }
    ;

    holder.bind(productArrayList.get(position), onClickListener);
  }

  @Override
  public int getItemCount() {
    return productArrayList.size();
  }

  public interface OnClickListener {
    void onClick(Product model);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final TextView productNameTextView;
    private final TextView productPriceTextView;
    private final ImageView productImageView;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      productNameTextView = itemView.findViewById(R.id.name);
      productPriceTextView = itemView.findViewById(R.id.price);
      productImageView = itemView.findViewById(R.id.image);
      itemView.setOnClickListener(this);
    }

    public void bind(final Product item, final OnClickListener listener) {
      itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public void onClick(View view) {
    }
  }
}
