package com.example.dhairyakumar.booklisting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ProcessingActivity extends AppCompatActivity
{
    public static final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?keyAIzaSyBcOD1Pp3Yahy4Qybjzw4MLlrL4JBxDxxg&q=";
    private static final String LOG_TAG = ProcessingActivity.class.getSimpleName();
    private BookAdapter bookAdapter;
    private String keyword;
    private String newURL;
    private TextView emptyStateTextView;
    private View loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            keyword = extras.getString("keyword");
            if (keyword != null) {
                try {
                    String encodedString = URLEncoder.encode(keyword, "UTF-8");
                    newURL = GOOGLE_BOOKS_URL + encodedString;
                } catch (UnsupportedEncodingException e) {
                    Log.e(LOG_TAG, "Problem in encoding " + e);
                }
            }
        }
        loadingIndicator = findViewById(R.id.loading_spinner);
        emptyStateTextView = findViewById(R.id.empty_view);
        ListView listView = findViewById(R.id.list);
        bookAdapter = new BookAdapter(this, new ArrayList<Book>());
        listView.setEmptyView(emptyStateTextView);
        listView.setAdapter(bookAdapter); // to bind the list view with the adapter
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                BookAsyncTask bookAsyncTask = new BookAsyncTask();
                bookAsyncTask.execute(newURL);
            } else {
                loadingIndicator.setVisibility(View.GONE);
                emptyStateTextView.setText(R.string.no_internet);
            }
        }
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }



    private class BookAsyncTask extends AsyncTask<String, Void, List<Book >>
    {
        @Override
        protected List doInBackground(String... urls)
        {
            if (urls.length < 1 || urls[0] == null)
            {
                return null;
            }
            Uri baseUri=Uri.parse(newURL);
            urls[0]= baseUri.toString();

            return fetchBookData(urls[0]);
        }

        @Override
        protected void onPostExecute(List<Book> books)
        {
            loadingIndicator.setVisibility(View.GONE);
            emptyStateTextView.setText(R.string.no_books);
            bookAdapter.clear();

            if (books != null && !books.isEmpty())
            {
                bookAdapter.addAll(books);
            }
        }

        public  List<Book> fetchBookData(String requestUrl)
        {
            URL url = createUrl(requestUrl);
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG,"Problem in making the HTTP request",e);
            }
            return extractFeatureFromJson(jsonResponse);
        }

        private URL createUrl(String stringUrl)
        {
            URL url;
            try
            {
                url = new URL(stringUrl);
            }
            catch (MalformedURLException exception)
            {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }


        private String makeHttpRequest(URL url) throws IOException
        {
            String jsonResponse = "";

            if (url == null)
            {
                return jsonResponse;
            }
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try
            {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200)
                {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                }
                else
                    {
                    Log.e(LOG_TAG, "HTTP RESPONSE =" + keyword);
                }
            }
            catch (IOException e)
            {
                Log.e(LOG_TAG, "Exception = " + e);
            }
            finally
            {
                if (urlConnection != null)
                {
                    urlConnection.disconnect();
                }
                if (inputStream != null)
                {
                    Log.e(LOG_TAG, "Exception");
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException
        {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private List<Book> extractFeatureFromJson(String bookJSON)
        {
            if (TextUtils.isEmpty(bookJSON)) {
                return null;
            }
            List<Book> bookList= new ArrayList<>();

            try {
                JSONObject baseJsonResponse = new JSONObject(bookJSON);
                if (baseJsonResponse.has("items"))
                {
                    JSONArray itemsArray = baseJsonResponse.optJSONArray("items");

                    if (itemsArray.length() > 0) {
                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject jsonObject = itemsArray.getJSONObject(i);
                            JSONObject jsonObject1 = jsonObject.getJSONObject("volumeInfo");
                            if (jsonObject1.has("authors")) {
                                JSONArray jsonAuthor = jsonObject1.optJSONArray("authors");
                                String author = jsonAuthor.getString(0);
                            String title = jsonObject1.optString("title");
                            String publisher = jsonObject1.optString("publisher");
                            Book books = new Book(author, title, publisher);
                            bookList.add(books);
                            }
                        }
                    }
                }

            } catch (JSONException e)
            {
                Log.e(LOG_TAG,"Problem in parsing JSON",e);
            }
            return bookList;
        }
    }
}
