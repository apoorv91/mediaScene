package app.test.laitkor.mediascene;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MediaByFolderId extends Activity implements AdapterView.OnItemSelectedListener {

    Button submitBtn;
    EditText folderId, display_Data_EditText;
    Spinner language_spinner;
    ProgressDialog progress;
    String selState;
    private String[] laguage_array = {"title_en", "title_hi", "title_fr", "title_it", "title_ur", "title_tm", "title_ch","title_de","title_jp","title_pt", "title_ru", "title_ko","title_es"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_by_folder_id);

        submitBtn = (Button) findViewById(R.id.submit_button);
        folderId = (EditText) findViewById(R.id.folder_editText);
        language_spinner = (Spinner) findViewById(R.id.language_spinner);
        display_Data_EditText = (EditText) findViewById(R.id.displayDataEditText);
        display_Data_EditText.setVisibility(View.INVISIBLE);
        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, laguage_array);
        adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language_spinner.setAdapter(adapter_state);
        language_spinner.setOnItemSelectedListener(this);

        submitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                receiveMediaByfolderRequest(v);
            }
        });

        language_spinner.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        language_spinner.setSelection(position);
        selState = (String) language_spinner.getSelectedItem();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void receiveMediaByfolderRequest(View View) {
        new GetData(this).execute();
    }

    private class GetData extends AsyncTask<String, Void, Void> {

        private final Context context;
        StringBuilder responseOutput = null;

        public GetData(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            display_Data_EditText.setVisibility(View.VISIBLE);
            display_Data_EditText.setKeyListener(null);
            display_Data_EditText.setText(responseOutput);
        }

        @Override
        protected Void doInBackground(String... params) {

            folderId = (EditText) findViewById(R.id.folder_editText);
            try {
                URL url = null;
                try {
                    url = new URL("http://52.204.177.76:8000/api/node/getMediaByFolderId?api_token=qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmlkjhgfds"+"&fid=" + folderId.getText().toString() + "&language=" + selState);
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
                MediaByFolderId.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        System.out.println("JSON DATA IS : " + responseOutput);

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
}