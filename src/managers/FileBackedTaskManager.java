package managers;

import Exceptions.ManagerLoadException;
import Exceptions.ManagerSaveException;
import Exceptions.TaskTypeException;
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
            fileWriter.write("ID,TYPE,NAME,STATUS,DESCRIPTION,EPIC\n");
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

        String taskClass = task.getClass().getName().toUpperCase().substring(6);
        String taskString = task.getId() + "," + taskClass + "," + task.getName() + "," + task.getTaskStatus() +
                "," + task.getDescription();

        if (taskClass.equals(TaskType.TASK.toString()) || taskClass.equals(TaskType.EPIC.toString())) {
            taskString = taskString + "\n";
        } else if (taskClass.equals(TaskType.SUBTASK.toString())) {
            Subtask subtask = (Subtask) task;
            taskString = taskString + "," + subtask.getEpicTaskId() + "\n";
        }
        return taskString;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

            while (br.ready()) {
                String line = br.readLine();
                if (!line.startsWith("ID")) {
                    Task task = fromString(line);
                    String taskClass = task.getClass().getName().toUpperCase().substring(6);
                    if (taskClass.equals(TaskType.TASK.toString())) {
                        taskManager.tasks.put(task.getId(), task);
                    } else if (taskClass.equals(TaskType.SUBTASK.toString())) {
                        taskManager.subtasks.put(task.getId(), (Subtask) task);
                    } else if (taskClass.equals(TaskType.EPIC.toString())) {
                        taskManager.epics.put(task.getId(), (Epic) task);
                    }
                }
            }
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