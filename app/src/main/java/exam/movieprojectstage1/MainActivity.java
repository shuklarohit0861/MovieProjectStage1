package exam.movieprojectstage1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AdapterMovies adapterMovies;
    GridView gridView;
    private ArrayList<MoviesDetails> movieData = null;
    String moviePref;
    SharedPreferences preferences;


    static  final String MOVIE_DATA = "MOVIE-DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("OnCreate","ON");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gridView = (GridView)findViewById(R.id.movieTitle_main);
         preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
         moviePref = preferences.getString(getString(R.string.pref_key_value),getString(R.string.pref_default_value));

        if(savedInstanceState != null ) {
            movieData = savedInstanceState.getParcelableArrayList(MOVIE_DATA);
            if(movieData != null) {
                adapterMovies = new AdapterMovies(getApplicationContext(), movieData);
                gridView.setAdapter(adapterMovies);
            }
        }

       gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               MoviesDetails selectedMovie =  adapterMovies.getItem(i);
               Intent intent = new Intent(MainActivity.this,DetailedActivity.class);
               intent.putExtra("Title",selectedMovie.getMovieTitle());
               intent.putExtra("OverView",selectedMovie.getOverView());
               intent.putExtra("Release_Date",selectedMovie.getRelease_date());
               intent.putExtra("Poster_path",selectedMovie.getPosterPath());
               intent.putExtra("Vote",selectedMovie.getVoteAverage());
               startActivity(intent);
           }
       });


    }

    public boolean checkInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.v("OnStart","ON");
        if(checkInternet(getApplicationContext())== true)
        {

            String moviePrefChange =  preferences.getString(getString(R.string.pref_key_value),getString(R.string.pref_default_value));

            if(!moviePrefChange.equals(moviePref)||movieData == null) {
                updateMoviedata();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Some Thing wrong with your Internet Check it out !!!!!!",Toast.LENGTH_LONG).show();
        }
    }

    protected void updateMoviedata()
    {
        FatchMovieData fatch = new FatchMovieData();
        moviePref = preferences.getString(getString(R.string.pref_key_value),getString(R.string.pref_default_value));
        fatch.execute(moviePref);
        Log.v("NETWORK CALL","Network call is made");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(movieData != null) {
            Log.v("data is saved","On save");
            outState.putParcelableArrayList(MOVIE_DATA, movieData);
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


            startActivity(new Intent(this,Settings.class));
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    private class FatchMovieData extends AsyncTask<String, Void, ArrayList<MoviesDetails>> {

        private ArrayList<MoviesDetails> getMovieDataFromJson(String movieJsonString) throws JSONException {
            final String TMD_RESULTS = "results";
            final String TMD_POSTER_PATH = "poster_path";
            final String TMD_OVERVIEW = "overview";
            final String TMD_RELEASE_DATE = "release_date";
            final String TMD_ORIGINAL_TITLE = "original_title";
            final String TMD_VOTE_AVERAGE = "vote_average";

            if (movieJsonString != null) {

                JSONObject jsonMovieResults = new JSONObject(movieJsonString);
                JSONArray jsonMovieList = jsonMovieResults.getJSONArray(TMD_RESULTS);

                ArrayList<MoviesDetails> movieData = new ArrayList<>();

                for (int i = 0; i < jsonMovieList.length(); i++) {
                    JSONObject jsonMovieDetails = jsonMovieList.getJSONObject(i);

                    MoviesDetails moviesDetails = new MoviesDetails(
                            jsonMovieDetails.getString(TMD_ORIGINAL_TITLE),
                            jsonMovieDetails.getString(TMD_OVERVIEW),
                            jsonMovieDetails.getString(TMD_POSTER_PATH),
                            jsonMovieDetails.getString(TMD_RELEASE_DATE),
                            jsonMovieDetails.getInt(TMD_VOTE_AVERAGE)
                    );


                    movieData.add(moviesDetails);
                }

                return movieData;
            } else {
                Log.v("Movie JSon empty", "Empty");
                return null;
            }

        }


        @Override
        protected ArrayList<MoviesDetails> doInBackground(String... strings) {

            final String THE_MOVIE_DATABASE = "api.themoviedb.org";
            final String API_KEY = "df7ab6bd6119f184002f7065000d0fc8";
            final String MOVIE = "movie";
            final String APPEND_3 = "3";


            HttpURLConnection httpsURLConnection = null;
            BufferedReader bufferedReader = null;
            String moviesSearch = strings[0];

            String movieJsonString = null;

            try {

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority(THE_MOVIE_DATABASE)
                        .appendPath(APPEND_3)
                        .appendPath(MOVIE)
                        .appendPath(moviesSearch)
                        .appendQueryParameter("api_key", API_KEY);

                URL url = new URL(builder.build().toString());
                // Log.v("URL",builder.build().toString());

                // Create the request to OpenWeatherMap, and open the connection
                httpsURLConnection = (HttpURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("GET");
                httpsURLConnection.connect();


                InputStream inputStream = httpsURLConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();

                if (inputStream == null) {
                    movieJsonString = null;
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String newLine;

                while ((newLine = bufferedReader.readLine()) != null) {
                    stringBuffer.append(newLine + "\n");
                }

                if (stringBuffer == null) {
                    movieJsonString = null;
                }

                movieJsonString = stringBuffer.toString();
            } catch (IOException e) {
                Log.e("NOTHING IN RECIVIED", "error", e);

                movieJsonString = null;

            } finally {
                if (httpsURLConnection != null) {
                    httpsURLConnection.disconnect();
                }

                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Log.e("Movie Activity", "Error closing bufferedReader", e);

                    }
                }

            }

            try {
                return getMovieDataFromJson(movieJsonString);
            } catch (JSONException e) {

                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MoviesDetails> moviesDetailses) {
            super.onPostExecute(moviesDetailses);

            if (moviesDetailses != null) {
                movieData = moviesDetailses;
                adapterMovies = new AdapterMovies(getApplicationContext(), moviesDetailses);
                gridView.setAdapter(adapterMovies);

            }
        }
    }
}