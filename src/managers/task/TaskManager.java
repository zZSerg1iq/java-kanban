package managers.task;

import enity.EpicTask;
import enity.SubTask;
import enity.Task;

import java.util.List;


public interface TaskManager {

    List<EpicTask> getEpicTaskList();
    List<Task> getTaskList();
    List<SubTask> getSubtaskList();

    void removeAllTasks();
    void removeAllEpics();
    void removeAllSubtasks();

    Task addTask(Task task);
    Task getTask(int taskId);
    Task updateTask(Task task);
    Task removeTask(int taskId);

    EpicTask addEpicTask(EpicTask task);
    EpicTask getEpicTask(int taskId);
    EpicTask updateEpic(EpicTask newEpic);
    List<SubTask> getEpicSubTaskList(int taskId);
    EpicTask removeEpic(int taskId);

    SubTask addSubTask(SubTask task);
    SubTask getSubTask(int taskId);
    SubTask updateSubTask(SubTask task);
    SubTask removeSubTask(int taskId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

}
