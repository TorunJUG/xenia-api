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

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        EventResponse that = (EventResponse) o

        if (id != that.id) return false
        if (endDate != that.endDate) return false
        if (startDate != that.startDate) return false
        if (title != that.title) return false

        return true
    }

    int hashCode() {
        int result
        result = (int) (id ^ (id >>> 32))
        result = 31 * result + (title != null ? title.hashCode() : 0)
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0)
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0)
        return result
    }
}
