# Summary for sprint 5

## Ahmed

This week I worked on adding the upvote/downvote feature for the forum posts. At first it seemed like a trivial task but using the transaction seemed different to what I have seen before. I have now finished the functionality but the tests are still due.

My estimates this time were wrong due to the fact that the task seemed easy but wasn't.

I still feel like I am improving a lot by these challenges. I have learned this time to read a bit about the documentation of an API I am willing to use for the sprint before trying to make an accurate guess of the time this feature is going to take.


## Aman

This week, I was able to finish the tasks I assigned to myself. I completed the choice, upload, and download (with compression) of an image to/from Firebase Storage.

My time estimates were very good in general. I underestimated one task (the upload) by an hour or so. I used this freed-up time to refactor one of my old features to use a general database implementation and remove redundant code. I also used a bit of my free time to help one of teammates with Mockito and dependency injection.

Next week, I plan on finishing my current user story by integrating the upload/download, camera, and the forum features. 


## Ewan

This week I worked on cropping photos, resizing and rotating the photos. I'm not quite done with the feature, because Android does not provide a way to get an angle from a two finger rotating motion. I am still figuring out how to implement this. The cropping functionality and the function to rotate from a given angle are done, but not yet tested.

I also helped Thomas to figure out how to make cirrus work for the camera tests, as they only passed locally. This feature was merged successfully.

I'm not quite sure what counts for last week or this week, but I finished the forum by implementing the last PR nitpicks during the holidays. I also made the presentation video for the mid semester demo.

As we weren't sure what tools to use and what to implement for the photo editing, it took me some extra time to figure out what libraries to use, so my time estimates are off quite a bit. I should also account for testing more in my future time estimates.

## Marcel 

This week, I worked on finishing the task of implementing a user-personalised POI list. This task was from last week and I was planning to spend on it 4 more hours and it is the actual time I spent. Moreover, I had my big task of the week which was to display the POIs on the Map using Markers and Clusters. I planned to spend 8 hours on it, however it was slightly faster and took me around 7 hours and a half. 

Something I believe I could improve for next week is to collaborate more with the team. It is true, however,  that this my tasks were really different from the ones from the rest of the team.

Something positive about this week is that I am happy since my time estimates are starting to be more accurate. Moreover, I am also happy because I was able to find a solution pretty efficiently in time to the problem of how to dynamically update the markers on the map. I didn't find any information on the internet on how to achieve this with cluster items.

Finally, next week, I plan to work, among other tasks, on using, in addition to the Overpass API, the Google Places API to get more detailed information about the POIs.

## Thomas (Scrum Master)

This week, I worked on implementing drawing with the finger on top of pictures which have been taken. With Aman's tips, I was also able to use mockito to test the poi info activity and achieve high coverage. I also managed to figure out how to run tests which use the camera on cirrus so I was able to merge the photo branch as well.

I am happy with what I have achieved for the drawing feature, as I managed to implement what I has envisioned without making any concessions because of technical obstacles. Also, I arrived a bit early at each stand up meeting this week.

I didn't manage to totally finish what I has planned to do, as I still need to implement the color picker for the drawing feature, and to test the activity. But I have caught up on branches which I hadn't managed to merge, so now I will be able to focus more on my work.


## Yingxuan

TODO


## Overall team

During this two week long sprint, the team has implemented many new features, including a camera and photo editing, using a whole new API for points of interest, storing and retrieving information from Firebase, and additional features for the forum. Most of the app's core features are nearly done being implemented, and further sprints will start focusing more on incorporating everything together and polishing certain parts of the app.

Team members are keen to help each other, by clarifying how their code functions or by explaining aspects of Android development which they have well understood. Collaboration is also apparent in the apparition of reusable pieces of code, with general interfaces having been developed to model databases and interact with Firebase.

For next week, the team still needs to work a bit on honing their time estimation skills for feature implementation, as certain tasks are left unfinished and others have been added mid-sprint.

