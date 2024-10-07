package tasks;

import enums.TaskStatus;
import enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    protected final int epicTaskId;

    public Subtask(String name, String description, TaskStatus taskStatus, Duration duration, LocalDateTime startTime,
                   int epicTaskId) {
        super(name, description, taskStatus, duration, startTime);
        this.taskType = TaskType.SUBTASK;
        this.epicTaskId = epicTaskId;
    }

    public Subtask(int id, String name, String description, TaskStatus taskStatus, Duration duration,
                   LocalDateTime startTime, int epicTaskId) {
        super(id, name, description, taskStatus, duration, startTime);
        this.taskType = TaskType.SUBTASK;
        this.epicTaskId = epicTaskId;
    }

    public int getEpicTaskId() {
        return epicTaskId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                ", taskType=" + taskType +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + getEndTime() +
                ", epicTaskId=" + epicTaskId +
                '}';
    }
}
