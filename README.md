Before you run the app you need to past you API Key from NewsApi.org in the build.gradle Module file under defaultConfig
you will see "PLACE_THE_API_KEY_HERE" don't remove the \", just place it between them.

App includes:
Listing articles per categories from USA (Top headlines, Business, Entertainment, Spots)
Each page of articles contains 20 articles, as you scroll down 20 more articles load
Articles are retrieved from the newsapi.org and stored in a local Room database, the reason for this is so you are able to use the app offline (also for searched articles).
Images are cached and managed by the Coil Library
Clicking on an Article you can view the article in article view or if the article doesn't have any content it open the website of the article.
In the article view you can share the link of the article on Facebook, Twitter and WhatsApp
When you click on Read More it opens the article website page in WebView inside the app, you are not leaving it.

App supports search feature from newsapi.org, it searches for all articles which are marked for English language.
You can also decide to save the search clicking on the bookmark icon, the app checks every 15 minutes if there are new articles, and if there are it sends you a notification.
The check is being done even when the app is closed.
Single clicking on the saved search fills the search box, double clicking it allows you to delete it.
In the Settings page you can decide if you want to receive notifications or not.

There are two options for notification system one which starts and runs always even if there are no saved searches,
and one where it detects when a keyword is added and removed, and decides whether there are keywords to start
the notification job.

Daily quotes feature is a simple cpp based feature which shows a random quote.

App has support for Light and Dark theme, and some support for the new Android 12 Dynamic colors.

Icons are from FlatIcon.com and FontAwesome.

[![Android CI](https://github.com/isrna/NewsAndroidApp/actions/workflows/android.yml/badge.svg)](https://github.com/isrna/NewsAndroidApp/actions/workflows/android.yml)

Images:
![Light Theme](https://raw.githubusercontent.com/isrna/NewsAndroidApp/main/screenshots/app_light.png)
![Dark Theme](https://raw.githubusercontent.com/isrna/NewsAndroidApp/main/screenshots/app_dark.png)
![Entertainment Tab](https://raw.githubusercontent.com/isrna/NewsAndroidApp/main/screenshots/entertainment.png)
![Article Website](https://raw.githubusercontent.com/isrna/NewsAndroidApp/main/screenshots/article_website.png)
![Settings](https://raw.githubusercontent.com/isrna/NewsAndroidApp/main/screenshots/settings.png)
![Notification for new articles based on saved search keywords](https://raw.githubusercontent.com/isrna/NewsAndroidApp/main/screenshots/notification_new_articles.png)
