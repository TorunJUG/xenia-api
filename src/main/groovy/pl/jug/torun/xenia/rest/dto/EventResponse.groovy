package pl.jug.torun.xenia.rest.dto

import pl.jug.torun.xenia.model.Event

/**
 * Created by mephi_000 on 06.09.14.
 */
class EventResponse {
    long id
    String title
    String startDate
    String endDate

    EventResponse(Event event) {
        this.id = event.id
        this.title = event.title
        this.startDate = event.startDate.toString()
        this.endDate = event.endDate.toString()
    }

    @Override
    public String toString() {
        return "EventResponse{id=$id, title='$title', startDate='$startDate', endDate='$endDate'}";
    }

    @Override
    boolean equals(Object obj) {

        if(obj == null) {
            return false
        }

        if (obj.is(this)) {
            return true
        }

        if (!(obj instanceof EventResponse)) {
            return false
        }

        EventResponse other = (EventResponse) obj

        return Objects.equals(id, other.id) &&
            Objects.equals(endDate, other.endDate) &&
            Objects.equals(startDate, other.startDate) &&
            Objects.equals(title, other.title)
    }

    @Override
    int hashCode() {
        return Objects.hash(id, title, startDate, endDate)
    }
}
