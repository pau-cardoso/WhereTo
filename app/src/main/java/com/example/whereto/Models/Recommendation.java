package com.example.whereto.Models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Date;

@Parcel(analyze = Recommendation.class)
@ParseClassName("Recommendation")
public class Recommendation extends ParseObject {

    public static final String KEY_LOCATION = "location";
    public static final String KEY_USER = "user";
    public static final String KEY_EAT = "Food";
    public static final String KEY_STAY = "Stay";
    public static final String KEY_VISIT = "Visit";

    /***************************
     * Setters and Getters
     ***************************/

    // Location data
    public String getLocation() {
        return getString(KEY_LOCATION);
    }
    public void setLocation(String location) {
        put(KEY_LOCATION, location);
    }

    // User data
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    // Category marks for the recommendations
    public boolean getEat() {
        return getBoolean(KEY_EAT);
    }
    public void setEat(boolean eat) {
        put(KEY_EAT, eat);
    }

    public boolean getStay() {
        return getBoolean(KEY_STAY);
    }
    public void setStay(boolean stay) {
        put(KEY_STAY, stay);
    }

    public boolean getVisit() {
        return getBoolean(KEY_VISIT);
    }
    public void setVisit(boolean visit) {
        put(KEY_VISIT, visit);
    }

    // Calculates the date in which a recommendation was created and returns
    public static String calculateTimeAgo(Date createdAt) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }
        return "";
    }
}
