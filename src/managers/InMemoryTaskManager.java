package managers;

import enums.TaskStatus;
import exceptions.TaskTimeException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {

    protected int id = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

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
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void clearTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            deletePrioritizedTask(task);
        }
        tasks.clear();
        System.out.println("Список задач очищен");
    }

    @Override
    public void clearSubtasks() {
        for (Task task : subtasks.values()) {
            historyManager.remove(task.getId());
            deletePrioritizedTask(task);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskEpic();
            epic.setTaskStatus(TaskStatus.NEW);
        }
        System.out.println("Все подзадачи удалены");
    }

    @Override
    public void clearEpicTasks() {
        for (Task task : epics.values()) {
            historyManager.remove(task.getId());
            deletePrioritizedTask(task);
        }
        epics.clear();
        for (Task task : subtasks.values()) {
            historyManager.remove(task.getId());
            deletePrioritizedTask(task);
        }
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
        if (isPrioritizedTasksCrossing(newTask)) {
            throw new TaskTimeException("Задача пересекается по времени с другой задачей");
        }
        int newId = generateNewId();
        newTask.setId(newId);
        tasks.put(newTask.getId(), newTask);
        addPrioritizedTask(tasks.get(newTask.getId()));
        return newTask;
    }

    @Override
    public Subtask addNewSubtask(Subtask newSubtask) {
        if (isPrioritizedTasksCrossing(newSubtask)) {
            throw new TaskTimeException("Подзадача пересекается по времени с другой подзадачей");
        }
        int epicId = newSubtask.getEpicTaskId();
        if (epics.containsKey(epicId)) {
            int newId = generateNewId();
            newSubtask.setId(newId);
            subtasks.put(newSubtask.getId(), newSubtask);
            addPrioritizedTask(subtasks.get(newSubtask.getId()));
            Epic epicTask = epics.get(epicId);
            epicTask.addSubtaskEpic(newSubtask);
            updateEpicTaskStatus(epicTask);
            updateEpicTaskTimes(epicTask);
        } else {
            System.out.println("Такого эпика не существует, невозможно добавить подзадачу, попробуйте еще раз");
        }
        return newSubtask;
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
            deletePrioritizedTask(tasks.get(taskId));
            tasks.put(taskId, updatedTask);
            addPrioritizedTask(updatedTask);
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
            deletePrioritizedTask(subtaskToDelete);
            subtasks.put(subtaskId, updatedSubtask);
            addPrioritizedTask(updatedSubtask);
            Epic epicTask = epics.get(epicId);
            List<Subtask> newSubtask = epicTask.getSubtaskEpic();
            newSubtask.remove(subtaskToDelete);
            newSubtask.add(updatedSubtask);
            updateEpicTaskStatus(epicTask);
            updateEpicTaskTimes(epicTask);
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
            deletePrioritizedTask(tasks.get(idToDelete));
            tasks.remove(idToDelete);
            historyManager.remove(idToDelete);
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
            deletePrioritizedTask(subtaskToDelete);
            subtasks.remove(idToDelete);
            historyManager.remove(idToDelete);
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
            historyManager.remove(idToDelete);
            for (Subtask subtask : epicTaskToDelete.getSubtaskEpic()) {
                deletePrioritizedTask(subtasks.get(subtask.getId()));
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
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

    private void updateEpicTaskStatus(Epic epic) {
        List<Subtask> newSubtasks = epic.getSubtaskEpic();

        boolean allNEWs = newSubtasks.stream().allMatch(subtask -> subtask.getTaskStatus() == TaskStatus.NEW);
        boolean allDONEs = newSubtasks.stream().allMatch(subtask -> subtask.getTaskStatus() == TaskStatus.DONE);

        if (newSubtasks.isEmpty() || allNEWs) {
            epic.setTaskStatus(TaskStatus.NEW);
        } else if (allDONEs) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private void updateEpicTaskTimes(Epic epic) {
        List<Subtask> newSubtasks = epic.getSubtaskEpic();

        List<Subtask> sortedSubtasks = newSubtasks.stream()
                .sorted(Comparator.comparing(Subtask::getStartTime))
                .toList();
        Duration allDurations = newSubtasks.stream()
                .map(Subtask::getDuration)
                .reduce(Duration::plus)
                .orElse(null);

        epic.setStartTime(sortedSubtasks.getFirst().getStartTime());
        epic.setEndTime(sortedSubtasks.getLast().getEndTime());
        epic.setDuration(allDurations);
    }

    private int generateNewId() {
        return id++;
    }

    private void addPrioritizedTask(Task task) {
        if (task.getStartTime() != null) prioritizedTasks.add(task);
    }

    private void deletePrioritizedTask(Task task) {
        if (task.getStartTime() != null) prioritizedTasks.remove(task);
    }

    private boolean isPrioritizedTasksCrossing(Task task) {
        return prioritizedTasks.stream()
                .filter(task1 -> task1.getStartTime() != null)
                .anyMatch(task1 -> isTasksCrossing(task1, task));
    }

    private boolean isTasksCrossing(Task task1, Task task2) {
        LocalDateTime startTime1 = task1.getStartTime();
        LocalDateTime endTime1 = task1.getEndTime();
        LocalDateTime startTime2 = task2.getStartTime();
        LocalDateTime endTime2 = task2.getEndTime();

        if (startTime1 == null || startTime2 == null) {
            return false;
        }
        return (startTime1.isBefore(endTime2) && endTime1.isAfter(startTime2));
    }
}