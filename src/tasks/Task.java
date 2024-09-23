package tasks;

import enums.TaskStatus;
import enums.TaskType;

import java.util.Objects;

public class Task {

    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus taskStatus;
    protected TaskType taskType;

    public Task(String name, String description, TaskStatus taskStatus) {
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
        this.taskType = TaskType.TASK;
    }

    public Task(int id, String name, String description, TaskStatus taskStatus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
        this.taskType = TaskType.TASK;
    }

    public String getName() {
        return name;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
