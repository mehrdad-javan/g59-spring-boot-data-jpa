package se.lexicon.g59springbootdatajpa.entity;

public enum EventStatus {

    PLANNED,
    ONGOING,
    CANCELLED,
    COMPLETED;


    public static EventStatus fromString(String status) {
        if (status == null || status.trim().isEmpty()) {
            return PLANNED;
        }
        try {
            return EventStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status + ". Valid statuses are: PLANNED, ONGOING, CANCELLED, COMPLETED");
        }
    }
}
