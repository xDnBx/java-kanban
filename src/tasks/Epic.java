package tasks;

import enums.TaskStatus;

import java.util.ArrayList;

public class Epic extends Task {
    protected ArrayList<Subtask> subtaskEpic = new ArrayList<>();

    public Epic(String name, String description, TaskStatus taskStatus) {
        super(name, description, taskStatus);
    }

    public ArrayList<Subtask> getSubtaskEpic() {
        return subtaskEpic;
    }

    public void setSubtaskEpic(ArrayList<Subtask> subtaskEpic) {
        this.subtaskEpic = subtaskEpic;
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
