package backend.academy;

public record LogsFilterArgs(String startDate, String endDate, String filterField, String filterValue) {
    public LogsFilterArgs(String startDate, String endDate) {
        this(startDate, endDate, null, null);
    }
}
