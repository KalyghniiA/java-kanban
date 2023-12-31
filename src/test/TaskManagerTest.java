package test;

import com.yandex.kanban.exception.DatabaseException;
import com.yandex.kanban.exception.KVClientException;
import com.yandex.kanban.exception.TaskException;
import com.yandex.kanban.managers.taskManager.TaskManager;
import com.yandex.kanban.model.EpicTask;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest<M extends TaskManager> {
   protected M manager;

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
    Subtask subtask1;
    Subtask subtask2;
    Task task4;
    Task task5;
    Task task6;
    Subtask subtask3;
    Subtask subtask4;

    @BeforeEach
    void createManager() throws IOException, KVClientException {
        try {
            task4 = new Task(
                    "name",
                    "description",
                    TaskStatus.NEW,
                    "10:00 01.01.2023",
                    "20"
            );
            task5 = new Task(
                    "name",
                    "description",
                    TaskStatus.NEW,
                    "10:21 01.01.2023",
                    "20"
            );

            task6 = new Task(
                    "name",
                    "description",
                    TaskStatus.NEW,
                    "10:40 01.01.2023",
                    "20"
            );
        } catch (TaskException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void createTask() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        List<Task> testingTasks = List.of(task1, task2, task3);

        assertAll(
                () -> assertTrue(manager.getAllTasks().containsAll(testingTasks)),
                () -> assertEquals(3, manager.getAllTasks().size()),
                () -> assertFalse(manager.getAllTasks().isEmpty())
        );
    }

    @Test
    void createTasks() throws DatabaseException, KVClientException, TaskException {
        List<Task> tasks = List.of(task1, task2, task3);

        List<Task> testingTasks = List.of(task1, task2, task3);

        manager.createTasks(tasks);

        assertAll(
                () -> assertTrue(manager.getAllTasks().containsAll(testingTasks)),
                () -> assertEquals(3, manager.getAllTasks().size()),
                () -> assertFalse(manager.getAllTasks().isEmpty())
        );
    }

    @Test
    void createSubtaskNotEpic() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(task1);
        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask2.getId()
        );
        final DatabaseException e = assertThrows(DatabaseException.class, () -> manager.createTask(subtask1));

        List<Task> testingList = List.of(task1,subtask1);

        assertAll(
                () -> assertFalse(manager.getAllTasks().containsAll(testingList)),
                () -> assertEquals(List.of(task1), manager.getAllTasks()),
                () -> assertEquals(e.getMessage(), "Для данной подзадачи нет главной задачи")
        );
    }

    @Test
    void createTasksRepeatTask() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(task1);
        final DatabaseException exception = assertThrows(DatabaseException.class, () -> manager.createTask(task1));
        manager.createTask(task2);
        manager.createTask(task3);


        List<Task> testingTasks = List.of(task1, task2, task3);

        assertAll(
                () -> assertTrue(manager.getAllTasks().containsAll(testingTasks)),
                () -> assertEquals(3, manager.getAllTasks().size()),
                () -> assertFalse(manager.getAllTasks().isEmpty()),
                () -> assertEquals("Данная задача уже существует", exception.getMessage())
        );
    }

    @Test
    void createEpicTaskEmptySubtasks() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(epicTask1);

        Task task = manager.getTask(epicTask1.getId());

        assertAll(
                () -> assertTrue(((EpicTask)task).getSubtasksId().isEmpty()),
                () -> assertEquals(task.getStatus(), TaskStatus.NEW)
        );
    }

    @Test
    void checkEpicStatusToInProgress() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(epicTask2);

        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask2.getId()
        );
        subtask2  = new Subtask(
                "subtask2",
                "description",
                TaskStatus.IN_PROGRESS,
                epicTask2.getId()
        );

        manager.createTask(subtask1);
        manager.createTask(subtask2);

        Task task = manager.getTask(epicTask2.getId());

        assertAll(
                () -> assertFalse(((EpicTask)task).getSubtasksId().isEmpty()),
                () -> assertEquals(task.getStatus(), TaskStatus.IN_PROGRESS)
        );
    }

    @Test
    void checkEpicStatusToInProgress2() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(epicTask2);
        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask2.getId()
        );
        subtask2  = new Subtask(
                "subtask2",
                "description",
                TaskStatus.DONE,
                epicTask2.getId()
        );

        manager.createTask(subtask1);
        manager.createTask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getTask(epicTask2.getId()).getStatus());
    }

    @Test
    void checkEpicStatusToInProgress3() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(epicTask2);
        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.IN_PROGRESS,
                epicTask2.getId()
        );
        subtask2  = new Subtask(
                "subtask2",
                "description",
                TaskStatus.DONE,
                epicTask2.getId()
        );

        manager.createTask(subtask1);
        manager.createTask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getTask(epicTask2.getId()).getStatus());
    }

    @Test
    void checkEpicStatusToDone() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(epicTask2);
        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.DONE,
                epicTask2.getId()
        );
        subtask2  = new Subtask(
                "subtask2",
                "description",
                TaskStatus.DONE,
                epicTask2.getId()
        );
        manager.createTask(subtask1);
        manager.createTask(subtask2);

        assertEquals(TaskStatus.DONE, manager.getTask(epicTask2.getId()).getStatus());
    }

    @Test
    void checkEpicStatusToDeleteSubtask() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(epicTask2);
        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.IN_PROGRESS,
                epicTask2.getId()
        );
        subtask2  = new Subtask(
                "subtask2",
                "description",
                TaskStatus.DONE,
                epicTask2.getId()
        );

        manager.createTask(subtask1);
        manager.createTask(subtask2);

        manager.removeTask(subtask1.getId());
        manager.removeTask(subtask2.getId());

        EpicTask task = (EpicTask) manager.getTask(epicTask2.getId());

        assertAll(
                () -> assertTrue(task.getSubtasksId().isEmpty()),
                () -> assertEquals(TaskStatus.NEW, task.getStatus())
        );
    }

    @Test
    void emptyManager() {
        assertAll(
                () -> assertTrue(manager.getAllTasks().isEmpty()),
                () -> assertNotNull(manager.getAllTasks())
        );
    }

    @Test
    void getTask() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        assertAll(
                () -> assertEquals(manager.getTask(task1.getId()), task1),
                () -> assertEquals(manager.getTask(task2.getId()), task2),
                () -> assertNotNull(manager.getTask(task3.getId()))
        );
    }

    @Test
    void getNormalTasks() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        List<Task> testingList = List.of(task1, task2, task3);

        assertAll(
                () -> assertTrue(manager.getAllNormalTask().containsAll(testingList)),
                () -> assertEquals(3, manager.getAllNormalTask().size()),
                () -> assertNotNull(manager.getAllNormalTask())
        );
    }

    @Test
    void getNormalTasksIsEmptyList() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        assertAll(
                () -> assertTrue(manager.getAllNormalTask().isEmpty()),
                () -> assertNotNull(manager.getAllNormalTask())
        );
    }

    @Test
    void getEpicTasks() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        List<Task> testingList = List.of(epicTask1, epicTask2);

        assertAll(
                () -> assertTrue(manager.getAllEpicTask().containsAll(testingList)),
                () -> assertEquals(2, manager.getAllEpicTask().size()),
                () -> assertNotNull(manager.getAllEpicTask())
        );
    }

    @Test
    void getEpicTasksIsEmptyList() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        assertAll(
                () -> assertTrue(manager.getAllEpicTask().isEmpty()),
                () -> assertNotNull(manager.getAllEpicTask())
        );
    }

    @Test
    void getSubtasks() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        //Очередная проблема с созданием id
        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask2.getId()
        );
        subtask2  = new Subtask(
                "subtask2",
                "description",
                TaskStatus.IN_PROGRESS,
                epicTask2.getId()
        );

        manager.createTask(subtask1);
        manager.createTask(subtask2);

        List<Task> testingList = List.of(subtask1, subtask2);

        assertAll(
                () -> assertTrue(manager.getAllSubtask().containsAll(testingList)),
                () -> assertEquals(2, manager.getAllSubtask().size()),
                () -> assertNotNull(manager.getAllSubtask())
        );
    }

    @Test
    void getSubtasksIsEmptyList() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(task1);

        assertAll(
                () -> assertTrue(manager.getAllSubtask().isEmpty()),
                () -> assertNotNull(manager.getAllSubtask())
        );
    }

    @Test
    void removeTask() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.removeTask(task1.getId());

        List<Task> testingList = List.of(task2, task3);

        assertAll(
                () -> assertTrue(manager.getAllTasks().containsAll(testingList)),
                () -> assertEquals(2, manager.getAllTasks().size())
        );
    }

    @Test
    void removeSubtask() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(epicTask2);

        //Очередная проблема с созданием id
        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask2.getId()
        );
        subtask2  = new Subtask(
                "subtask2",
                "description",
                TaskStatus.IN_PROGRESS,
                epicTask2.getId()
        );

        manager.createTask(subtask1);
        manager.createTask(subtask2);



        manager.removeTask(subtask2.getId());

        List<Task> testingListTasks = List.of(epicTask2, subtask1);
        List<UUID> testingListIdSubtask = List.of(subtask1.getId());

        assertAll(
                () -> assertTrue(manager.getAllTasks().containsAll(testingListTasks)),
                () -> assertEquals(testingListIdSubtask, epicTask2.getSubtasksId())
        );
    }

    @Test
    void removeEpicTask() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        //Очередная проблема с созданием id
        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask2.getId()
        );
        subtask2  = new Subtask(
                "subtask2",
                "description",
                TaskStatus.IN_PROGRESS,
                epicTask2.getId()
        );

        manager.createTask(subtask1);
        manager.createTask(subtask2);
        manager.removeTask(epicTask2.getId());
        final DatabaseException e = assertThrows(DatabaseException.class, () -> manager.getTask(subtask1.getId()));

        List<Task> testingList = List.of(epicTask1);

        assertAll(
                () -> assertEquals(testingList, manager.getAllTasks()),
                () -> assertTrue(manager.getAllSubtask().isEmpty()),
                () -> assertEquals(e.getMessage(), "Данной задачи нет")
        );
    }

    @Test
    void removeAllTasks() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.removeAllTasks();

        assertTrue(manager.getAllTasks().isEmpty());

    }

    @Test
    void removeAllNormalTasks() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        //Очередная проблема с созданием id
        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask2.getId()
        );
        subtask2  = new Subtask(
                "subtask2",
                "description",
                TaskStatus.IN_PROGRESS,
                epicTask2.getId()
        );

        manager.createTask(subtask1);
        manager.createTask(subtask2);
        manager.removeAllNormalTask();

        List<Task> testingTasks = List.of(epicTask1, epicTask2, subtask1, subtask2);

        assertAll(
                () -> assertTrue(manager.getAllTasks().containsAll(testingTasks)),
                () -> assertEquals(4, manager.getAllTasks().size()),
                () -> assertTrue(manager.getAllNormalTask().isEmpty())
        );
    }

    @Test
    void removeAllSubtasks() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        //Очередная проблема с созданием id
        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask2.getId()
        );
        subtask2  = new Subtask(
                "subtask2",
                "description",
                TaskStatus.IN_PROGRESS,
                epicTask2.getId()
        );

        manager.createTask(subtask1);
        manager.createTask(subtask2);
        manager.removeAllSubtasks();

        List<Task> testingList = List.of(epicTask1, epicTask2);

        assertAll(
                () -> assertTrue(manager.getAllTasks().containsAll(testingList)),
                () -> assertEquals(testingList.size(), manager.getAllTasks().size()),
                () -> assertTrue(manager.getAllSubtask().isEmpty()),
                () -> assertTrue(epicTask2.getSubtasksId().isEmpty())
        );
    }

    @Test
    void removeAllEpicTasks() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        //Очередная проблема с созданием id
        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask2.getId()
        );
        subtask2  = new Subtask(
                "subtask2",
                "description",
                TaskStatus.IN_PROGRESS,
                epicTask2.getId()
        );

        manager.createTask(subtask1);
        manager.createTask(subtask2);
        manager.removeAllEpicTask();

        List<Task> testingList = List.of(task1, task2, task3);

        assertAll(
                () -> assertTrue(testingList.containsAll(manager.getAllTasks())),
                () -> assertEquals(testingList.size(), manager.getAllTasks().size()),
                () -> assertTrue(manager.getAllEpicTask().isEmpty()),
                () -> assertTrue(manager.getAllSubtask().isEmpty())
        );
    }

    @Test
    void getSubtasksToEpicTask() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        //Очередная проблема с созданием id
        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask2.getId()
        );
        subtask2  = new Subtask(
                "subtask2",
                "description",
                TaskStatus.IN_PROGRESS,
                epicTask2.getId()
        );

        manager.createTask(subtask1);
        manager.createTask(subtask2);

        List<Task> testingList = List.of(subtask1, subtask2);

        assertAll(
                () -> assertTrue(testingList.containsAll(manager.getSubtasksToEpicTask(epicTask2.getId()))),
                () -> assertEquals(testingList.size(), manager.getSubtasksToEpicTask(epicTask2.getId()).size()),
                () -> assertTrue(manager.getSubtasksToEpicTask(epicTask1.getId()).isEmpty())
        );
    }

    @Test
    void checkStatusEpicTaskToNew() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(epicTask2);
        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask2.getId()
        );

        assertEquals(epicTask2.getStatus(), TaskStatus.NEW);

        manager.createTask(subtask1);

        assertEquals(epicTask2.getStatus(), TaskStatus.NEW);
    }

    @Test
    void checkStatusEpicTaskToInProgress() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(epicTask2);
        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask2.getId()
        );
        subtask2  = new Subtask(
                "subtask2",
                "description",
                TaskStatus.IN_PROGRESS,
                epicTask2.getId()
        );

        manager.createTask(subtask1);

        manager.createTask(subtask2);

        assertEquals(epicTask2.getStatus(), TaskStatus.IN_PROGRESS);
    }

    @Test
    void checkStatusEpicTaskToReplaceSubtask() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(epicTask2);
        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask2.getId()
        );

        manager.createTask(subtask1);

        Task newSubtask = new Subtask(
                subtask1.getName(),
                subtask1.getDescription(),
                TaskStatus.IN_PROGRESS,
                subtask1.getEpicTaskId()
        );
        newSubtask.setId(subtask1.getId());

        manager.replaceTask(newSubtask);

        assertEquals(epicTask2.getStatus(), TaskStatus.IN_PROGRESS);
    }

    @Test
    void checkStatusEpicTaskToDoneStatusSubtask() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(epicTask2);
        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask2.getId()
        );

        manager.createTask(subtask1);
        Task newSubtask = new Subtask(
                subtask1.getName(),
                subtask1.getDescription(),
                TaskStatus.DONE,
                subtask1.getEpicTaskId()
        );
        newSubtask.setId(subtask1.getId());

        manager.replaceTask(newSubtask);

        assertEquals(epicTask2.getStatus(), TaskStatus.DONE);
    }

    @Test
    void checkStatusEpicTaskToOneTaskDone() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(epicTask2);
        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask2.getId()
        );
        subtask2  = new Subtask(
                "subtask2",
                "description",
                TaskStatus.IN_PROGRESS,
                epicTask2.getId()
        );

        manager.createTask(subtask1);
        manager.createTask(subtask2);

        Task newSubtask = new Subtask(
                subtask1.getName(),
                subtask1.getDescription(),
                TaskStatus.DONE,
                subtask1.getEpicTaskId()
        );
        newSubtask.setId(subtask1.getId());

        manager.replaceTask(newSubtask);

        assertEquals(epicTask2.getStatus(), TaskStatus.IN_PROGRESS);
    }

    @Test
    void checkPriorityTasks() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(task5);
        manager.createTask(task4);
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);


        assertAll(
                () -> assertEquals(5, manager.getPriority().size()),
                () -> assertEquals(task4, manager.getPriority().get(0)),
                () -> assertEquals(task5, manager.getPriority().get(1))
        );
    }

    @Test
    void checkIntersection() throws DatabaseException, KVClientException, TaskException {
        manager.createTask(task4);
        manager.createTask(task5);
        final TaskException e = assertThrows(TaskException.class, () -> manager.createTask(task6));



        assertAll(
                () -> assertEquals(2, manager.getPriority().size()),
                () -> assertEquals(2, manager.getAllTasks().size()),
                () -> assertFalse(manager.getAllTasks().contains(task6)),
                () -> assertEquals(e.getMessage(), "Время выполнения данной задачи пересекается с действующими задачами")
        );
    }

    @Test
    void checkIntersection2() throws DatabaseException, KVClientException, TaskException{
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(task4);
        manager.createTask(task5);
        final TaskException exception = assertThrows(TaskException.class, () -> manager.createTask(task6));





        assertAll(
                () -> assertEquals(5, manager.getPriority().size()),
                () -> assertEquals(5, manager.getAllTasks().size()),
                () -> assertFalse(manager.getAllTasks().contains(task6)),
                () -> assertEquals("Время выполнения данной задачи пересекается с действующими задачами", exception.getMessage())
        );
    }

    @Test
    void checkTimeToEpicTask() throws DatabaseException, KVClientException, TaskException{
        String startTime = "10:00 01.01.2020";
        String endTime = "11:40 01.01.2020";
        String duration = "1:40";

        try {
            manager.createTask(epicTask1);
            subtask3 = new Subtask("name", "description", TaskStatus.NEW, epicTask1.getId(), startTime, "20");
            subtask4 = new Subtask("name", "description", TaskStatus.IN_PROGRESS, epicTask1.getId(), "10:20 01.01.2020", "1:20");
            manager.createTask(subtask3);
            manager.createTask(subtask4);

            assertAll(
                    () -> assertEquals(startTime, epicTask1.getStartTime()),
                    () -> assertEquals(endTime, epicTask1.getEndTime()),
                    () -> assertEquals(duration, epicTask1.getDuration()),
                    () -> assertEquals(manager.getPriority().size(), 2),
                    () -> assertEquals(manager.getPriority().get(0), subtask3)
            );

        } catch (TaskException e) {
            System.out.println(e.getMessage());
        }

    }

    @Test
    void checkTimeToEpicTask2() throws DatabaseException, KVClientException, TaskException {
        String startTime = "10:00 01.01.2020";
        String endTime = "11:40 02.01.2020";
        String duration = "25:40";

        manager.createTask(epicTask1);
        subtask3 = new Subtask("name", "description", TaskStatus.NEW, epicTask1.getId(), startTime, "20");
        subtask4 = new Subtask("name", "description", TaskStatus.IN_PROGRESS, epicTask1.getId(), "10:20 02.01.2020", "1:20");
        manager.createTask(subtask3);
        manager.createTask(subtask4);


        assertAll(
                () -> assertEquals(startTime, epicTask1.getStartTime()),
                () -> assertEquals(endTime, epicTask1.getEndTime()),
                () -> assertEquals(duration, epicTask1.getDuration()),
                () -> assertEquals(manager.getPriority().size(), 2),
                () -> assertEquals(manager.getPriority().get(0), subtask3)
        );


    }

    @Test
    void checkTimeToEpicTask3() throws DatabaseException, KVClientException, TaskException {
        String startTime = "10:00 01.01.2020";
        String endTime = "11:40 01.01.2020";
        String duration = "1:40";


        manager.createTask(epicTask1);
        subtask3 = new Subtask("name", "description", TaskStatus.NEW, epicTask1.getId(), startTime, "20");
        subtask4 = new Subtask("name", "description", TaskStatus.DONE, epicTask1.getId(), "10:20 01.01.2020", "1:20");
        manager.createTask(subtask3);
        manager.createTask(subtask4);

        System.out.println(manager.getPriority());
        assertAll(
                () -> assertEquals(startTime, epicTask1.getStartTime()),
                () -> assertEquals(endTime, epicTask1.getEndTime()),
                () -> assertEquals(duration, epicTask1.getDuration()),
                () -> assertEquals(manager.getPriority().size(), 2),
                () -> assertEquals(manager.getPriority().get(0), subtask3)
        );


    }
}
