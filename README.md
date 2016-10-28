Xenia-Api [![Build Status](https://travis-ci.org/TorunJUG/xenia-api.svg?branch=develop)](https://travis-ci.org/TorunJUG/xenia-api) [![Coverage Status](https://coveralls.io/repos/github/TorunJUG/xenia-api/badge.svg?branch=develop)](https://coveralls.io/github/TorunJUG/xenia-api?branch=develop)
=

Backend application for Xenia - giveaway drawing machine that works in connection with your Meetup group. Create prizes, add giveaways to your events and draw winners in just a few easy steps!

```
mvn spring-boot:run -DMEETUP_KEY={your-meetup-api-key} -DMEETUP_GROUP_URL_NAME={your-meetup-group-url-name}
```


How to migrate from 1.0 to 2.0?
==

**ATTENTION:** Before you start any migration, please backup `xeniadb.mv.db` file. You might need it if something goes wrong. You have been warned :)

1\. Synchronize tags with remote repository
```
git fetch --tags
```
2\. Switch to tag `1.0.0`
```
git checkout tags/1.0.0
```
3\. Run the application in the command line:
```
mvn spring-boot:run
```
or in your favourite IDE.

4\. When the application is running go to [http://localhost:8080/events/export](http://localhost:8080/events/export) and download `xenia-1.0.dump.json` file and save it on your hard drive.

5\. Kill the application and switch to tag `2.0.0`
```
git checkout tags/2.0.0
```
6\. Remove any `xeniadb.*` files from application root directory - importer runs only if the database is clean and contains no events etc.

7\. Copy `xenia-1.0.dump.json` file to `src/main/resources/recover/xenia-1.0.dump.json` and run the application.

8\. Recovery service will import all events, members, prizes, giveaways and draw results to your database.

9\. Enjoy Xenia 2.0! :)

