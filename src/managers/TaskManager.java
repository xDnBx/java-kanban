package managers;

import enums.TaskStatus;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    static int id = 1;

    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasksFromEpic(Epic epicTask) {
        return epicTask.getSubtaskEpic();
    }

    public void clearTasks() {
        tasks.clear();
        System.out.println("Список задач очищен");
    }

    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskEpic();
        }
        System.out.println("Все подзадачи удалены");
    }

    public void clearEpicTasks() {
        epics.clear();
        subtasks.clear();
        System.out.println("Все эпик задачи и их подзадачи удалены");
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public Subtask getSubtaskById(int subtaskId) {
        return subtasks.get(subtaskId);
    }

    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }

    public Task addNewTask(Task newTask) {
        int newId = generateNewId();
        newTask.setId(newId);
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    public Subtask addNewSubtask(Subtask newSubtask) {
        int epicId = newSubtask.getEpicTaskId();
        int newId = generateNewId();
        newSubtask.setId(newId);
        subtasks.put(newSubtask.getId(), newSubtask);
        Epic epicTask = epics.get(epicId);
        epicTask.addSubtaskEpic(newSubtask);
        updateEpicTaskStatus(epicTask);
        return newSubtask;
    }

    public Epic addNewEpic(Epic newEpic) {
        int newId = generateNewId();
        newEpic.setId(newId);
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    public Task updateTask(Task updatedTask) {
        int taskId = updatedTask.getId();

        if (tasks.containsKey(taskId)) {
            tasks.put(taskId, updatedTask);
            return updatedTask;
        } else {
            System.out.println("Задачи с таким id не существует");
            return null;
        }
    }

    public Subtask updateSubtask(Subtask updatedSubtask) {
        int epicId = updatedSubtask.getEpicTaskId();
        int subtaskId = updatedSubtask.getId();
        if (subtasks.containsKey(subtaskId)) {
            Subtask subtaskToDelete = subtasks.get(subtaskId);
            subtasks.put(subtaskId, updatedSubtask);
            Epic epicTask = epics.get(epicId);
            ArrayList<Subtask> newSubtask = epicTask.getSubtaskEpic();
            newSubtask.remove(subtaskToDelete);
            newSubtask.add(updatedSubtask);
            epicTask.setSubtaskEpic(newSubtask);
            updateEpicTaskStatus(epicTask);
            return updatedSubtask;
        } else {
            System.out.println("Подзадачи с таким id не существует");
            return null;
        }
    }

    public Epic updateEpicTask(Epic updatedEpicTask) {
        int epicId = updatedEpicTask.getId();
        if (epics.containsKey(epicId)) {
            ArrayList<Subtask> newSubtask = new ArrayList<>(epics.get(epicId).getSubtaskEpic());
            epics.put(epicId, updatedEpicTask);
            updatedEpicTask.setSubtaskEpic(newSubtask);
            updateEpicTaskStatus(updatedEpicTask);
            return updatedEpicTask;
        } else {
            System.out.println("Эпика с таким id не существует");
            return null;
        }
    }

    public Task deleteTaskById(int idToDelete) {
        Task taskToDelete = tasks.get(idToDelete);
        tasks.remove(idToDelete);
        return taskToDelete;
    }

    public Subtask deleteSubtaskById(int idToDelete) {
        Subtask subtaskToDelete = subtasks.get(idToDelete);
        int epicId = subtaskToDelete.getEpicTaskId();
        Epic epicTask = epics.get(epicId);
        subtasks.remove(idToDelete);
        epicTask.removeSubtaskEpic(subtaskToDelete);
        updateEpicTaskStatus(epicTask);
        return subtaskToDelete;
    }

    public Epic deleteEpicTaskById(int idToDelete) {
        Epic epicTaskToDelete = epics.get(idToDelete);
        epics.remove(idToDelete);
        for (Subtask subtask : epicTaskToDelete.getSubtaskEpic()) {
            subtasks.remove(subtask.getId());
        }
        epicTaskToDelete.clearSubtaskEpic();
        return epicTaskToDelete;
    }

    public void updateEpicTaskStatus(Epic epicTask) {
        int numberOfNEWs = 0;
        int numberOfDONEs = 0;
        ArrayList<Subtask> newSubtasks = epicTask.getSubtaskEpic();

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

    public int generateNewId() {
        return id++;
    }

    @Override
    public String toString() {
        return "TaskManager{" +
                "tasks=" + tasks +
                ", subtasks=" + subtasks +
                ", epics=" + epics +
                '}';
    }
}