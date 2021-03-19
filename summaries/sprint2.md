# Summary for sprint 2

## Ahmed (Scrum master)

I worked on refactoring the code from last sprint to follow the MVVM model. As a result I was able to write better tests for the activities that also use mocks.

I learned from last time all there is to version control so I didn't waste any time on that this week. On the other hand, my time estimates were still off, so I didn't have to implement all of the sprint tasks. I still have to make the chat log more consistent (this should be easier now since we start the activity with the POI) and work on the chat groups themselves.

In the future, I will try and improve my time estimates further.


## Aman 

I worked on finishing the map activity and its test. I also learned about Firebase in order to implement a POI database but, after talking to my colleagues, we realized that it is not needed. With my freed-up time, I started implementing a review system. For this, I have learned about the MVVM pattern and the RecyclerView. Since I still need to connect the review system to the Firebase database, I will not be able to merge this feature in time for this week's demo.

My time estimates were better than last week as I factored in the learning time.

In the future, I will try to define tasks which are clearly separate from my colleagues to reduce the chances of wasting my time on overlapping tasks.


## Ewan

I successfully refactored the forum to an MVVM model, and made it update the data dynamically. The Forum works.

Unfortunately, I had some issues with testing, Cloud Firestore only crashes when I run tests, even the most basic one (In which I do not call any Firestore queries). I spent a lot of time on this and still haven't managed to fix it. After asking Marcel for help, we concluded that I should replace it with the Mock Firebase I would have used for testing, so I would at least have something to show.
However, I then realized that my Mock Firebase does not work (the Observers do not get notified correctly). I spent 3 hours trying to make it work, unsuccessfully.
This means I have nothing I can merge into the main branch. As a proof of my work this week, I have made a blank PR.

My time estimate doesn't really apply, I think I estimated correctly (it would have been fine without all the problems), I just haven't found the correct way of fixing the tests yet. I spent 13 hours on SDP this week.

I will need more help with this next week, I am getting frustrated, I want to work on other things.


## Marcel

I worked on improving the sing in tests since I was using Firebase and this was making the tests sometimes fail on Cirrus. Moreover, I have implemented the database classes for chat groups and the view model for the chat group activity.

My estimate was underestimated for the sign in testing and refactoring, my estimate was of 2 hours and I went over 4 hours. This was again because I had to learn and find how to modularize and refactor those classes. Something I have to improve regarding the chat groups is that when creating the tasks for the sprint I didn't account for many previous task that had to be done (e.g. Database Service) before actually being able to properly implement the chat groups.

On the positive side, I believe I found a balance in with my first two PRs this week, which were of appropriate length to be happily revised by my peers. 


## Thomas

I worked on finishing the tests for the map activity. Then I implemented a new activity which appears when a point of interest is clicked. For now it has placeholders for the forum posts and for the chat, and allows to swipe left and right to switch between the two.

My time estimates were a bit better than last week because now I understand better how long testing can take. For my implementation, I had to read and understand code which was written by my peers, which was interesting.

This week, projects for other courses took a lot of my time, so I have to manage it better. I also had a lot of trouble with Cirrus because the tests kept failing with it, so I didn't manage to merge my branch. So I have to find a way to fix that.


## Yingxuan

This week I learned how to use MVVM model with firebase and I continued to work on the profile, however I got stuck longtime on the problem that I cannot log in on the emulator, even with the code of main branch. This made me unable to test my code until I figured out the problem. Then I managed to fix some bugs, namely several null pointers exceptions and undefined reference using directly ids, but we can still not see the profile interface appear. The positive side is I get more familiar with different tabs of logcat and the MVVM model.

I did not correctly estimate the time I need, I spent 9 hours on the project but did not achieve my goals. This is partially due to the unexpected problem of sign in, I thought some settings on firebase would be the solution at the beginning of the week, but I end up trying different things and lose my time. The emulator also takes time to run and lags on Mac, I constantly have the system UI isn't responding warning, it's a downside for efficiency.

Next week I should be more efficient and estimate the time more precisely.



## Overall team

We haven't managed to finish all of the user stories we have assigned this sprint. Therefore, we will have the same user stories since we believe these are the most important ones.

Even though our time estimates this week were a bit better, we are still learning to account for various sub-tasks such as reading the documentation, testing, merging, etc. One thing we should improve is having more meetings among team members when we are stuck. This will help us avoid wasting time on bugs/problems which we cannot solve on our own and make forward progress.

The standup meeting really helped this time especially since every person has gained some experience in an API, we usually found ourselves saying "hey I something like that before, maybe I can show you how to do it after the meeting" and we found that to be really beneficial.


