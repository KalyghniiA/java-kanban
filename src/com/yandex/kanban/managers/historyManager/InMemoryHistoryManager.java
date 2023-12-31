package com.yandex.kanban.managers.historyManager;

import com.yandex.kanban.exception.CustomListException;
import com.yandex.kanban.exception.TaskException;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.util.CustomLinkedList;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final static int MAX_SIZE = 10;
    private final CustomLinkedList<Task> history = new CustomLinkedList<>();
    private final Map<UUID, CustomLinkedList.Node<Task>> historyDictionary = new HashMap<>();
    @Override
    public void add(Task task) {
        try {
            if (task == null) {
                throw new TaskException("Нельзя передавать пустое значение");
            }
        } catch (TaskException e) {
            System.out.println(e.getMessage());
            return;
        }

        UUID id = task.getId();

        try {
            if (historyDictionary.containsKey(id)) {
                history.remove(historyDictionary.get(id));
                historyDictionary.remove(id);
            }
        } catch (CustomListException e) {
            System.out.println(e.getMessage());
            return;
        }


        if (history.size() == MAX_SIZE) {
            try {
                UUID oldId;
                oldId = history.getFirst().getData().getId();
                history.remove(historyDictionary.get(oldId));
                historyDictionary.remove(oldId);
            } catch (CustomListException e) {
                System.out.println(e.getMessage());
                return;
            }
        }

        historyDictionary.put(task.getId(), history.add(task));
    }

    @Override
    public void remove(UUID id) {
        try {
            if (id == null) {
                throw new TaskException("Нельзя передавать пустое значение");
            }
        } catch (TaskException e) {
            System.out.println(e.getMessage());
            return;
        }

        try {
            history.remove(historyDictionary.get(id));
            historyDictionary.remove(id);
        } catch (CustomListException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Task> getHistory() {
        if (history.size() == 0) {
            System.out.println("История пуста");
            return new ArrayList<>();
        }

        List<Task> result = new LinkedList<>();
        CustomLinkedList.Node<Task> next;

        try {
            next = history.getFirst();
        } catch (CustomListException e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }

        for (int i = 0; i < history.size(); i++) {
            result.add(next.getData());
            next = next.getNext();
            if (next == null) {
                break;
            }
        }

        return result;
    }

    @Override
    public void clear() {
        if (history.size() == 0) {
            System.out.println("История пуста");
            return;
        }

        CustomLinkedList.Node<Task> node;
        CustomLinkedList.Node<Task> nextNode;
        try {
            node = history.getFirst();
        } catch (CustomListException e) {
            System.out.println(e.getMessage());
            return;
        }
        int size = history.size();

        for (int i = 0; i < size; i++) {
            nextNode = node.getNext();
            try {
                history.remove(node);
            } catch (CustomListException e) {
                System.out.println(e.getMessage());
            }
            node = nextNode;
            if (nextNode == null) {
                break;
            }
        }

        historyDictionary.clear();
    }
}
