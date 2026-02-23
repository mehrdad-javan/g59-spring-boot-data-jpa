package se.lexicon.g59springbootdatajpa.entity;

public enum EventStatus {

    PLANNED,
    ONGOING,
    CANCELLED,
    COMPLETED;


    public static EventStatus fromString(String status) {
        if (status == null || status.isEmpty()) {
            return PLANNED;
        } else {
            throw new IllegalArgumentException("Invalid status: " + status + ". Valid statuses are: PLANNED, ONGOING, CANCELLED, COMPLETED");
        }
    }
}
