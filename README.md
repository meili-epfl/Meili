# Meili
### Badges
[![Build Status](https://api.cirrus-ci.com/github/meili-epfl/Meili.svg)](https://cirrus-ci.com/github/meili-epfl/Meili)
[![Maintainability](https://api.codeclimate.com/v1/badges/a145f81ea17c85e8ef30/maintainability)](https://codeclimate.com/github/meili-epfl/Meili/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/a145f81ea17c85e8ef30/test_coverage)](https://codeclimate.com/github/meili-epfl/Meili/test_coverage)

## Test the app
Clone the main branch and add the files `keys.xml` in `app/src/main/res/values/` folder and `google-service.json` in `app/` folder given by the Meili team.

## Description:

Interact with people while visiting new places! See what others think about an attraction, get their recommendations for the best cafés, and maybe even learn about the place’s history from experts! Fancy more interaction and wanna share a pizza? Hop on to the live chat with people around you!

Meili creates feeds based on points of interest (POIs). Each POI has multiple tabs letting you create and see posts (text, pictures) in addition to giving you access to a live chat with people in the area. After leaving the area, you can still post for a little while (live chat will be restricted to people who are physically in the area), but after having left for too long, you will only be able to read.


## App Requirements

### Overview of main features:
- Google and Facebook sign in
- Customizable profile (picture, name, bio)
- Review (rating, title and summary) points of interest: add new or edit existing (unique review per user per point of interest) 
- Post (picture, title, text) on points of interest
  - Sort posts by newest, oldest, or most popular
  - Upvote/Downvote
  - Comments (which can be sorted by newest or oldest)
- In-app camera with editing: cropping, rotating, filters, emojis, text, drawing
- Click on someones name or picture to visit their profile
- Dark/Light/System modes
- Save your favourite points of interest which will be displayed in your profile
- Detailed information about each point of interest
  - Take me there capabiltiy (will open google maps and give you directions to the point of interest)
  - Possibility to call the point of interest
  - Snippet of and link to the point of interest website
- Feed with posts from points of interest close to you
- Add friends nearby (using bluetooth technology)
- Private chat with friends with notifications
- Public chat for each point of interest
- Meili Lens
  - Orient phone in direction of a building/point of interest to find out its name. It will also be colored differently (in purple) on the map, allowing the user to click on it directly without having to look for it. This works with all points of interest in Meili.
  - Take a photo of a landmark and find out what it is (uses Google's Landmark Detection API). This works only with well-known landmarks (Eiffel tower, Rockefeller Center, etc...)

### Split app model:

- Firebase's Realtime Database (for chat messages) and Firestore (for everything else) are used as our backend.

### Sensor usage:

- Geolocation to determine location on the map
- Accelerometer and magnetic field sensors to determine orientation for Meili Lens (poi detection)
- Camera for Meili Lens (landmark detection)
- Camera for taking photos which can be edited(filters, text, cropping, emojis) and shared
- Bluetooth to find and add nearby friends

### User support:

- A user can choose to stay anonymous or sign in using Google (or Facebook TODO) and this will give further capabilities to the users. 
- When a user is signed in, they will be able to customize their profile, chat with people around them, make posts, and review points of interest.

Advantages to being logged in:

- Add friends
- Favorite POIs and history of posts
- Customizable profile (username, profile picture, and bio)
- Chat
- Make posts and interact with them (upvote/downvote and comment)
- Add reviews

Without logging in, the user can benefit from the app in read-only mode.

### Local cache:

- We have implemented our own local cache that will fetch the Google Places API response with the points of interest around the user. We implemented a 2 level cache. The first level is that we store the data in the object, and the second level is that we store the data on the mobile storage. The way we handle the data is the following. We implemented a write-through cache so when a response is received from the API, we will write it both in storage and in the object. The process of fetching data is more comples. If the data in the cache is not valid or not present and we have internet connection, we will fetch the information from the API. If we don't have internet connection and we have some data, even if not valid we will return it. If data is valid then we will return the data in the highest level of the cache (the data is the same but the higher the level, the faster it will be. Finally, in order to determin the validity of the cache we use two metrics, time (we consider that data is valid during 1 hour) and distance, if the cached request was further away than 1km we will consider the data invalid. The cache service we implemented is general meaning that one can easily use it to cache various things in the future.

- We also implemented a UserPreferences class for storing small attributes like user's theme preference (only) in local storage. This allows the app to be responsive on startup since it doesn't have to fetch any remote data and allows unsigned in users to also have these basic customizations. 

- For other things such as posts, reviews, chat, etc. we relied on Firebase's automatic caching since there would be no point in reinventing the wheel.

### Offline mode:

Users will be able to:
- customize their profile
- use Nearby Friend to become friends with people near them
- have access to the posts and reviews of their favorite points of interest
- have access to the posts and reviews of the points of interest which were around them when they went offline
