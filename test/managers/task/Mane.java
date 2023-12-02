package managers.task;

import enity.EpicTask;
import enity.SubTask;
import managers.Managers;
import managers.task.TaskManager;
import java.time.LocalDateTime;


public class Mane {


    /**
     * эксепшен был из за того, что я забыл сделать update метод для списка даты-времени задач
     * и вызывался метод добавления, что неправильно
     * Даже не знаю, как я это упустил :\  Дело не в "поторопился". Я просто.. забыл о_О
     *
     * апдэйт времени теперь сделать можно, но только если нет пересечений с другими задачами
     */
    public static void main(String[] args) {
        TaskManager tm = Managers.getDefault();
        EpicTask epic = new EpicTask("1","11");

        tm.addEpicTask(epic);
        System.out.println(epic);

        LocalDateTime dt1 = LocalDateTime.now();

        SubTask sub1 = new SubTask("s1","s111", dt1,60, epic.getTaskId());
        System.out.println(sub1);
        tm.addSubTask(sub1);


        SubTask sub2 = new SubTask("s2","s222", dt1.plusMinutes(120),90, epic.getTaskId());
        System.out.println(sub2);

        tm.addSubTask(sub2);
        System.out.println(epic);

//        sub2.setDuration(93);
        sub2.setTaskName("s2_1");
        tm.updateSubTask(sub2);
        System.out.println(epic);
    }
}