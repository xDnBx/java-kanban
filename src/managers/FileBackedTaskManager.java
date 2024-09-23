package managers;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import exceptions.TaskTypeException;
import enums.TaskStatus;
import enums.TaskType;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {
    protected File file;
    public final String HEADER = "ID,TYPE,NAME,STATUS,DESCRIPTION,EPIC\n";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void clearEpicTasks() {
        super.clearEpicTasks();
        save();
    }

    @Override
    public Task addNewTask(Task newTask) {
        super.addNewTask(newTask);
        save();
        return newTask;
    }

    @Override
    public Subtask addNewSubtask(Subtask newSubtask) {
        super.addNewSubtask(newSubtask);
        save();
        return newSubtask;
    }

    @Override
    public Epic addNewEpic(Epic newEpic) {
        super.addNewEpic(newEpic);
        save();
        return newEpic;
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        super.updateSubtask(updatedSubtask);
        save();
    }

    @Override
    public void updateEpicTask(Epic updatedEpicTask) {
        super.updateEpicTask(updatedEpicTask);
        save();
    }

    @Override
    public void deleteTaskById(int idToDelete) {
        super.deleteTaskById(idToDelete);
        save();
    }

    @Override
    public void deleteSubtaskById(int idToDelete) {
        super.deleteSubtaskById(idToDelete);
        save();
    }

    @Override
    public void deleteEpicTaskById(int idToDelete) {
        super.deleteEpicTaskById(idToDelete);
        save();
    }

    private void save() {
        try (Writer fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
            fileWriter.write(HEADER);
            for (Task task : getTasks()) {
                fileWriter.write(toString(task));
            }
            for (Epic epic : getEpics()) {
                fileWriter.write(toString(epic));
            }
            for (Subtask subtask : getSubTasks()) {
                fileWriter.write(toString(subtask));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи файла: " + e.getMessage());
        }
    }

    private String toString(Task task) {
        if (task == null) {
            return null;
        }

        TaskType taskClass = task.getTaskType();
        String taskString = task.getId() + "," + taskClass + "," + task.getName() + "," + task.getTaskStatus() +
                "," + task.getDescription();

        if (taskClass.equals(TaskType.SUBTASK)) {
            Subtask subtask = (Subtask) task;
            taskString = taskString + "," + subtask.getEpicTaskId();
        }
        return taskString + "\n";
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        int maxId = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

            while (br.ready()) {
                String line = br.readLine();
                if (!line.startsWith("ID")) {
                    Task task = fromString(line);
                    TaskType taskClass = task.getTaskType();
                    if (taskClass.equals(TaskType.TASK)) {
                        taskManager.tasks.put(task.getId(), task);
                    } else if (taskClass.equals(TaskType.EPIC)) {
                        taskManager.epics.put(task.getId(), (Epic) task);
                    } else if (taskClass.equals(TaskType.SUBTASK)) {
                        Subtask subtask = (Subtask) task;
                        taskManager.subtasks.put(subtask.getId(), subtask);
                        int epicId = subtask.getEpicTaskId();
                        Epic epic = taskManager.epics.get(epicId);
                        epic.addSubtaskEpic(subtask);
                    }
                    if (task.getId() > maxId) {
                        maxId = task.getId();
                    }
                }
            }
            taskManager.afterLoadId(maxId);
            return taskManager;
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    private static Task fromString(String value) {
        String[] values = value.split(",");
        Task task;

        if (values[1].equals(TaskType.TASK.toString())) {
            task = new Task(Integer.parseInt(values[0]), values[2], values[4], TaskStatus.valueOf(values[3]));
        } else if (values[1].equals(TaskType.SUBTASK.toString())) {
            task = new Subtask(Integer.parseInt(values[0]), values[2], values[4], TaskStatus.valueOf(values[3]),
                    Integer.parseInt(values[5]));
        } else if (values[1].equals(TaskType.EPIC.toString())) {
            task = new Epic(Integer.parseInt(values[0]), values[2], values[4], TaskStatus.valueOf(values[3]));
        } else {
            throw new TaskTypeException("Нет такого типа задачи: " + values[1]);
        }
        return task;
    }
}