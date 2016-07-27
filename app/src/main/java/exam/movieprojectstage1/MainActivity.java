package exam.movieprojectstage1;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

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
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private AdapterMovies adapterMovies;
    private ArrayList<MoviesDetails> movieData;
    private MoviesDetails [] moviesDetails = {
            new MoviesDetails("Movie 1","movie overview","/cGOPbv9wA5gEejkUN892JrveARt.jpg","movie releasedate",1),
            new MoviesDetails("movie 2","movie overView","/6FxOPJ9Ysilpq0IgkrMJ7PubFhq.jpg","movie releasedate",2),
            new MoviesDetails("movie 3","movie overView","/ghL4ub6vwbYShlqCFHpoIRwx2sm.jpg","movie release Date",3),
            new MoviesDetails("movie 4","movie overView","/z09QAf8WbZncbitewNk6lKYMZsh.jpg","movie release Date",4)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapterMovies = new AdapterMovies(getApplicationContext(), Arrays.asList(moviesDetails));
        GridView gridView = (GridView)findViewById(R.id.movieTitle_main);
        gridView.setAdapter(adapterMovies);
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

            FatchMovieData fatchdata = new FatchMovieData() ;
            fatchdata.execute("popular");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class FatchMovieData extends AsyncTask<String,Void,MoviesDetails []>
    {

        private MoviesDetails [] getMovieDataFromJson(String movieJsonString) throws JSONException
        {
            final String TMD_RESULTS = "results";
            final String TMD_POSTER_PATH = "poster_path";
            final String TMD_OVERVIEW = "overview";
            final String TMD_RELEASE_DATE = "release_date";
            final String TMD_ORIGINAL_TITLE = "original_title";
            final String TMD_VOTE_AVERAGE = "vote_average";

            if (movieJsonString != null) {

                JSONObject jsonMovieResults = new JSONObject(movieJsonString);
                JSONArray jsonMovieList = jsonMovieResults.getJSONArray(TMD_RESULTS);
                MoviesDetails[] movieDetail = new MoviesDetails[jsonMovieList.length()];

                for (int i = 0; i < jsonMovieList.length(); i++) {
                    JSONObject jsonMovieDetails = jsonMovieList.getJSONObject(i);

                    movieDetail[i].setPosterPath(jsonMovieDetails.getString(TMD_POSTER_PATH));
                    movieDetail[i].setRelease_date(jsonMovieDetails.getString(TMD_RELEASE_DATE));
                    movieDetail[i].setMovieTitle(jsonMovieDetails.getString(TMD_ORIGINAL_TITLE));
                    movieDetail[i].setOverView(jsonMovieDetails.getString(TMD_OVERVIEW));
                    movieDetail[i].setVoteAverage(jsonMovieDetails.getInt(TMD_VOTE_AVERAGE));
                }

                return movieDetail;
            }
            else
            {
                Log.v("Movie JSon empty","Empty");
                return null;
            }

        }




        @Override
        protected MoviesDetails[] doInBackground(String... strings) {

             final String THE_MOVIE_DATABASE = "api.themoviedb.org";
            final String API_KEY = "their is a api_key";
            final String MOVIE = "movie";
            final String APPEND_3 = "3";


            HttpURLConnection httpsURLConnection = null;
            BufferedReader bufferedReader = null;
            String moviesSearch = strings[0];

            String movieJsonString = null;

            try{

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority(THE_MOVIE_DATABASE)
                        .appendPath(APPEND_3)
                        .appendPath(MOVIE)
                        .appendPath(moviesSearch)
                        .appendQueryParameter("api_key",API_KEY);

                URL url = new URL(builder.build().toString());
                // Log.v("URL",builder.build().toString());

                // Create the request to OpenWeatherMap, and open the connection
                httpsURLConnection = (HttpURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("GET");
                httpsURLConnection.connect();


                InputStream inputStream = httpsURLConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();

                if(inputStream == null)
                {
                    movieJsonString = null;
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String newLine ;

                while ((newLine = bufferedReader.readLine())!= null)
                {
                    stringBuffer.append(newLine +"\n");
                }

                if(stringBuffer == null)
                {
                    movieJsonString = null;
                }

                movieJsonString = stringBuffer.toString();
            }
            catch (IOException e)
            {
                Log.e("NOTHING IN RECIVIED","error",e);

                movieJsonString = null;

            }

            finally {
                if(httpsURLConnection != null)
                {
                    httpsURLConnection.disconnect();
                }

                    if(bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        }
                        catch (IOException e)
                        {
                        Log.e("Movie Activity","Error closing bufferedReader",e);

                        }
                    }



            }


            try {
                return getMovieDataFromJson(movieJsonString);
            }
            catch (JSONException e)
            {

                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MoviesDetails[] moviesDetailses) {
            super.onPostExecute(moviesDetailses);

            if(moviesDetailses != null)
            {
               movieData = new ArrayList<>(Arrays.asList(moviesDetailses));

                adapterMovies.clear();
                adapterMovies.addAll(movieData);

            }
        }
    }
}
