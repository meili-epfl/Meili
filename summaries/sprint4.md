# Summary for sprint 4

## Ahmed

Since we are starting to finish some core functionalities, my work this weak was to create the activity that will merge all of them (i.e. the activity of the main released product).

My time estimates are still accurate since last week.

After finishing this main ui, I will move to add secondary features to our code. Starting with adding and upvote/downvote system for the forums.


## Aman 

I continued working on the POI review feature this sprint. I was able to finish its implementation, retouch the UI, and merge the feature into main with high coverage.

My time estimate for this week was correct as I am starting to have a good idea of how much time testing a certain feature can take.

After finishing all tasks assigned to me for this sprint, I looked into my task for the next sprint: implementing a cache for our Firestore databases. I found out that Firestore already implements a caching mechanism in the background. Since we have a small minority of features (currently only 1, namely the POIService, but this might increase in the future) which use other APIs, with the team I decided to assign this task a lower priority and moved it back to the product backlog.


## Ewan (Scrum Master)

This week I finished the forum and implemented changes requested from the team when reviewing my PR. I had some git problems that slowed me down, but overall didn't have that many problems. 

I also refactored the camera code, so that I understand everything and only keep the useful stuff from the tutorial I did. For this I spent some time reading the API and looking for the features we need. The basic functionality of seeing the preview and taking pictures is done, as well as being able to take landscape pictures. I need to figure out how to make tests.

Overall good time estimates, the git problems slowed me down so I couldn't start the camera tests.


## Marcel 

### TODO


## Thomas

### TODO


## Yingxuan

During this week, I worked on refactoring the code of profile to integrate the MVVM model, but I did not go as far as I wish, because it's time consuming to understand how to apply the pattern, and I have a bug that I did not managed to fix it. I also rebased and made the feature to use the same User model as in main branch,  by adding fields in User, I had to modify lots of tests involving User, I think I need to reconsider if this is the best way to do.

My time estimation is a little better, I asked other group members before the start of this sprint and learned that with MVVM model it may take time, so I planed to do this over 2 sprints. And since the refoactoring will require great change in testings, I decided after discussion with the team to refactor directly without finishing the test of last week's code which will be overwrite.


## Overall team

This week, the team managed to continue adding new features, even though some of us had less time available to work on the project. Pull requests are being well reviewed, which is a very good sign that the project is going well.

The stand up meetings being this close apart this week made it so we had less to say in the second one, but it was useful to keep each other updated and made it able to ask for help and clarifications.

The demo will be done during the holidays.

