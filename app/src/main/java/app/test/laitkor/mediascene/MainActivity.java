package app.test.laitkor.mediascene;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends Activity {

    Button networkready, mediaByFolder, downloadImage, downloadThumbnail;
    StringBuilder responseOutput;
    ProgressDialog progress;
    String apiNetworkStatus;
    JSONObject json_object = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkready = (Button) findViewById(R.id.button1);
        mediaByFolder = (Button) findViewById(R.id.button2);
        downloadImage = (Button) findViewById(R.id.button3);
        downloadThumbnail = (Button) findViewById(R.id.button4);

        networkready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNetworkMethod(v);
            }
        });

        mediaByFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextScreen = new Intent(getApplicationContext(), MediaByFolderId.class);
                startActivity(nextScreen);
            }
        });

        downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent nextScreen = new Intent(getApplicationContext(), ImageDownload.class);
                nextScreen.putExtra("button_id", "imageDownload");
                startActivity(nextScreen);
            }
        });

        downloadThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent nextScreen = new Intent(getApplicationContext(), ImageDownload.class);
                nextScreen.putExtra("button_id", "thumbnailDownload");
                startActivity(nextScreen);
            }
        });
    }

    public void checkNetworkMethod(View View) {
        new PostClass(this).execute();
    }
    private class PostClass extends AsyncTask<String, Void, Void> {

        private final Context context;
        public PostClass(Context c) {
            this.context = c;
        }
        Exception mException = null;

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
            this.mException = null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            AlertDialog.Builder alertObject = new AlertDialog.Builder(context);
            alertObject.setMessage(apiNetworkStatus);
            alertObject.setCancelable(false);
            alertObject.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertdialogobject = alertObject.create();
            alertdialogobject.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = null;
                try {
                    url = new URL("http://52.204.177.76:8000/api/node/checknetwork?api_token=qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmlkjhgfds");
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
                    connection.setRequestMethod("GET");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setDoOutput(true);
                final int responseCode = connection.getResponseCode();
                final StringBuilder output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator") + "Response Code " + responseCode);
                output.append(System.getProperty("line.separator") + "Type " + "GET");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                responseOutput = new StringBuilder();
                System.out.println("output===============" + br);
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                br.close();

                output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + System.getProperty("line.separator") + responseOutput.toString());
                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            json_object = new JSONObject(responseOutput.toString());
                            apiNetworkStatus = json_object.getString("Msg");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                progress.dismiss();
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

