/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Jeremy Joseph Hanniel
 */
public class Task {
    public String taskId;
    public String taskName;
    public String deadline;
    public ArrayList<String> assignees;
    public ArrayList<String> tags;
    public boolean status;
    public String categName;

    public Task(String taskId, String taskName, String deadline, ArrayList<String> assignees, ArrayList<String> tags, boolean status, String categName) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.deadline = deadline;
        this.assignees = assignees;
        this.tags = tags;
        this.status = status;
        this.categName = categName;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
