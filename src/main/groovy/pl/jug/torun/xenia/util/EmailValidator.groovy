package pl.jug.torun.xenia.util

final class EmailValidator {

    public static boolean isValid(final String email) {
        return email ==~ /^[a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))/
    }
}
