# Summary for sprint 11

## Ahmed

This week I finshed writing the tests for the notifcations code. The test themsleves aren't long but I had to rewrite a lot of code and spend a lot of time to figure out how to make the code "testable". I also added the feature where the date of a chat message is only shown if it's the first message of any day. This feature was divided into two parts and there was no impediments since it was work on a pretty familiar part of the code.

I am very glad my time estimates this week were correct despite some difficulties, namely the internet being down several times. One reason for this is that I am generally getting better at estimating time. The other reason is that I am more familiar with the android framework so I have a better understanding of what to do and how much time it will usually take.

For the final sprint I am planning on keeping up with this pace and fixing more bugs!

## Aman

This week I improved the user interface of the point of interest activities to make more intuitive and easy to use. I also improved the app launching experience by displaying a logo at startup and redirecting the user to the correct activity (sign-in for a first-time user, map for a returning user).

Furthermore, I contributed to the PR fixing remarks from code review before approving it, which was time I hadn't added in my estimates. Since I didn't have too much additional time this week due to other projects, I decided to work on my last task of verifying/fixing image caching in the next sprint. With a little bit of remaining time I had, I fixed some bugs such as the app having two launcher activities and fix warnings like unused code or UI/UX suggestions (such as putting the cancel button on the left and the confirm button on the right).

## Ewan

This sprint I worked on improving the UI of the Profile, removing the default image in Posts and showing images (when they exist) directly in Post previews (forum, feed, my posts).
As I started my features a bit late, only 1 of my PRs was reviewed in time, but everything is done.

One problem I had is that I was one of the last PRs to get reviewed, so I had to update my branch very often to merge changes in main.

My time estimates were very good this week, and I am happy wih that.

## Marcel

This week I had 3 tasks to work on. The first was to restrict possibility to create posts and reviews to only users who have been in the point of interest before. I believe this is an important task since it was one of the features that makes Meili valuable, users will be able to trust posts and reviews because they must have been there before. The second task was also really exciting to implement, now we are displaying the POI being analyzed by MeiliLens in a different colour so that the users can have a UI reference in the map. It was hard to implement this task because of how the `ClusterManager` works in android and in my opinion the documentation on this aspect is not really good. Finally, my last task for the week was to fix a supposed bug in the cache service, but after digging deep we realized that everything is working properly and that it was just a false red flag.

I managed to implement all of my tasks in the estimated time, there was just the last task that took me less time since at the end I found that there was no problem. Since I had a little bit of time left because of my shorter than expected last task, I collaborated in the fixing code review remarks PR and also reviewed as many PRs as I could.

## Thomas (Scrum Master)

This week I worked on making a multitude of small improvements and bug fixes for the app.

I am happy that I managed to merge all of my tasks, except for one. The app now has a better feel, look, and is more coherent overall.

I was not able to merge my last task because once again I has problems with all of the tests passing locally, but not on cirrus. I didn't have enough time to find the root of the issue.


## Yingxuan

This week I had two tasks, one consists of night mode improvement, another is adding name of POI to posts. I'm happy to find a way to display different group of colors on dark mode for the menu items exactly as the team wanted. For the second task, I had a bug that did not send error message, and stuck for a moment, then after checking the firebase console I managed to fix it, and the poi name can show up when the post is viewed in my posts and feed, we decided not to show it in forum because it is already grouped by poi and will be redundant otherwise. This task is not marged yet because it modifies the database and we do not want it to disturb zhe demo.

## Overall team

This week, the team did a lot of polishing work so that Meili looks like a legit app which could be on the app store! This includes convenience features such as a dark mode, interface ameliorations, bug fixes, and user experience improvements to make navigation in the app flow more smoothly.

This week had a lot of pull requests, so team members collaborated a lot in reviewing each others' code. Many changes made this week were subjective, so team members were also helpful in giving their opinions so that we have an app that everyone is happy with.

