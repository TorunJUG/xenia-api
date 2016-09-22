package pl.jug.torun.xenia.meetup

/**
 * Helper trait for test classes that uses {@link MeetupClient}.
 */
trait MeetupClientAware {

    static final String RANDOM_MEETUP_KEY = UUID.randomUUID().toString()
    static final String MEETUP_GROUP_NAME = "Torun-JUG"

    final MeetupRestTemplate restTemplate = new MeetupRestTemplate(RANDOM_MEETUP_KEY, MEETUP_GROUP_NAME)

    final MeetupClient meetupClient = new MeetupClient(restTemplate)
}