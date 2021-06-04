# Meili
### Badges
[![Build Status](https://api.cirrus-ci.com/github/meili-epfl/Meili.svg)](https://cirrus-ci.com/github/meili-epfl/Meili)
[![Maintainability](https://api.codeclimate.com/v1/badges/a145f81ea17c85e8ef30/maintainability)](https://codeclimate.com/github/meili-epfl/Meili/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/a145f81ea17c85e8ef30/test_coverage)](https://codeclimate.com/github/meili-epfl/Meili/test_coverage)

## Test the app
Clone the main branch and add the files `keys.xml` in ... and `google-service.json` in ... given by the Meili team.

## Satisfaction of App Requirements

## Description:

Interact with people while visiting new places ! See what others think about an attraction, get their recommendations for the best cafés, and maybe even learn about the place’s history from experts! Fancy more interaction and wanna share a pizza ? Hop on to the live chat with people around you!

Meili creates feeds based on points of interest (POIs). Each POI has multiple tabs letting you create and see posts (text, pictures) in addition to giving you access to a live chat with people in the area. After leaving the area, you can still post for a little while (live chat will be restricted to people who are physically in the area), but after having left for too long, you will only be able to read.


## Requirements:

### Split app model: 

- Google Places (Search, Details, Photos): to get information about the POIs
- Google Firebase: Authentication and Database
- Google Maps: to show a live map of the user and the different POIs available around them. (similar to Pokemon Go with gyms)


### Sensor usage:

- Geolocation to determine location on the map
- Gyroscope to determine orientation on the map
- Compass
- Camera to take photos which can then be shared (maybe filters, cropping)


### User support:

- You can sign in using Google (or Facebook TODO check wether this is fixed) and this will give further capabilities to the users. 
- When a user is signed in, he/she will be able to customize his/her profile, chat with people around them, and make posts and review their favourite points of interest

The user will be able to:

- The user will be able to access and make posts associated with the points of interest he/she visited but only if he/she visited this place.
- The user will be able to chat with other people who are at that moment close to the same point of interest.

Advantages to being logged in:

- Add friends
- View history of visited POIs, posts, etc.
- User profile (username, profile picture, etc.)
- Access live chat (in addition only people close to the POI can access the chat)
- Be able to post
- Be able to interact with posts: vote (thank) and comment


Personalization:

- Limiting users to only interact with visited points of interest.
- User profiles can showcase the places they have visited (if they choose to do so)
- Favorite/Save a POI or a post

### Local cache:

- We have implemented our own local cache that will fetch the Google Places API response with the points of interest around the user. We implemented a 2 level cache. The first level is that we store the data in the object, and the second level is that we store the data on the mobile storage. The way we handle the data is the following. We implemented a write-through cache so when a response is received from the API, we will write it both in storage and in the object. The process of fetching data is more comples. If the data in the cache is not valid or not present and we have internet connection, we will fetch the information from the API. If we don't have internet connection and we have some data, even if not valid we will return it. If data is valid then we will return the data in the highest level of the cache (the data is the same but the higher the level, the faster it will be. Finally, in order to determing the validity of the cache we use two metrics, time  (we consider that data is valid during 1 hour) and distance, if the cached request was further away than 1km we will consider the data invalid.

- Firebase cache TODO: explain


### Offline mode:

- Users will be able to have access to all of the points of interest visited in the UI Map.
- Users will be able to read all of the Post/messages/pictures of the visited POIs that were retrieved since last internet connection

### Overview of main features:
- Google Sign-in (TODO: facebook sign in?)
- Pesonalizable profile (the user can change its picture, name and status)
- Review points of interest and give a rating
- Post on points of interest and add pictures
- On-app camera and editable pictures
- Click on someones name or picture to visit their profile
- Dark/Light mode availabe
- Save your favourite points of interest which will be displayed in your profile
- Detailed information about each point of interest
  - Take me there capabiltiy (will open google maps and give you indications to the point of interest)
  - Possibility to call the point of interest
  - Direct link to the point of interest website
- Feed with posts of points of interest close to you
- Add friends nearby (using bluetooth technology)
- Chat with friends
- Chat with people around a point of interest
- TODO: notifications for friend chat??
- TODO: explain Meili Lens
- TODO: what else?


