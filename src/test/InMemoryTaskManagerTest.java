package test;

import com.yandex.kanban.exception.TaskException;
import com.yandex.kanban.managers.Managers;
import com.yandex.kanban.managers.taskManager.TaskManager;
import com.yandex.kanban.model.EpicTask;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    TaskManager manager;
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
    void createManager() {
        manager = Managers.getMemoryTaskManager();

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
    void createTask() {
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
    void createTasks() {
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
    void createSubtaskNotEpic() {
        manager.createTask(task1);
        manager.createTask(subtask1);

        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask2.getId()
        );

        List<Task> testingList = List.of(task1,subtask1);

        assertAll(
                () -> assertFalse(manager.getAllTasks().containsAll(testingList)),
                () -> assertNull(manager.getTask(subtask1.getId())),
                () -> assertEquals(List.of(task1), manager.getAllTasks())
        );
    }

    @Test
    void createTasksRepeatTask() {
        manager.createTask(task1);
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
    void emptyManager() {
        assertAll(
                () -> assertTrue(manager.getAllTasks().isEmpty()),
                () -> assertNotNull(manager.getAllTasks())
        );
    }

    @Test
    void getTask() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        assertAll(
                () -> assertEquals(manager.getTask(task1.getId()), task1),
                () -> assertEquals(manager.getTask(task2.getId()), task2),
                () -> assertNotNull(manager.getTask(task3.getId())),
                () -> assertNull(manager.getTask(epicTask1.getId()))
        );
    }

    @Test
    void getNormalTasks() {
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
    void getNormalTasksIsEmptyList() {
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        assertAll(
                () -> assertTrue(manager.getAllNormalTask().isEmpty()),
                () -> assertNotNull(manager.getAllNormalTask())
        );
    }

    @Test
    void getEpicTasks() {
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
    void getEpicTasksIsEmptyList() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        assertAll(
                () -> assertTrue(manager.getAllEpicTask().isEmpty()),
                () -> assertNotNull(manager.getAllEpicTask())
        );
    }

    @Test
    void getSubtasks() {
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
    void getSubtasksIsEmptyList() {
        manager.createTask(task1);

        assertAll(
                () -> assertTrue(manager.getAllSubtask().isEmpty()),
                () -> assertNotNull(manager.getAllSubtask())
        );
    }

    @Test
    void removeTask() {
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
    void removeSubtask() {
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
    void removeEpicTask() {
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

        List<Task> testingList = List.of(epicTask1);

        assertAll(
                () -> assertEquals(testingList, manager.getAllTasks()),
                () -> assertTrue(manager.getAllSubtask().isEmpty()),
                () -> assertNull(manager.getTask(subtask1.getId()))
        );
    }

    @Test
    void removeAllTasks() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.removeAllTasks();

        assertAll(
                () -> assertTrue(manager.getAllTasks().isEmpty()),
                () -> assertNull(manager.getTask(task1.getId()))
        );
    }

    @Test
    void removeAllNormalTasks() {
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
    void removeAllSubtasks() {
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
    void removeAllEpicTasks() {
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
    void getSubtasksToEpicTask() {
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
    void checkStatusEpicTaskToNew() {
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
    void checkStatusEpicTaskToInProgress() {
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
    void checkStatusEpicTaskToReplaceSubtask() {
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
    void checkStatusEpicTaskToDoneStatusSubtask() {
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
    void checkStatusEpicTaskToOneTaskDone() {
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
    void checkPriorityTasks() {
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
    void checkIntersection() {
        manager.createTask(task4);
        manager.createTask(task5);
        manager.createTask(task6);



        assertAll(
                () -> assertEquals(2, manager.getPriority().size()),
                () -> assertEquals(2, manager.getAllTasks().size()),
                () -> assertFalse(manager.getAllTasks().contains(task6))
        );
    }

    @Test
    void checkIntersection2() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(task4);
        manager.createTask(task5);
        manager.createTask(task6);



        assertAll(
                () -> assertEquals(5, manager.getPriority().size()),
                () -> assertEquals(5, manager.getAllTasks().size()),
                () -> assertFalse(manager.getAllTasks().contains(task6))
        );
    }

    @Test
    void checkTimeToEpicTask() {
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
    void checkTimeToEpicTask2() {
        String startTime = "10:00 01.01.2020";
        String endTime = "11:40 02.01.2020";
        String duration = "25:40";

        try {
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

        } catch (TaskException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void checkTimeToEpicTask3() {
        String startTime = "10:00 01.01.2020";
        String endTime = "10:20 01.01.2020";
        String duration = "0:20";

        try {
            manager.createTask(epicTask1);
            subtask3 = new Subtask("name", "description", TaskStatus.NEW, epicTask1.getId(), startTime, "20");
            subtask4 = new Subtask("name", "description", TaskStatus.DONE, epicTask1.getId(), "10:20 01.01.2020", "1:20");
            manager.createTask(subtask3);
            manager.createTask(subtask4);

            assertAll(
                    () -> assertEquals(startTime, epicTask1.getStartTime()),
                    () -> assertEquals(endTime, epicTask1.getEndTime()),
                    () -> assertEquals(duration, epicTask1.getDuration()),
                    () -> assertEquals(manager.getPriority().size(), 1),
                    () -> assertEquals(manager.getPriority().get(0), subtask3)
            );

        } catch (TaskException e) {
            System.out.println(e.getMessage());
        }
    }
}
