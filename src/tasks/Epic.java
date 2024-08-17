package tasks;

import enums.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    protected final List<Subtask> subtaskEpic = new ArrayList<>();

    public Epic(String name, String description, TaskStatus taskStatus) {
        super(name, description, taskStatus);
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
    public String toString() {
        return "Epic{" +
                "id='" + id + '\'' +
                ", subtaskIds=" + subtaskEpic + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
