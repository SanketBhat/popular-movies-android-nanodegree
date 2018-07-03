
/*
 * Copyright 2018 Sanket Bhat
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.udacity.sanketbhat.popularmovies.model;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity(tableName = "favorites")
public class Movie implements Parcelable {

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    @PrimaryKey
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("vote_average")
    private double voteAverage;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("genre_ids")
    private int[] genreIds = null;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("overview")
    private String overview;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("adult")
    private boolean adult;
    @SerializedName("videos")
    private VideoResponse videoResponse;
    @SerializedName("genres")
    private List<Genre> genres;
    @SerializedName("reviews")
    private ReviewResponse reviewResponse;
    public Movie() {

    }

    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        voteAverage = in.readDouble();
        releaseDate = in.readString();
        genreIds = in.createIntArray();
        backdropPath = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        adult = in.readByte() != 0;
    }

    public VideoResponse getVideoResponse() {
        return videoResponse;
    }

    public void setVideoResponse(VideoResponse videoResponse) {
        this.videoResponse = videoResponse;
    }

    public ReviewResponse getReviewResponse() {
        return reviewResponse;
    }

    public void setReviewResponse(ReviewResponse reviewResponse) {
        this.reviewResponse = reviewResponse;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public int[] getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(int[] genreIds) {
        this.genreIds = genreIds;
    }

    public String getGenresString() {
        //If Genre list is not null, return its names
        if (genres != null && genres.size() > 0) {
            String strings[] = new String[genres.size()];
            for (int i = 0; i < genres.size(); i++) {
                strings[i] = genres.get(i).getGenreName();
            }
            return TextUtils.join(", ", strings);
        }
        if (genreIds == null) return "";
        String[] strings = new String[genreIds.length];
        for (int i = 0; i < genreIds.length; i++) {
            strings[i] = Genre.getGenreString(genreIds[i]);
        }
        return TextUtils.join(", ", strings);
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDisplayReleaseDate() {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(getReleaseDate());
            return SimpleDateFormat.getDateInstance().format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getReleaseDate();
    }

    public List<Video> getVideos() {
        if (videoResponse == null) return null;
        return videoResponse.getVideos();
    }

    public List<Review> getReviews() {
        if (reviewResponse == null) return null;
        return reviewResponse.getReviews();
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeDouble(voteAverage);
        dest.writeString(releaseDate);
        dest.writeIntArray(genreIds);
        dest.writeString(backdropPath);
        dest.writeString(overview);
        dest.writeString(posterPath);
        dest.writeByte((byte) (adult ? 1 : 0));
    }
}
