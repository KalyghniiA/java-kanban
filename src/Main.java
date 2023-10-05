import com.yandex.kanban.managers.Managers;
import com.yandex.kanban.managers.taskManager.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefaultTaskManager();
    }
}
