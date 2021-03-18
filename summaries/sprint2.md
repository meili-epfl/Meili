# Summary for sprint 2

## Ahmed (Scrum master)

##TODO


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

##TODO


## Yingxuan

##TODO


## Overall team

##TODO
