package app.test.laitkor.mediascene;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ImageDownload extends AppCompatActivity {

    private static Uri URI = null;
    Button submitBtn, downloadImageBtn;
    EditText mediaId, display_Data_EditText;
    ProgressDialog progress;
    String resultOutput, _button_id, jsonImageStatus, jsonMediaValue;
    ImageView imageDisplay;
    Bitmap myBitmap;
    private String ImagePath;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_download);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        submitBtn = (Button) findViewById(R.id.submit_button);
        downloadImageBtn = (Button) findViewById(R.id.save_image_button);
        mediaId = (EditText) findViewById(R.id.media_editText);
        display_Data_EditText = (EditText) findViewById(R.id.displayDataEditText);
        imageDisplay = (ImageView) findViewById(R.id.imageView);

        display_Data_EditText.setVisibility(View.INVISIBLE);
        imageDisplay.setVisibility(View.INVISIBLE);
        downloadImageBtn.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        _button_id = intent.getStringExtra("button_id");
        System.out.println("button id is : " + _button_id);

        submitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                display_Data_EditText.setVisibility(View.INVISIBLE);
                imageDisplay.setVisibility(View.INVISIBLE);
                downloadImageBtn.setVisibility(View.INVISIBLE);

                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if (_button_id.equals("imageDownload")) {
                    imageDownloadMethod(v);
                } else if (_button_id.equals("thumbnailDownload")) {
                    if (mediaId.getText().toString().equals("")) {
                        AlertDialog.Builder alertObject = new AlertDialog.Builder(ImageDownload.this);
                        alertObject.setMessage("Please enter media id");
                        alertObject.setCancelable(false);
                        alertObject.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog alertdialogobject = alertObject.create();
                        alertdialogobject.show();

                    } else {
                        imageDownloadMethod(v);
                    }
                }
            }
        });
    }

    public void downloadImage(Bitmap bitmap) {
        this.bitmap = bitmap;
        try {

            if (ContextCompat.checkSelfPermission(ImageDownload.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ImageDownload.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 11);
            } else
                downloadImageAfterPermissionCheck(bitmap);
        } catch (Exception e) {

            e.printStackTrace();
        }
        System.out.println("bitmap value in download button code segment is : " + bitmap);
        Toast.makeText(ImageDownload.this, "Image Saved Successfully In Gallery", Toast.LENGTH_LONG).show();
    }

    private void downloadImageAfterPermissionCheck(Bitmap bitmap) {
        ImagePath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "media scene image", "Media Scene image");
        URI = Uri.parse(ImagePath);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 11: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    downloadImageAfterPermissionCheck(ImageDownload.this.bitmap);
                }
                return;
            }
        }
    }

    // This async task gets data from API and manages it.

    public void imageDownloadMethod(View view) {
        new GetImageData(this).execute();
    }

    // This async task gets data of image from server and converts into bitmap and sets in imageview.
    private void GetImageFromServer() {
        new SetImageData(this).execute();
    }

    private class GetImageData extends AsyncTask<String, Void, Void> {

        private final Context context;
        StringBuilder responseOutput = null;

        public GetImageData(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (jsonImageStatus.equals("1")) {

                GetImageFromServer();
                display_Data_EditText.setVisibility(View.VISIBLE);
                display_Data_EditText.setKeyListener(null);
                display_Data_EditText.setText(responseOutput);

                downloadImageBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        downloadImage(myBitmap);
                    }
                });

            } else if (jsonImageStatus.equals("0")) {
                display_Data_EditText.setVisibility(View.VISIBLE);
                display_Data_EditText.setKeyListener(null);
                display_Data_EditText.setText(responseOutput);
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = null;
                try {
                    if (_button_id.equals("imageDownload")) {
                        url = new URL("http://52.204.177.76:8000/api/node/imageDownload?api_token=qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmlkjhgfds" + "&media_id=" + mediaId.getText().toString());
                    } else if (_button_id.equals("thumbnailDownload")) {
                        url = new URL("http://52.204.177.76:8000/api/node/thumbnailDownload?api_token=qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmlkjhgfds" + "&media_id=" + mediaId.getText().toString());
                    }
                    System.out.println("url value is : " + url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    connection.setRequestMethod("POST");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setDoOutput(true);
                int responseCode = connection.getResponseCode();

                final StringBuilder output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator") + "Response Code " + responseCode);
                output.append(System.getProperty("line.separator") + "Type " + "POST");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                responseOutput = new StringBuilder();
                System.out.println("output===============" + br);
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                br.close();

                output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + System.getProperty("line.separator") + responseOutput.toString());

                ImageDownload.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("JSON DATA IS : " + responseOutput);

                        try {
                            JSONObject json_object = new JSONObject(responseOutput.toString());

                            jsonMediaValue = json_object.getString("media");
                            jsonImageStatus = json_object.getString("imgstatus");
                            System.out.println("JSON DATA IS : " + resultOutput);
                            System.out.println("media and image status is  : \n" + jsonMediaValue + "\n" + jsonImageStatus);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progress.dismiss();
                    }
                });
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    } // end of async task method

    private class SetImageData extends AsyncTask<String, Void, Void> {
        private final Context context;

        public SetImageData(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading image");
            progress.show();
        }

        protected void onPostExecute(Void result) {
            System.out.println("Bitmap value in set image async task code segment is : " + myBitmap);

        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                String profile_image_url = jsonMediaValue;
                System.out.println("image URL is " + profile_image_url);
                java.net.URL url_image = new java.net.URL(profile_image_url);
                HttpURLConnection connection_image = (HttpURLConnection) url_image.openConnection();
                connection_image.setDoInput(true);
                connection_image.connect();
                InputStream input = connection_image.getInputStream();
                myBitmap = BitmapFactory.decodeStream(input);

                ImageDownload.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        imageDisplay.setImageBitmap(myBitmap);
                        imageDisplay.setVisibility(View.VISIBLE);
                        downloadImageBtn.setVisibility(View.VISIBLE);
                        progress.dismiss();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
