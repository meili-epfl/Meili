# Summary for sprint 3

## Ahmed

I worked solely on tests this week. I have worked on creating tests for the forum branch using mocks to have the maximum possible code coverage. I have also worked on increasing the coverage of the already found tests in the chat branch. I also introduced some bug fixes to some tests in the codebase.

After two weeks, my estimates are finally accurate. I am starting to have a sense of how much a test for a certain code would take.

Similarly to me learning advanced vcs features, I feel like making tests and mocking services is something that would take up most of the time in one week but would be faster to do in later weeks since I would have learned the basics on how to do these things.


## Aman 

I worked on the Point of Interest review system (with Firebase) this week and finished it's implementation. This allows the user to add reviews (or edit their review if they already added one), see others' reviews and the average rating for a POI. However, I haven't been able to implement tests for this feature, which is why it couldn't be merged.

My time estimates were very underestimated. I had estimated 8 hours to finish this feature. However, I spent 16 hours on it this week just to finish the implementation and another 2 hours or so trying to learn about mocking and related concepts like argument capturing.

The reason behind my understimation was the fact that I didn't know the complexity of learning about and implementing a MVVM architecture and Firebase. I will continue improving my estimations by attributing more time to learning about new frameworks/apis/architectures and testing in the future.


## Ewan

This week I worked on the beginning of the camera feature, and continued the tests for the forum. 

I followed a tutorial to use Camera X and implemented it into the project (it's just a start). I still need to learn more about how it works to continue on this feature. Thomas and I decided to not work on the feature after this, because we had other stuff to do.

After having talked with Aman, we had an idea to fix one of the problems we had where we couldn't mock Firestore correctly (it crashed the tests).
I then spent a long time implementing it, it's a bit janky for now (only the tests are impacted), but it works. Ahmed and I also wrote tests to drive the coverage for the forum up to 79% in Jacoco (which I don't think we can improve just now). We didn't manage to merge into main this week because of technicalities, but it should be done by next sprint.

My time estimations were wrong, I shouldn't have taken the camera task. although it was a nice breather after 2 weeks on the forum. 


## Marcel (Scrum Master)

I worked on finishing the chat, we use to have a one-to-one chat and now we have a group chat linked to a specific point of interest. I also worked on improving the UI for the chats, and now it feels closer to a real commercial messaging app. Finally, I also collaborated with Ahmed on improving the previous tests that were failing on CirrusCI.

This week I managed to finish all of the tasks that were assigned to me at the beginning of the sprint and also took an extra task (fixing the tests that were sometimes failing on CirrusCI). 

My time estimates were still a little bit off by underestimating but I am getting closer. On the bad side, I wanted to help Ewan to finish the tests for the forum but didn't manage to find time for that due to the workload in other courses. I hope to be able to help more in the next sprints.


## Thomas

This week I investigated implementing a sticker/filter feature to make the photograph sharing feature more interesting. However I realized that it would take a lot of time to implement (2~3 weeks) and so that the benefit/effort ratio of it wasn't high enough. So I decided to leave it for now and to instead focus on implementing an information panel for points of interest.

I am getting a bit better at estimating how much time tasks take, so I was able to rapidly switch gears and not waste my time once I saw that there were other higher priority tasks.

To test the POI info panel I have been using mockito for the first time, and it has been tricky figuring out how to structure the code to make it work. Because of that and high workload in other courses, I haven't been able to merge this week's work yet.

## Yingxuan

I worked on the user profile this week and it finally works when I no longer use fragment. I also updated the UI of the profile to make it coherent with the service it provides: modify and save different user data. I reorganized my code in order to be consistent with main and rebased. There were much more conflicts than expected that I had to solve manually, maybe because that I did not rebase since the creation of the branch. I should do this update more often in the future. It's good to make use of draft pull request to obtain advices of others, Thomas' comments were very useful and helped me to debug. I learned the basic of Mockito and testing with Espresso in more details, which allows me to write several tests.

I had to work for other subjects due to the upcoming midterm so I worked 8 hours on the project without being able to go further with the testing, I should contribute more next week.





## Overall team

This week, the team has worked hard and collaborated to improve testing. We can say that we have learnt and we are starting to use Mockito extensively. In the following weeks we will keep bringing it to the rest of the codebase. 

A positive aspect of this week has been the collaboration between the members to give a hand and ease the finishing of some tasks that were getting harder to finish. 

Time estimates are still off when we have to adventure ourselves into technologies that are new to us. However, for tasks that we are more familiar with it seems that we are starting to make more accurate time predictions. 

Finally, standups meetings continue to be really usefull since it allows knowledge transfering between team members and hence easing the learning curve of our peers.
