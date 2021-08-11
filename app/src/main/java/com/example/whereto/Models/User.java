package com.example.whereto.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel(analyze = User.class)
@ParseClassName("User")
public class User extends ParseObject {

    public static final String KEY_EMAIL = "email";
    public static final String KEY_PICTURE = "profilePicture";
    public static final String KEY_NAME = "name";
    public static final String KEY_FOLLOWERS = "followers";
    public static final String KEY_FOLLOWING= "following";

    /***************************
     * Setters and Getters
     ***************************/

    // Place recommended name data
    public String getEmail() {
        return getString(KEY_EMAIL);
    }
    public void setEmail(String email) {
        put(KEY_EMAIL, email);
    }

    // Picture file of the recommendation
    public ParseFile getPicture() {
        return getParseFile(KEY_PICTURE);
    }
    public void setPicture(ParseFile picture) {
        put(KEY_PICTURE, picture);
    }

    // Location data
    public String getName() {
        return getString(KEY_NAME);
    }
    public void setLocation(String name) {
        put(KEY_NAME, name);
    }

    // Rate of the recommendation data
    public int getFollowers() {
        return getNumber(KEY_FOLLOWERS).intValue();
    }
    public void setFollowers(Number followers) {
        put(KEY_FOLLOWERS, followers);
    }

    // Rate of the recommendation data
    public int getFollowing() {
        return getNumber(KEY_FOLLOWING).intValue();
    }
    public void setFollowing(Number following) {
        put(KEY_FOLLOWING, following);
    }


    /***************************
     * Class Functions
     ***************************/
}
