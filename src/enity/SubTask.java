package enity;

public class SubTask extends Task{

    private int hostTaskID;

    public SubTask(String taskName, String taskDescription, int hostTaskID) {
        super(taskName, taskDescription);

        this.hostTaskID = hostTaskID;
    }

    public int getHostTaskID() {
        return hostTaskID;
    }

    public void setHostTaskID(int hostTaskID) {
        this.hostTaskID = hostTaskID;
    }
}
