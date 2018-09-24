package br.com.fbs.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import br.com.fbs.popularmovies.dto.MovieDto;
import br.com.fbs.popularmovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    MovieDto[] moviesDataFromJson;
    GridView mGridView;
    ProgressBar progressBarLoading;
    TextView textViewError;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridView = findViewById(R.id.gv_movies);
        progressBarLoading = findViewById(R.id.pb_loading);
        textViewError = findViewById(R.id.tv_error_message);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_sort),
                Context.MODE_PRIVATE);
        String preferenceSort = sharedPreferences.getString(getString(R.string.preference_sort), getString(R.string.sort_popular));

        makeFilmQuery(preferenceSort);

        mGridView.setOnItemClickListener(movieClickListener);

    }

    private final GridView.OnItemClickListener movieClickListener = new GridView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MovieDto movieDto = (MovieDto) parent.getItemAtPosition(position);

            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.putExtra("movieDetails", movieDto);
            startActivity(intent);
        }
    };

    private void makeFilmQuery(String sort) {
        URL searchUrl = NetworkUtils.buildUrlForFilms(sort);
        new FilmQueryTask().execute(searchUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_sort),
                Context.MODE_PRIVATE);

        if (id == R.id.action_top_rated) {
            String endpoint = "top_rated";
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.preference_sort), endpoint);
            editor.apply();
            makeFilmQuery(endpoint);
        }

        if (id == R.id.action_most_popular) {
            String endpoint = "popular";
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.preference_sort), endpoint);
            editor.apply();
            makeFilmQuery(endpoint);
        }

        return super.onOptionsItemSelected(item);
    }

    public class FilmQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String searchResults) {
            progressBarLoading.setVisibility(View.INVISIBLE);
            if (searchResults != null && !searchResults.equals("")) {
                showGridFilms();
                try {
                    moviesDataFromJson = getMoviesDataFromJson(searchResults);
                    mGridView.setAdapter(new ImageAdapter(MainActivity.this, moviesDataFromJson));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                showErrorMessage();
            }
        }
    }

    private MovieDto[] getMoviesDataFromJson(String receiptJson) throws JSONException {
        JSONObject moviesJson = new JSONObject(receiptJson);
        JSONArray resultsArray = moviesJson.getJSONArray("results");

        MovieDto[] movies = new MovieDto[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {
            movies[i] = new MovieDto();

            JSONObject movieInfo = resultsArray.getJSONObject(i);

            movies[i].setTitle(movieInfo.getString("title"));
            movies[i].setPosterPath(movieInfo.getString("poster_path"));
            movies[i].setSynopsis(movieInfo.getString("overview"));
            movies[i].setVoteAverage(movieInfo.getDouble("vote_average"));
            movies[i].setReleaseDate(movieInfo.getString("release_date"));
        }

        return movies;
    }

    private void showGridFilms() {
        textViewError.setVisibility(View.INVISIBLE);
        mGridView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mGridView.setVisibility(View.INVISIBLE);
        textViewError.setVisibility(View.VISIBLE);
    }
}