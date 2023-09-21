
import com.yandex.kanban.manager.Manager;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskStatus;
import com.yandex.kanban.model.EpicTask;
import com.yandex.kanban.model.Subtask;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        Task task1 = new Task(
                "task1",
                "description",
                TaskStatus.NEW
        );

        Task task2 = new Task(
                "Task2",
                "description",
                TaskStatus.IN_PROGRESS
        );

        Task task3 = new Task(
                "Task3",
                "description",
                TaskStatus.DONE
        );

        EpicTask epicTask1 = new EpicTask(
                "epicTask1",
                "description"
        );

        EpicTask epicTask2 = new EpicTask(
                "epicTask2",
                "description"
        );

        Subtask subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask2.getId()
        );

        Subtask subtask2 = new Subtask(
                "subtask2",
                "description",
                TaskStatus.IN_PROGRESS,
                epicTask2.getId()
        );
        //create
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);
        manager.createTask(subtask1);
        manager.createTask(subtask2);

        //getAllTask
        System.out.println(manager.getAllTasks());

        //get epicTask - testing status
        System.out.println(manager.getTask(epicTask1.getId()));
        System.out.println(manager.getTask(epicTask2.getId()));

        //testing status epickTtask at delete subtask
        //manager.removeTask(subtask2.getId());
        //System.out.println(manager.getTask(epicTask2.getId()));

        //testing getAllSubtask to epicTask
        //System.out.println(manager.getSubtasksToEpicTask(epicTask2.getId()));

        //testing remove epictask and all subtasks
        manager.removeTask(epicTask2.getId());
        System.out.println(manager.getAllTasks());
    }
}
