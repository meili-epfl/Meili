# Summary for sprint 7

## Ahmed

This week I finished the upvoting/downvoting feature of the forum. I had to use the transactions to assure the atomicity of upvotes/downvotes. I also ended up storing the list of all upvoters/downvoters in order to ensure no person can up/downvote twice.

Originally, I was supposed to finish this feature and the facebook-sign in. But unfortunately, I only had time to finish this feature since the testing was quite tricky and I had to work with an already existing code-base.

Even though my time estimates were off this week, having two goals for the sprint helped since at least I was able to finsih one of them. I still need to improve in that aspect though.


## Aman (Scrum Master)

This week I worked on the nearby friend feature which allows the user to become friends with a nearby user using Bluetooth. I overestimated the time for this feature by about 2 hours, mainly because I didn't know that the Google API abstracts away a lot of nitty-gritty of the implementation.

With my freed-up time, I helped Yingxuan with the profile feature, especially the mvvm architecture and the mocking. I also refactored the code to use the existing firebase services to avoid code duplication. I spent around 5 hours on this task. We were finally able to merge this feature into the main!

In the future, I plan to improve my time estimates by first skimming through the API I'm planning to use before estimating the time.

Another thing I would like to improve is to not get carried away in my feature PRs by fixing unrelated bugs. I will make separate PRs for the bug fixes so that it's easier for my teammates to review them. It will also be better for documentation/history of what code comes from where.


## Ewan 

This week I finished and tested my features for the photo editor. I ran into quite a few problems and got stuck, but I managed to fix everything. These features are rotating the image, cropping the image and adding emojis to it. I am proud in particular of having found how to test the rotationGestureDetector, which saved a lot of coverage issues I was afraid I wouldn't be able to test.

My time estimates this week were quite good except for the the time I spent being stuck on problems. I spent 14h of my time this week, I'm still very slow at writing code, but at least I finally managed to implement my tasks.


## Marcel 

I started this week by implementing the error handling when receiving a response from an API. I made a first version which was to keep calling the API every 5 minutes but after discussing with Aman we decided the best would be to make a couple of requests and if errors keep persisting then we don't expect that this error will be solved before closing the app and we stop requesting a response (for eg server might be down, or no internet connection). My second task was to refactor the cache service to make it general and usable bu more activities other than the POIService. Finally, I spent 1 hour more on the second cache than expected and since Aman had already finished his tasks for the week we decided that he was going to take my third task and I instead did an extra smaller task of adding the login activity popup on the profile activity if the user is not logged in.

My time estimates are getting much better, for the first and third tasks my estimates were accurate. And, for the second one I was just slightly off by 1 hour, I predicted 4 hours but spent 5 on the second task.

For the weeks to come I will be looking for finishing to improve my time estimations and improving my productivity in terms of useful code per sprint.


## Thomas

This week I finished the filters feature and I improved the photo editing UI. I also added the possibility to switch between the front and back camera in the camera activity.

Compared to last week, I was able to merge all the features that I has planned this week. I was less greedy and better in my time estimates.

Next week, I want to improve the speed at which I write code, because sometimes I go in wrong directions when trying to implement a feature, which makes me lose time. So I have to plan better and be better at sensing when the way that I am trying to implement a feature is not the right one.


## Yingxuan

This week I tried to debug the profile and to test it, but was still stuck. Fortunately Aman helped me with this feature and thanks to his work, it is merged into main. For the other task, I implemented a photo editing feature that allows to add text on photos, I also added test about it and updated the UI to make it coherent with other photo editing tools. I spent some extra time to fulfill the criteria of code climate in order to pass its check. 

I spent more time than estimated but with the effort of myself and my teammates we were able to deliver the tasks. This encourages me to continue and to improve my performance for the coming week.


## Overall team

This week, the team was able to make a big push to add more features to the main, both long-pending and new ones. We hope to continue this momentum in the future weeks!

We are getting better at helping each other when someone is stuck. However, this is an aspect we could even further improve by asking for help early, before losing a lot of time on something which another teammate might already have experience with. 

Furthermore, PR reviews are becoming more and more helpful as all teammates are getting experienced in the different frameworks/apis. This leads to constructive suggestions on PRs and assures a good quality of code on the main.
