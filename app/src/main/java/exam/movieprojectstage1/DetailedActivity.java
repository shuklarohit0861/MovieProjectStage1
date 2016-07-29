package exam.movieprojectstage1;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailedActivity extends AppCompatActivity {

    private TextView title,overView,releaseDate,voteAvg;
    private ImageView imageView;
    String url = "http://image.tmdb.org/t/p/w500/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        findByViewALL();
        Bundle extra = getIntent().getExtras();

        title.setText(extra.getString("Title"));
        overView.setText(extra.getString("OverView"));
        releaseDate.setText(extra.getString("Release_Date"));
        voteAvg.setText(String.valueOf(extra.getInt("Vote")));
        Picasso.with(getApplicationContext()).load(url+extra.getString("Poster_path")).into(imageView);


    }

    private void findByViewALL()
    {
        title = (TextView) findViewById(R.id.movie_title_text_view);
        overView = (TextView)findViewById(R.id.OverView_text_view);
        releaseDate = (TextView)findViewById(R.id.release_date_textView);
        voteAvg = (TextView)findViewById(R.id.text_view_votes);
        imageView = (ImageView)findViewById(R.id.posterImageView);
    }
}
