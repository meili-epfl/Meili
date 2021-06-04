# Meili
### Badges
[![Build Status](https://api.cirrus-ci.com/github/meili-epfl/Meili.svg)](https://cirrus-ci.com/github/meili-epfl/Meili)
[![Maintainability](https://api.codeclimate.com/v1/badges/a145f81ea17c85e8ef30/maintainability)](https://codeclimate.com/github/meili-epfl/Meili/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/a145f81ea17c85e8ef30/test_coverage)](https://codeclimate.com/github/meili-epfl/Meili/test_coverage)

## Test the app
To test the app you can download the branch and then you will have to add two files: `keys.xml` in ... and `google-service.json` in ... This files will be provided by the Meili team.

## Satisfaction of App Requirements
### Sensor usage: TODO: explain further
- Camera
- Location
- Giroscope/Orientation

### User support:
- You can sign in using Google (or Facebook TODO check wether this is fixed) and this will give further capabilities to the users. 
- When a user is signed in, he/she will be able to customize his/her profile, chat with people around them, and make posts and review their favourite points of interest

### Local cache:
- We have implemented our own local cache that will fetch the Google Places API response with the points of interest around the user. We implemented a 2 level cache. The first level is that we store the data in the object, and the second level is that we store the data on the mobile storage. The way we handle the data is the following. We implemented a write-through cache so when a response is received from the API, we will write it both in storage and in the object. The process of fetching data is more comples. If the data in the cache is not valid or not present and we have internet connection, we will fetch the information from the API. If we don't have internet connection and we have some data, even if not valid we will return it. If data is valid then we will return the data in the highest level of the cache (the data is the same but the higher the level, the faster it will be. Finally, in order to determing the validity of the cache we use two metrics, time  (we consider that data is valid during 1 hour) and distance, if the cached request was further away than 1km we will consider the data invalid. 
- Firebase cache TODO: explain
