package tasks;

import enums.TaskStatus;
import managers.TaskManager;

import java.util.ArrayList;

public class Subtask extends Task{
    protected int epicTaskId;
    TaskManager taskManager = new TaskManager();

    public Subtask(String name, String description, TaskStatus taskStatus, int epicTaskId) {
        super(name, description, taskStatus);
        this.epicTaskId = epicTaskId;
    }

    public int getEpicTaskId() {
        return epicTaskId;
    }

    public void setEpicTaskId(int epicTaskId) {
        this.epicTaskId = epicTaskId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id='" + id + '\'' +
                ", epicTaskId=" + epicTaskId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
