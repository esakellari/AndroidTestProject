package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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

    new JSONDataTask().execute();

    swipeRefreshLayout = findViewById(R.id.swiperefresh);
    swipeRefreshLayout.setOnRefreshListener(
        () -> {
          new JSONDataTask().execute();
          swipeRefreshLayout.setRefreshing(false);
        });
  }

  private class JSONDataTask extends AsyncTask<String, String, String> {
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
        urlConnection.setRequestProperty("User-Agent", "androidTestApp");

        if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
          InputStream inputStream = urlConnection.getInputStream();
          bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
          StringBuilder stringBuilder = new StringBuilder();
          String line;

          while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
          }

          //Save the JSON data locally
          String filename = "products.json";
          File file = new File(MainActivity.this.getFilesDir(), filename);
          BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
          bufferedWriter.write(stringBuilder.toString());
          bufferedWriter.close();

          return filename;
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
    protected void onPostExecute(String filename) {
      if (filename != null) {
        try {
          //Read the stored JSON data
          File file = new File(MainActivity.this.getFilesDir(), filename);
          FileReader fileReader = new FileReader(file);
          BufferedReader bufferedReader = new BufferedReader(fileReader);
          StringBuilder stringBuilder = new StringBuilder();
          String line = bufferedReader.readLine();
          while (line != null) {
            stringBuilder.append(line).append("\n");
            line = bufferedReader.readLine();
          }
          bufferedReader.close();

          JSONArray jsonArray = new JSONArray(stringBuilder.toString());

          for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            //Download the thumbnail for this product
            String thumbnailUrl = jsonObject.getString("Thumbnail");

            String path = null;
            String id = String.valueOf(jsonObject.getInt("Id"));
            try {
              path = new ImageDownloadTask(MainActivity.this)
                  .execute(thumbnailUrl, id + "thumbnail.PNG").get();
            } catch (ExecutionException | InterruptedException e) {
              e.printStackTrace();
            }

            //Get Product description
            String des = null;
            if (jsonObject.has("Description")) {
              des = jsonObject.getString("Description");
            }
            Product product =
                new Product(String.valueOf(jsonObject.getInt("Id")), jsonObject.getString("Name"),
                            jsonObject.getString("Price"),
                            jsonObject.getString("Image"),
                            des, path);

            entryList.add(product);
          }

          productAdapter = new ProductAdapter(
              entryList,
              product -> {
                Intent intent = new Intent(MainActivity.this, ProductDetails.class);
                intent.putExtra(NEXT_SCREEN, product);
                startActivity(intent);
              });

          linearLayoutManager = new LinearLayoutManager(
              MainActivity.this, LinearLayoutManager.VERTICAL, false);

          recyclerView = findViewById(R.id.recyclerView);
          recyclerView.setLayoutManager(linearLayoutManager);
          recyclerView.setAdapter(productAdapter);
        } catch (JSONException | IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}