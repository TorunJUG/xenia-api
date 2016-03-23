Xenia-Api [![Build Status](https://travis-ci.org/TorunJUG/xenia-api.svg?branch=master)](https://travis-ci.org/TorunJUG/xenia-api)
=========

Api for Xenia - JUG drawing machine based on Meetup (EventBrite coming soon!) which allows you to draw prizes on events and to keep track of them. It guarantees fair drawing - no participant will win two times an event and any prize more than once. You should have java and maven installed to run:

 mvn spring-boot:run

 Designed to work with Xenia-ng. To work, you need to provide an api key into application.properties. Additionally, iif you want messaging, token also needs to be provided.

To connect to you meetup, run with `-DMEETUP_KEY={your-meetup-key}` and `-DMEETUP_NAME={your-meetup-group-name}` to provide secret api keys in runtime
