# Summary for sprint 10

## Ahmed

this week I worked on implementing a notification service (only when a user receives a direct message for the moment). I used the Firebase Console Messaging to send out the notifications. No existing api call was there to send notifications it was only available from the web's firebase console. I therefore implemented a service to send out these requests to the web. I then used the FirebaseMessagingService to customize the recevied notifications. For instance, the title of the notification is customized to be "from <name of the user>" and the body of the notifications is the message itself; moreover when you click on the notification you go to the chat.
 
 This feature took more time then expected since it was ridded with new challenges and I felt like the notification service wasn't really well documented so I had to resort to a lot of external sources like stack overflow. The testing of this feature has been as hard since most of the added code is just overrided private functions so I had to get creative to run these codes while also running mocked services and not the real ones...

TODO

## Aman

I implemented a feed where the user can see posts from points of interests around them. I started out by upgrading our firstore databse service to support queries and adapted its usage accordingly. For the feed itself, I used a couple of our previously-built services like the poi fetcher and the forum activity. This led me to refactor a lot of code to avoid duplication, improve the maintainibility of the codebase and allow for even easier additions in the future. For this user story, I had a perfect time estimate overall even though a couple of my tasks took one hour more and less, compensating each other.

It was a lot of fun working on this feature and it's great that we can now implement such complex-looking features with ease thanks to the general services we implemented in earlier sprints.


## Ewan (Scrum Master)
 
This week, I was very busy with other projects and I had to wait a little for other PRs to be done, as they implemented tools I wanted to use in my "My Posts" feature.

I managed to finish the feature I was assigned to in little time (about 5h), but as mentioned above, I didn't have time to do any extra work which would have made me do 8h. No particular problems were encountered.

I also helped with the dark/light mode UI, which only took me 30 min.
 
While preparing for the demo, I noticed a few bugs that I put in the backlog. As I am not able to be on campus, my little sister used the app to help me mwith the demo.
 
For the first time, I vastly overestimated the amount of work it would take me (5h instead of 8h). This is because the team implemented tools that facilitated the implementation of my feature. I will also make sure I work for more than 8h next sprint to compensate the hours lost this week.


## Marcel 

During this sprint, I implemented two big features. The first was to improve the point of interest information screen, and add it to the application flow. This meant that I had to link it from the map activity to information screen, and this information screen to the chats, forum and reviews. Moreover, I implemented a couple of small features inside this screen as the possibility to call the poi, give you directions to the poi (by directing you to google maps) and show you a quick preview of the website (three features that I find really cool).  I am happy since this task took me as expected 4 hours of work.
The second task I had to do was to change in the whole application, to have the possibilitiy to access the author profile. So for example, in reviews, we wanted to display the image of the author and name (which were not yet available in the activity) and then add the possibility of redirecting you to their profile. I estimated 2 hours of workload, but now I see that it was really underestimated. It took me over 5 hours to finish this feature. One of the causes where that some PRs with big modifications of the files I was working on where merged while I was working on the feature, and then it took me a long time to merge my part with the changes.

Despite this last task taking me more than expected, I am really happy with the work I've done this week since it is finishing to polish the app and making it look like a real app that you could find in google play store.

## Thomas

TODO

## Yingxuan

TODO

## Overall team

This sprint, the team implemented features that drastically improve the User experience, such as being able to change between light and dark mode in app, being able to click on other user's profiles to see their information, and notifications.
 
We are getting very good at refactoring and making more and more general tools as the project gets bigger. This is very helpful and drastically reduces the amount of time features take to implement.
 
We also have our first official user, Ewan's little sister, who used the app as a normal user to help him with this week's demo.
