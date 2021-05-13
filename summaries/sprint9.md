# Summary for sprint 9

## Ahmed
##TODO

## Aman

This week, I worked on the poi detection user story. I first used google's Landmark Detection API which took much less time than what I had predicted. Turned out that the API did all of the work and all I needed to do was call the API and handle the response.

However, this API only works for well known landmarks like the EIffel tower but not for more day-to-day POIs such as the EPFL Rolex. I decided to improve poi detection using my own implementation and estimated it would take around 6h, which was the time I had remaning in the sprint. To this end, I used the accelerometer and magnetic field sensors to calculate the orientation of a user's phone. This allowed me to find which POI was nearest in the user's field of view. This now allows the user to see what POI they are looking at just by orienting their phone towards it! This feature took me 7h instead of my 6h estimation because I had to learn more (than expected) about the different layouts in android.

I combined these 2 features into _Meili Lens_ and added them to the main map actiivty so that the user has fast access to poi detection, allowing them to quickly find out the name (and maybe details in the future!) of the POI they are looking at.


## Ewan 
This week I worked on the profile. I improved the UI so that buttons are better placed, and that they are only available when it makes sense. I also made it so that you can click on your friends in your friends list to see their profile (not editable). I'm very happy with my design choices, I think it looks super cool.

As I wanted to make things pretty, I spent extra time on that.

I also had an unexpected error that only happened on Cirrus which made me do overtime (I wouldn't consider that as part of the time estimates, as the tests passed locally). I think the reason was that Cirrus doesn't use the same devices as the android emulator, which makes certain buttons unreachable if we aren't careful with how we implement the UI. That was a learning experience.

Other than that, my time estimates were perfect for the implementation of the features and the tests, which is the most important. I'm happy with my work this week.


## Marcel (Scrum Master)

#todo

## Thomas

#TODO

## Yingxuan

#TODO

## Overall team

I believe the team has overcomed the destabilization sprint exceedingly well. We have managed to implement the user story that the assistants added (calling it Meili Lens) but also we were able to implement many other cool features as improving the UI for the friends list, adding the possibility to see profiles of other users, being able to save your favorite pois and even fixing some bugs we discovered in the chat. Finally, we also started addressing the comments from the code review we received from the assistants by revising the past code but also by having them in mind while writing new code.
We are continuing to do at least 2 stand-ups per week which are proving to be really useful to share what we are doing and give our opinions on how to make the app better and how to help the other teamates. 
