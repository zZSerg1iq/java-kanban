package enity.task.status;

public enum Status {
    NEW("задача только создана, но к её выполнению ещё не приступили"),
    IN_PROGRESS("над задачей ведётся работа"),
    DONE("задача выполнена");

    private String description;

    Status(String description) {
        this.description = description;
    }
}
