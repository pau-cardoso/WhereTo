package com.example.whereto;

import android.app.Application;

import com.example.whereto.Models.Followers;
import com.example.whereto.Models.LikeRecommendation;
import com.example.whereto.Models.Recommendation;
import com.example.whereto.Models.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Register your parse models
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Recommendation.class);
        ParseObject.registerSubclass(LikeRecommendation.class);
        ParseObject.registerSubclass(Followers.class);
        //ParseObject.registerSubclass(LikeRecommendation.class);
        // set applicationId, and server server based on the values in the back4app settings.
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("VDQgdzBb0873PhvDiDTC64CUmZt4O9cl5yKw1ztL")
                .clientKey("Xu9LUwbRtoyxLgl3YDYq3kHEbXUiTtPoha0PmPzu")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
