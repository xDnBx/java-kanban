package tasks;

import enums.TaskStatus;

public class Subtask extends Task {
    protected final int epicTaskId;

    public Subtask(String name, String description, TaskStatus taskStatus, int epicTaskId) {
        super(name, description, taskStatus);
        this.epicTaskId = epicTaskId;
    }

    public Subtask(int id, String name, String description, TaskStatus taskStatus, int epicTaskId) {
        super(id, name, description, taskStatus);
        this.epicTaskId = epicTaskId;
    }

    public int getEpicTaskId() {
        return epicTaskId;
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
