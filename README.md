# WhereTo
===

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
This app lets backpack travelers create a social media where they can share recommendations of different places to visit along their journey to a destination. Users can add a destination and the app will show the route to take, with points of the recommendations other backpack travelers have gone to and recommended. Users can also post a recommendation of the places they went through the journey and want to recommend to other backpackers. 

### App Evaluation
- **Category:** Travel
- **Mobile:** This app is uniquely mobile since it lets you use your phone as a GPS for backpack travelers. You can see other backpacker's recommendations in your way to your destination and have guidance on where you are and where you need to go. This will only be accessible through a mobile app development. 
- **Story:** Allows travelers to see recommendations from other users and post their own, creating a community of mutual likings and reviews from different places so you can enjoy all the journey until you get to your destination.
- **Market:** The market for this app is for travelers who would like to know how to get to your destination and get different recommendations at the same time. 
- **Habit:** This app is a habit forming for travelers since they'll have to open the app each time they'd like for assistance on their journey or recommendations. This will also allow the users to create their own content by posting recommendations on places they've gone to. 
- **Scope:** This app starts as a way to see your journey and extending recommendations for other travelers, but can grow to create a whole commnity where people can follow each other or share the places they've traveled to. 

## Product Spec

### 1. User Stories (Required and Optional)

####    Required Must-have Stories
- User can login
- User can logout
- User can create new account 
- User can search destinations 
- User can see the map rendered using Google Map API with recommendations pin pointed 
- User can see recommendations 
- User can post recommendations 
- User can filter recommendations by category 
- User can double tap recommendation to like 
- Add animation for clicking recommendations or pin points in recommendations 
- User can see their profile page with the recommendations they created 

####    Optional Nice-to-have Stories
- User can post stories
- User can see other stories
- User can follow/unfollow more backpackers
- User can see their profile page with his recommendations and trips
- User can view other user’s profiles and see their recommendations and trips
- User can see a list of their followers
- User can see a list of their following
- User can see most popular recommendations and destinations
- User can create collections with favorite places
- User can switch between Light/Dark Mode
- User can like recommendations
- User can comment recommendations

### 2. Screen Archetypes

1. Login Screen
    - User can login
2. Registration screen
    - User can create new account
3. Stream
    - User can see the recommendations
    - User can like/unlike recommendations
    - User can comment recommendations
    - User can logout
4. Creation
    - User can post recommendations
5. Search
    - User can filter recommendations by category
    - User can search destinations
6. Map View
    - User can see the map
    - User can see their route for their destination

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Map view (with pin points of recommendations by users)
* Stream view of recommendations posted
* Profile

**Flow Navigation** (Screen to Screen)

* Login screen
   * Signup screen
   * Map screen
* Signup screen
   * Map screen
* Map screen
    * Detail view of recommendations
* Stream screen
    * Detail view of recommendations
* Creation Screen
    * Map screen (after you finish posting the recommendation)
