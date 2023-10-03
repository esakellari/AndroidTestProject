package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class ImageDownloadTask extends AsyncTask<String, Void, String> {
  private final Context context;

  public ImageDownloadTask(Context context) {
    this.context = context;
  }

  @Override
  protected String doInBackground(String... params) {
    URL url = null;
    HttpURLConnection urlConnection;
    InputStream in;
    Bitmap bitmap = null;

    try {
      //param[0] = image url
      String urlStr = params[0].replace("http", "https");
      url = new URL(urlStr);
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
      Objects.requireNonNull(bitmap).compress(Bitmap.CompressFormat.PNG, 100, out);
      Objects.requireNonNull(out).flush();
      out.close();
      MediaScannerConnection.scanFile(this.context,
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