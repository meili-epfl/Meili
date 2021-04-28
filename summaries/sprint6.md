# Summary for sprint 6

## Ahmed

This week I worked to introduce a new login option for new users that is facebook login and was hit by a new type of task. That is setting up the facebook API to work with our app. This task has proven cumbersome and I haven't taken it into consideration at all.

My time estimates are getting worse again because I am challenging myself to the limit; not by giving myself short time estimates intentionally but by choosing new "types of tasks" every sprint that I might learn from.

I would then use the next week to finish the last 2 week's features.


## Aman 

I refactored it to use a recycler view which is more efficient and to use the general database to remove redundant code. I also integrated the gallery and the camera features with the forum, allowing users to add photos to their posts.

I finished all my tasks for this sprint and took on an extra 4 hour task since I had more free time from other courses this week. My time estimates were almost on point with a couple of tasks that I over/under estimated by 1 hour. I also had some time to help my teammates with their PRs. I cleaned up the tests for the unified UI and helped with its refactoring to remove duplicated code.

Overall, I am happy with my work this week as I am becoming more efficient. This is mainly because I'm learning to better dig throug documentation and testing is becoming less of hurdle since I understand mocking.

## Ewan 
This week i made a tool that detects the angle made by a two finger rotation gesture. The tool works, but hasn't been tested yet. I had a lot of trouble applying the angle found in the editing feature, because rotating bitmaps make them bigger each time (think about fitting a rotated rectangle inside another). I also had trouble applying the rotation to the cropping tool, which overrides the onTouch function in the library, so I cannot make a custom onTouch function for it. I spent 7h fixing this particular problem, which threw off the other tasks I had planned this week.

The solution I ended up with was to rotate the ImageView directly, and rotate the bitmap only when we need the result of the rotation. This feature was not tested.

As I greatly underestimated the problems I would face, I asked Thomas to take my task of making the filter features. Having done that, he found a good library that maybe does everything we wanted to implement, so I will have a look at this library next week and maybe reimplement my features with this cleaner tool.

My time estimates were off, I have a lot of trouble estimating where problems will appear and how much time they will take, so I did not manage to test my code.
As said above, what I did works now, but I will have a look at the new tool Thomas has found before making the tests. Hopefully we will be able to merge the editing feature next week !


## Marcel 

This week, I worked on changing the API that we were using to fetch the POIs, we used to use Overpass API and now we are using Google Places API. This task was completed and merge at the beginning of the week. My second task has been to work on a version of the service that fetches POIs from Google API to implement a 2-level caching system.

Regarding my time estimates, for the first task I made an accurate one, however, for the second task, I have been off by 2 hours. The underestimation is due in some part because I wanted to take an extra task for this week (since the first one wasn't 8 hours long) and this probably made me underestimate it so that my workload was of 8 hours. I will try to be more accurate for next weeks even if that means adding 2 more hours of workload than expected.

Overall, my two tasks this week have been fun and I feel I am making progress in time estimation, testing and developping android apps.


## Thomas

This week, I first worked on integrating the changes Marcel made to the map with my poi info activiy, so that I can merge it. Then, I continued working on the photo editing. I added color picking for drawing on the image, and (since I had more time) the option to add filters to the image. I also added undo and redo buttons.

Overall, adding the color picker was faster than I thought, so I had more time to implement the filters additionally.

However, filters took more time to implement than I thought. So I did not have time to polish the activity to merge it. Next week, I have to be less greedy and first focus on my assigned task, merge it, and then think about doing something else.


## Yingxuan (Scrum Master)

This week I went back to the profile without MVVM, I did some back and forth with different git operations between commits to have the version I needed. I rebased my code to keep it updated with main, and had problem with libraries cannot be resolved, for Picasso and then Glide. After this I found a bug that I had no idea about how to fix it. 
My time estimation was bad and I lost confidence in it.

## Overall team

This week the team worked on both implementing new features and polishing existed ones. A unified UI is made to group features together and a two-level cache was introduced, photos can be added in forum and a new possibility for login was started as well. Different parts of the code were refactored to be more readable and efficient, the code quality was improved to have a A level maintainbility again.

The time estimation was off for some of us, as there is uncertainty when exploring new features.

There are active interactions between team members during standup meetings and code reviews, so that remarks and clarifications can be made. 


