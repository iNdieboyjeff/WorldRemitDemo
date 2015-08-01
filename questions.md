Design Decisions
================

As this was purely a demonstration app, it has not been fully secured.  While _ProGuard_ has been used for obfuscation, for a production app of this nature I would also make use of _DexGuard_ to
provide better encryption and tamper detection.  SSL pinning has been included within the app to demonstrate how this would assist in preventing MITM attacks and request sniffing using tools like
Charles Proxy.  For a production app I would normally load API urls from an encrypted file loaded at app startup, however for this case I've simply hardcoded them which is not ideal.  Test
coverage is not totally complete, but provides a representative sample.  A very basic translation to Turkish (without special characters) has been provided as an example.


Design Patterns
===============
* MVC
* Dependency Injection
* Service Layers
* Factory
* Singleton
* Builder
* Adapter
* ViewHolder

Development Resources
=====================

I would always reccomend someone check out the official [Android developer website](http://developer.android.com/) and if necessary the same for [Amazon](http://developer.amazon.com/).
[StackOverflow](http://stackoverflow.com/) is always a good resource, as most problems developers encounter have been encountered (and hopefully solved) by others.  It is very rare you can't find
the answer to a question there. [GitHub](https://github.com/) is always a useful site, with plenty of helpful libraries and code samples.


Keeping Up-To-Date
==================

I tend to keep up-to-date by following the official announcements from Google on the developer site, Twitter and Google+.  There are also a range of blogs or other developers, who I generally follow
on Google+ to get the latest news and views.  I would also tend to check the Technology section of the Guardian, even if you have to allow for delayed reposting of events, and a noticeable
pro-Apple/anti-Android bias.


Myself
======

`{
	"name": "Jeff Sutton",
	"gender":"Male",
	"dob":"1979-06-19T11:00:00 +0100",
	"languages":["en"],
	"interests":["programming","music","politics","reading","history"],
	"apps":[
		{"name":"TVPlayer", "link":"https://play.google.com/store/apps/details?id=com.tvplayer"},
		{"name":"The Box Plus", "link":"https://play.google.com/store/apps/details?id=uk.co.thebox"},
		{"name":"Horse & Country", "link":"https://play.google.com/store/apps/details?id=tv.horseandcountry"},
		{"name":"Watch Food Network UK", "link":"https://play.google.com/store/apps/details?id=uk.co.foodnetwork"},
		{"name":"BoxNation", "link":"https://play.google.com/store/apps/details?id=com.simplestream.bn"},
		{"name":"Ideal World", "link":"https://play.google.com/store/apps/details?id=com.simplestream.android.iw"},
		{"name":"Ideal-World", "link":"https://play.google.com/store/apps/details?id=com.isd.idealworld"},
		{"name":"Pocket Writers", "link":"https://play.google.com/store/apps/details?id=com.peapple.writers"}
	]
}`


Libraries
=========
* __Ion__ - Simplifies all forms of http request, image loading etc.  An essential library for most projects.
* __Gson (and Gson-XML)__ - If you use ReST services, Gson makes converting from JSON and POJO quick and simple.  Gson-XML is a 3rd party extension usin Gson for reading XML data.
* __Crashlytics__ - Realtime crash reports, an essential tool for all apps.
* __AndroidUtilities__ - My own library of useful functions and utilities, including Date utilities, MD5 hashing, User-Agent strings, etc.
* __Android Support Libraries__ - Almost impossible not to use these, making newer Android functionality available on older devices.



Tools
=====
1. Android Studio(!)
2. An Android Phone/Tablet/TV Box (for testing)
3. ProGuard/DexGuard
4. Charles Proxy
5. Photoshop


