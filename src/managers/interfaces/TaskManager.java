package managers.interfaces;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getTasks();

    List<Subtask> getSubTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasksFromEpic(int epicId);

    List<Task> getPrioritizedTasks();

    void clearTasks();

    void clearSubtasks();

    void clearEpicTasks();

    Task getTaskById(int taskId);

    Subtask getSubtaskById(int subtaskId);

    Epic getEpicById(int epicId);

    Task addNewTask(Task newTask);

    Subtask addNewSubtask(Subtask newSubtask);

    Epic addNewEpic(Epic newEpic);

    void updateTask(Task updatedTask);

    void updateSubtask(Subtask updatedSubtask);

    void updateEpicTask(Epic updatedEpicTask);

    void deleteTaskById(int idToDelete);

    void deleteSubtaskById(int idToDelete);

    void deleteEpicTaskById(int idToDelete);

    List<Task> getHistory();
}
