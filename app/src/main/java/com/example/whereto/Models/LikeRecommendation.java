package com.example.whereto.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel(analyze = LikeRecommendation.class)
@ParseClassName("LikeRecommendation")
public class LikeRecommendation extends ParseObject {
    public static final String KEY_RECOMMENDATION = "recommendationId";
    public static final String KEY_USER = "userId";

    /***************************
     * Setters and Getters
     ***************************/

    // User data
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public ParseObject getRecommendation() {
        return getParseObject(KEY_RECOMMENDATION);
    }
    public void setRecommendation(ParseObject recommendation) {
        put(KEY_RECOMMENDATION, recommendation);
    }

}
