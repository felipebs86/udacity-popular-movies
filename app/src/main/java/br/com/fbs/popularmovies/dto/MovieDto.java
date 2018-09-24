package br.com.fbs.popularmovies.dto;

import java.io.Serializable;

/**
 * Created by felipe on 21/09/18.
 */

public class MovieDto implements Serializable {
    private String mTitle;
    private String mPosterPath;
    private String mSynopsis;
    private Double mVoteAverage;
    private String mReleaseDate;

    public MovieDto() {
    }

    public void setTitle(String Title) {
        mTitle = Title;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    public void setSynopsis(String synopsis) { mSynopsis = synopsis; }

    public void setVoteAverage(Double voteAverage) {
        mVoteAverage = voteAverage;
    }

    public void setReleaseDate(String releaseDate) { mReleaseDate = releaseDate; }

    public String getTitle() {
        return mTitle;
    }

    public String getPosterPath() {
        final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/w185";
        return POSTER_BASE_URL + mPosterPath;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public Double getVoteAverage() {
        return mVoteAverage;
    }

    public String getReleaseDate() { return mReleaseDate; }

}
