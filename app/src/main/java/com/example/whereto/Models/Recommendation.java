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

    public static final String KEY_PLACE = "place";
    public static final String KEY_REVIEW = "review";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_RATE = "rate";
    public static final String KEY_PRICE_RATE = "priceRate";
    public static final String KEY_PICTURE = "picture";
    public static final String KEY_USER = "user";
    public static final String KEY_EAT = "foodCategory";
    public static final String KEY_STAY = "stayCategory";
    public static final String KEY_VISIT = "visitCategory";

    /***************************
     * Setters and Getters
     ***************************/

    // Place recommended name data
    public String getPlace() {
        return getString(KEY_PLACE);
    }
    public void setPlace(String place) {
        put(KEY_PLACE, place);
    }

    // Review data
    public String getReview() {
        return getString(KEY_REVIEW);
    }
    public void setReview(String review) {
        put(KEY_REVIEW, review);
    }

    // Location data
    public String getLocation() {
        return getString(KEY_LOCATION);
    }
    public void setLocation(String location) {
        put(KEY_LOCATION, location);
    }

    // Rate of the recommendation data
    public float getRate() {
        return getNumber(KEY_RATE).floatValue();
    }
    public void setRate(Number rate) {
        put(KEY_RATE, rate);
    }

    // Price rate of the recommendation
    public int getPriceRate() {
        return getNumber(KEY_PRICE_RATE).intValue();
    }
    public void setPriceRate(Number priceRate) {
        put(KEY_PRICE_RATE, priceRate);
    }

    // Picture file of the recommendation
    public ParseFile getPicture() {
        return getParseFile(KEY_PICTURE);
    }
    public void setPicture(ParseFile picture) {
        put(KEY_PICTURE, picture);
    }

    // User data
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    /* Category marks for the recommendations */
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

    /***************************
     * Class Functions
     ***************************/

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
