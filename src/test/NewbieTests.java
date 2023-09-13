package test;

import enity.EpicTask;
import enity.SubTask;
import enity.Task;
import enity.task.status.Status;
import enity.task.type.TaskType;
import management.TaskManager;

import java.util.LinkedList;
import java.util.Random;

public class NewbieTests extends TaskManager{

    private final Random random;
    private LinkedList<EpicTask> epicTaskList_checklist;
    private LinkedList<SubTask> subTaskList_checklist;
    private LinkedList<Task> taskList_checklist;

    public NewbieTests() {
        random = new Random();
    }

    public void runTests(){
        fillManager();
    }

    private void fillManager(){
        addEpicTasks();
        addSubTasks();
        addRegularTasks();

        equals_test();
        inProgressStatusEpicTaskTest();
        doneStatusEpicTaskTest();
        epicTaskRemoveTest();
        subTaskRemoveTest();
        removeSomeTasksTest();
        removeAllTest();
    }

    private void addEpicTasks() {
        epicTaskList_checklist = new LinkedList<>();

        for (int i = 1; i < 10; i++) {
            EpicTask epicTask = new EpicTask("Epic"+i, "desc_Epic"+i);
            int id = addTask(epicTask).getTaskId();

            EpicTask epicTask_check = new EpicTask("Epic"+i, "desc_Epic"+i);
            epicTask_check.setStatus(Status.NEW);
            epicTask_check.setTaskId(id);
            epicTaskList_checklist.add(epicTask_check);
        }
    }

    private void addSubTasks(){
        subTaskList_checklist = new LinkedList<>();
        for (EpicTask epic: epicTaskList_checklist) {
            int subTaskCount = random.nextInt(7)+3;

            for (int i = 1; i < subTaskCount; i++) {
                SubTask subTask = new SubTask("SubTask" + i, "desc_SubTask"+i, epic.getTaskId());
                int id = addTask(subTask).getTaskId();

                SubTask subTaskCheck = new SubTask("SubTask" + i, "desc_SubTask"+i, epic.getTaskId());
                subTaskCheck.setStatus(Status.NEW);
                subTaskCheck.setTaskId(id);
                epic.addSubTask(subTaskCheck);
                subTaskList_checklist.add(subTaskCheck);
            }
        }
    }

    private void addRegularTasks(){
        taskList_checklist = new LinkedList<>();

        for (int i = 0; i < 10; i++) {
            Task regular = new Task("regular"+i, "desc_regular"+i);
            int id = addTask(regular).getTaskId();

            Task regular_check = new Task("regular"+i, "desc_regular"+i);
            regular_check.setStatus(Status.NEW);
            regular_check.setTaskId(id);
            taskList_checklist.add(regular_check);
        }
    }

    private void equals_test() {
        epicTaskList_checklist.forEach(epicTask -> {
            EpicTask epicTask_map = (EpicTask) taskTypeMap.get(TaskType.EPIC).get(epicTask.getTaskId());
            EpicTask epicTask_check = epicTask;
            if(!epicTask_check.equals(epicTask_map)){
                System.out.println("!! equals_test fail: epicTaskList_checklist !!");
                System.out.println("manager obj: "+epicTask_map);
                System.out.println("own obj: "+epicTask_check);
                System.out.println("--------------------------------");
            };
        });

        taskList_checklist.forEach(task -> {
            Task task_map = taskTypeMap.get(TaskType.REGULAR).get(task.getTaskId());
            Task task_check = task;
            if(!task_check.equals(task_map)){
                System.out.println("!! equals_test fail: taskList_checklist !!");
                System.out.println("manager obj: "+task_map);
                System.out.println("own obj: "+task_check);
                System.out.println("--------------------------------");
            };
        });
    }

    private void inProgressStatusEpicTaskTest(){
        var t = taskTypeMap.get(TaskType.EPIC);
        t.forEach((integer, task) -> {

            SubTask sub =  ((EpicTask) task).getSubTaskList().peek();
            sub.setStatus(Status.IN_PROGRESS);
            updateTask(sub);
        });

        t.forEach((integer, task) -> {
            if( task.getStatus() != Status.IN_PROGRESS ){
                System.out.println("Epic status 'IN PROGRESS' comparison error");
            }
        });
    };

    private void doneStatusEpicTaskTest(){
        var epicMap = taskTypeMap.get(TaskType.EPIC);

        epicMap.forEach((integer, epic) -> {
            var sublist = ((EpicTask) epic).getSubTaskList();
            for (SubTask sub: sublist ) {
                sub.setStatus(Status.DONE);
                updateTask(sub);
            }
       });

        epicMap.forEach((integer, task) -> {
            if (task.getStatus() != Status.DONE ){
                System.out.println("Epic status 'done' comparison error");
            }
        });
    };

    private void epicTaskRemoveTest() {
        EpicTask epicTask = epicTaskList_checklist.get(random.nextInt(epicTaskList_checklist.size()));

        var sublist = epicTask.getSubTaskList();
        removeEpicTask(epicTask.getTaskId());
        epicTaskList_checklist.remove(epicTask);

        var subTaskMap = taskTypeMap.get(TaskType.SUB);
        for (SubTask sub: sublist ) {
            if (subTaskMap.containsKey(sub.getTaskId())){
                System.out.println("!! epicTaskRemoveTest fail: subtask present !!");
                System.out.println(sub.getTaskId());
                System.out.println("--------------------------------");
            }
            subTaskList_checklist.remove(sub);
        }
        if (taskTypeMap.get(TaskType.EPIC).containsKey(epicTask.getTaskId())){
            System.out.println("!! epicTaskRemoveTest fail: epic present !!");
            System.out.println(epicTask);
            System.out.println("--------------------------------");
        }
    }

    private void subTaskRemoveTest() {
        for (int i = 0; i < 10; i++) {
            SubTask subTask = subTaskList_checklist.get(random.nextInt(subTaskList_checklist.size()));
            removeSubtask(subTask.getTaskId());

            EpicTask epicTask = (EpicTask) taskTypeMap.get(TaskType.EPIC).get(subTask.getHostTaskID());

            if (epicTask.getSubTaskList().contains(subTask)){
                System.out.println("!! subTaskRemoveTest fail: subtask contains in epic!!");
                System.out.println(epicTask);
                System.out.println("--------------------------------");
            }
        }
    }

    private void removeSomeTasksTest() {
        for (int i = 0; i < 5; i++) {
            Task task = taskList_checklist.get(random.nextInt(taskList_checklist.size()));
            removeRegularTask(task.getTaskId());
            taskList_checklist.remove(task);

            if (taskTypeMap.get(TaskType.REGULAR).containsKey(task.getTaskId())){
                System.out.println("!! removeSomeTasksTest fail: task present !!");
                System.out.println(task);
                System.out.println("--------------------------------");
            }
        }

    }

    private void removeAllTest() {
        taskTypeMap.get(TaskType.SUB).clear();
        if (taskTypeMap.get(TaskType.SUB).size() > 0){
            System.out.println("!! removeAllTest fail !!");
            System.out.println("--------------------------------");
        }

        taskTypeMap.get(TaskType.REGULAR).clear();
        if (taskTypeMap.get(TaskType.REGULAR).size() > 0){
            System.out.println("!! removeAllTest fail !!");
            System.out.println("--------------------------------");
        }

        taskTypeMap.get(TaskType.EPIC).clear();
        if (taskTypeMap.get(TaskType.EPIC).size() > 0){
            System.out.println("!! removeAllTest fail !!");
            System.out.println("--------------------------------");
        }
    }


}
