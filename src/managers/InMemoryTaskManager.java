package managers;

import enums.TaskStatus;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private int id = 1;

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasksFromEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epicTask = epics.get(epicId);
            return epicTask.getSubtaskEpic();
        } else {
            System.out.println("Эпика с таким id не существует");
            return new ArrayList<>();
        }
    }

    @Override
    public void clearTasks() {
        tasks.clear();
        System.out.println("Список задач очищен");
    }

    @Override
    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskEpic();
            epic.setTaskStatus(TaskStatus.NEW);
        }
        System.out.println("Все подзадачи удалены");
    }

    @Override
    public void clearEpicTasks() {
        epics.clear();
        subtasks.clear();
        System.out.println("Все эпик задачи и их подзадачи удалены");
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        if (task != null) {
            historyManager.add(task);
        } else {
            System.out.println("Задачи с таким id не существует, попробуйте еще раз");
        }
        return task;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            historyManager.add(subtask);
        } else {
            System.out.println("Подзадачи с таким id не существует, попробуйте еще раз");
        }
        return subtask;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            historyManager.add(epic);
        } else {
            System.out.println("Эпика с таким id не существует, попробуйте еще раз");
        }
        return epic;
    }

    @Override
    public Task addNewTask(Task newTask) {
        int newId = generateNewId();
        newTask.setId(newId);
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    @Override
    public Subtask addNewSubtask(Subtask newSubtask) {
        int epicId = newSubtask.getEpicTaskId();
        if (epics.containsKey(epicId)) {
            int newId = generateNewId();
            newSubtask.setId(newId);
            subtasks.put(newSubtask.getId(), newSubtask);
            Epic epicTask = epics.get(epicId);
            epicTask.addSubtaskEpic(newSubtask);
            updateEpicTaskStatus(epicTask);
            return newSubtask;
        } else {
            System.out.println("Такого эпика не существует, невозможно добавить подзадачу, попробуйте еще раз");
            return null;
        }
    }

    @Override
    public Epic addNewEpic(Epic newEpic) {
        int newId = generateNewId();
        newEpic.setId(newId);
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    @Override
    public void updateTask(Task updatedTask) {
        int taskId = updatedTask.getId();

        if (tasks.containsKey(taskId)) {
            tasks.put(taskId, updatedTask);
        } else {
            System.out.println("Задачи с таким id не существует, попробуйте еще раз");
        }
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        int epicId = updatedSubtask.getEpicTaskId();
        int subtaskId = updatedSubtask.getId();
        if (subtasks.containsKey(subtaskId)) {
            Subtask subtaskToDelete = subtasks.get(subtaskId);
            subtasks.put(subtaskId, updatedSubtask);
            Epic epicTask = epics.get(epicId);
            List<Subtask> newSubtask = epicTask.getSubtaskEpic();
            newSubtask.remove(subtaskToDelete);
            newSubtask.add(updatedSubtask);
            updateEpicTaskStatus(epicTask);
        } else {
            System.out.println("Подзадачи с таким id не существует");
        }
    }

    @Override
    public void updateEpicTask(Epic updatedEpicTask) {
        int epicId = updatedEpicTask.getId();
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            epic.setDescription(updatedEpicTask.getDescription());
            epic.setName(updatedEpicTask.getName());
        } else {
            System.out.println("Эпика с таким id не существует");
        }
    }

    @Override
    public void deleteTaskById(int idToDelete) {
        if (tasks.containsKey(idToDelete)) {
            tasks.remove(idToDelete);
            System.out.println("Задача с id " + idToDelete + " успешно удалена");
        } else {
            System.out.println("Задачи с таким id не существует, попробуйте еще раз");
        }
    }

    @Override
    public void deleteSubtaskById(int idToDelete) {
        if (subtasks.containsKey(idToDelete)) {
            Subtask subtaskToDelete = subtasks.get(idToDelete);
            int epicId = subtaskToDelete.getEpicTaskId();
            Epic epicTask = epics.get(epicId);
            subtasks.remove(idToDelete);
            epicTask.removeSubtaskEpic(subtaskToDelete);
            updateEpicTaskStatus(epicTask);
            System.out.println("Подзадача с id " + idToDelete + " успешно удалена");
        } else {
            System.out.println("Подзадачи с таким id не существует");
        }
    }

    @Override
    public void deleteEpicTaskById(int idToDelete) {
        if (epics.containsKey(idToDelete)) {
            Epic epicTaskToDelete = epics.get(idToDelete);
            epics.remove(idToDelete);
            for (Subtask subtask : epicTaskToDelete.getSubtaskEpic()) {
                subtasks.remove(subtask.getId());
            }
            epicTaskToDelete.clearSubtaskEpic();
            System.out.println("Эпик с id " + idToDelete + " и связанные с ним подзадачи успешно удалены");
        } else {
            System.out.println("Эпика с таким id не существует");
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicTaskStatus(Epic epicTask) {
        int numberOfNEWs = 0;
        int numberOfDONEs = 0;
        List<Subtask> newSubtasks = epicTask.getSubtaskEpic();

        for (Subtask subtask : newSubtasks) {
            if (subtask.getTaskStatus() == TaskStatus.NEW) {
                numberOfNEWs++;
            } else if (subtask.getTaskStatus() == TaskStatus.DONE) {
                numberOfDONEs++;
            }
        }

        if (newSubtasks.isEmpty() || numberOfNEWs == newSubtasks.size()) {
            epicTask.setTaskStatus(TaskStatus.NEW);
        } else if (numberOfDONEs == newSubtasks.size()) {
            epicTask.setTaskStatus(TaskStatus.DONE);
        } else {
            epicTask.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private int generateNewId() {
        return id++;
    }
}