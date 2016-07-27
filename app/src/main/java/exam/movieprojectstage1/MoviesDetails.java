package exam.movieprojectstage1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rohit Shukla on 26-07-2016.
 */
public class MoviesDetails implements Parcelable {

    private String movieTitle;
    private String overView;
    private String posterPath;
    private String release_date;
    private int voteAverage;

    public MoviesDetails(String movieTitle,String overView,String posterPath,String release_date,int voteAverage)
    {
        this.movieTitle = movieTitle;
        this.overView = overView;
        this.posterPath = posterPath;
        this.release_date = release_date;
        this.voteAverage = voteAverage;
    }

    public String getOverView() {
        return overView;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public int getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(int voteAverage) {
        this.voteAverage = voteAverage;
    }






    protected MoviesDetails(Parcel in) {
        this.movieTitle = in.readString();
        this.overView = in.readString();
        this.posterPath = in.readString();
        this.release_date = in.readString();
        this.voteAverage = in.readInt();

    }

    public static final Creator<MoviesDetails> CREATOR = new Creator<MoviesDetails>() {
        @Override
        public MoviesDetails createFromParcel(Parcel in) {
            return new MoviesDetails(in);
        }

        @Override
        public MoviesDetails[] newArray(int size) {
            return new MoviesDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(movieTitle);
        parcel.writeString(overView);
        parcel.writeString(posterPath);
        parcel.writeString(release_date);
        parcel.writeInt(voteAverage);
    }
}
