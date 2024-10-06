package tasks;

import enums.TaskStatus;
import enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    protected final List<Subtask> subtaskEpic = new ArrayList<>();
    protected LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.taskStatus = TaskStatus.NEW;
        this.taskType = TaskType.EPIC;
        this.duration = Duration.ZERO;
        this.startTime = null;
        this.endTime = null;
    }

    public Epic(int id, String name, String description, TaskStatus taskStatus, Duration duration,
                LocalDateTime startTime, LocalDateTime endTime) {
        super(id, name, description, taskStatus, duration, startTime);
        this.taskType = TaskType.EPIC;
        this.endTime = endTime;
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.taskStatus = TaskStatus.NEW;
        this.taskType = TaskType.EPIC;
        this.duration = Duration.ZERO;
        this.startTime = null;
        this.endTime = null;
    }

    public List<Subtask> getSubtaskEpic() {
        return subtaskEpic;
    }

    public void addSubtaskEpic(Subtask subtaskId) {
        subtaskEpic.add(subtaskId);
    }

    public void removeSubtaskEpic(Subtask subtaskId) {
        subtaskEpic.remove(subtaskId);
    }

    public void clearSubtaskEpic() {
        subtaskEpic.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                ", taskType=" + taskType +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", subtaskEpic=" + subtaskEpic +
                '}';
    }
}
