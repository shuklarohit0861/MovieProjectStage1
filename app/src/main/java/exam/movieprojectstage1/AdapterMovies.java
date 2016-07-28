package exam.movieprojectstage1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by rohit shukla on 26-07-2016.
 */
public class AdapterMovies extends ArrayAdapter<MoviesDetails> {

    public AdapterMovies (Context context , List<MoviesDetails> moviesDetailes)
    {
        super(context,0,moviesDetailes);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        MoviesDetails moviesDetails = getItem(position);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_titles_layout,parent,false);
        }

        String url = "http://image.tmdb.org/t/p/w500/";

        Picasso.with(getContext()).load(url+moviesDetails.getPosterPath())
                .into((ImageView) convertView.findViewById(R.id.imageView_MovieDataBase));

        return convertView;
    }




}
