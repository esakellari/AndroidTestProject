package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
  ArrayList<Product> entryList = new ArrayList<Product>();
  ProductAdapter productAdapter;
  LinearLayoutManager linearLayoutManager;
  RecyclerView recyclerView;
  public static final String NEXT_SCREEN = "details_screen";
  SwipeRefreshLayout swipeRefreshLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    new MyTask().execute();

    swipeRefreshLayout = findViewById(R.id.swiperefresh);
    swipeRefreshLayout.setOnRefreshListener(
        () -> {
          new MyTask().execute();
          swipeRefreshLayout.setRefreshing(false);
        });
  }

  private class ImageDownloadTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
      URL url = null;
      HttpURLConnection urlConnection;
      InputStream in;
      Bitmap bitmap = null;

      try {
        //param[0] = image url
        url = new URL(params[0]);
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }

      try {
        urlConnection = (HttpURLConnection) Objects.requireNonNull(url).openConnection();

        if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
          in = urlConnection.getInputStream();
          bitmap = BitmapFactory.decodeStream(in);
          in.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

      File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

      if (!path.exists()) {
        path.mkdirs();
      }
      //param[1] = product Id , used as image name
      File imageFile = new File(path, params[1]);
      FileOutputStream out = null;
      try {
        out = new FileOutputStream(imageFile);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }

      try {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        out.flush();
        out.close();
        MediaScannerConnection.scanFile(MainActivity.this,
                                        new String[]{imageFile.getAbsolutePath()}, null,
                                        (path1, uri) -> {
                                        });
      } catch (Exception ignored) {
      }
      return imageFile.getAbsolutePath();
    }

    @Override
    protected void onPostExecute(String s) {
      super.onPostExecute(s);
    }
  }

  private class MyTask extends AsyncTask<String, String, String> {
    HttpsURLConnection urlConnection;
    BufferedReader bufferedReader = null;

    @Override
    protected String doInBackground(String... strings) {

      URL url = null;
      try {
        url = new URL("https://vivawallet.free.beeceptor.com/v1/api/products");
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
      try {
        urlConnection = (HttpsURLConnection) Objects.requireNonNull(url).openConnection();
        urlConnection.setRequestProperty("User-Agent", "my-rest-app-v0.1");
        if (urlConnection.getResponseCode() == 200) {
          InputStream inputStream = urlConnection.getInputStream();
          bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
          StringBuilder stringBuilder = new StringBuilder();
          String line;

          while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
          }
          return stringBuilder.length() != 0 ? stringBuilder.toString() : null;
        }
      } catch (IOException e) {
        e.printStackTrace();
        return null;
      } finally {
        if (urlConnection != null) {
          urlConnection.disconnect();
        }
        if (bufferedReader != null) {
          try {
            bufferedReader.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
      return null;
    }

    @Override
    protected void onPostExecute(String jsonStr) {
      //jsonStr = DummyJson.getDummyJson();
      if (jsonStr != null) {
        try {
          JSONArray jsonArray = new JSONArray(jsonStr);

          for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            //Download the thumbnail for this product
            String thumbnailUrl = jsonObject.getString("Thumbnail");
            thumbnailUrl = thumbnailUrl.replace("http", "https");

            String path = null;
            String id = String.valueOf(jsonObject.getInt("Id"));
            try {
              path = new ImageDownloadTask().execute(thumbnailUrl, id + "thumbnail.PNG").get();
            } catch (ExecutionException | InterruptedException e) {
              e.printStackTrace();
            }

            //Get Product description
            String des = null;
            if (jsonObject.has("Description")) {
              des = jsonObject.getString("Description");
            }
            Product product = new Product(jsonObject.getString("Name"),
                                          jsonObject.getString("Price"),
                                          jsonObject.getString("Image"),
                                          des, path);

            entryList.add(product);
          }

          productAdapter = new ProductAdapter(
              MainActivity.this, entryList,
              new ProductAdapter.OnClickListener() {
                @Override
                public void onClick(Product product) {
                  Toast.makeText(
                      MainActivity.this.getApplicationContext(),
                      "Item Clicked" + product.getProductName(),
                      Toast.LENGTH_LONG).show();
                  Intent intent = new Intent(MainActivity.this,
                                             ProductDetails.class);

                  intent.putExtra(NEXT_SCREEN, product);
                  startActivity(intent);
                }
              });

          linearLayoutManager = new LinearLayoutManager(
              MainActivity.this, LinearLayoutManager.VERTICAL, false);

          recyclerView = findViewById(R.id.recyclerView);
          recyclerView.setLayoutManager(linearLayoutManager);
          recyclerView.setAdapter(productAdapter);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
  }
}