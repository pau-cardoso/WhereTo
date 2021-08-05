package com.example.whereto.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel(analyze = Followers.class)
@ParseClassName("Followers")
public class Followers extends ParseObject {
    public static final String KEY_FOLLOWER = "follower";
    public static final String KEY_FOLLOWING = "following";

    /***************************
     * Setters and Getters
     ***************************/

    // User data
    public ParseUser getFollower() {
        return getParseUser(KEY_FOLLOWER);
    }
    public void setFollower(ParseUser user) {
        put(KEY_FOLLOWER, user);
    }

    public ParseUser getFollowing() {
        return getParseUser(KEY_FOLLOWING);
    }
    public void setFollowing(ParseUser user) {
        put(KEY_FOLLOWING, user);
    }
}
