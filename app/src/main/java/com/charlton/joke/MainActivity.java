package com.charlton.joke;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    /**
     * Fields (References Available throughout the class)
     */
    Button mButton;
    TextView jokeTextView;

    /**
     * Create a message box with a title and a message
     * @param title
     * @param message
     */
    public void createMessage(String title, String message) {
        /**
         * Initialize the AlertDialog Object
         */
        AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
        /**
         * Set a title to the messagebox
         */
        messageBox.setTitle(title);
        /**
         * Set a message
         */
        messageBox.setMessage(message);
        /**
         * Set The Positive Button text to the message box and a action
         */
        messageBox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Clicked Yes", Toast.LENGTH_SHORT).show();
            }
        });
        messageBox.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Clicked No", Toast.LENGTH_SHORT).show();
            }
        });
        /**
         * Show the message box
         */
        messageBox.show();
    }

    /**
     * The function to initialize the layout and other important things related to the UI and Networking
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Assign the activity a layout
         */
        setContentView(R.layout.activity_main);
        /**
         * Attach the button from layout by it's id
         */
        mButton = (Button) findViewById(R.id.button);
        jokeTextView = (TextView) findViewById(R.id.jokeTextView);
        /**
         * When the user clicks the button, this onClick Function will launch
         */
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new NetworkCall().execute("http://api.icndb.com/jokes/random?firstName=Charlton&lastName=Smith&limitTo=[nerdy]");
            }
        });
    }

    /**
     * Make the network call in the background (Async Thread) so it doesnt freeze the app
     */
    public class NetworkCall extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 1) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String content = null;
                    while ((content = bufferedReader.readLine()) != null) {
                        stringBuilder.append(content);
                    }
                    bufferedReader.close();
                    httpURLConnection.disconnect();
                    return stringBuilder.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            showMessage(s);
        }
    }

    /**
     * Parse the JSON Object we recieved from the api
     * Show it via toast
     * Apply it to the jokeTextView
     * @param content
     */
    private void showMessage(String content) {
        try {
            JSONObject jsonObject = new JSONObject(content);
            String type = jsonObject.getString("type");
            if (type.equals("success")) {
                JSONObject valueObject = jsonObject.getJSONObject("value");
                String joke = valueObject.getString("joke");
                /**
                 * Toast Message
                 */
                Toast.makeText(this, joke, Toast.LENGTH_LONG).show();
                /**
                 * Assign the joke to the textview
                 */
                jokeTextView.setText(joke);

                /**
                 * Show a messagebox
                 */
                createMessage("Joke",joke);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
