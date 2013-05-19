/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package progin5;

/**
 *
 * @author Endy
 */
import Model.Task;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Requester {

    Socket requestSocket;
    ObjectOutputStream out;
    ObjectInputStream in;
    String message;
    ArrayList<Task> tasklist;

    Requester() {
    }

    void run() {
        try {
            //1. creating a socket to connect to the server
            requestSocket = new Socket("localhost", 8888);
            System.out.println("Connected to localhost in port 8888");
            //2. get Input and Output streams
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());
            //3: Communicating with the server
            do {
                try {
                    message = (String) in.readObject();
                    System.out.println("server>" + message);
                    sendMessage("showtask");
                    parseTask(message);
                    sendMessage("logout");
                } catch (ClassNotFoundException classNot) {
                    System.err.println("data received in unknown format");
                }
            } while (!message.equals("logout"));
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            //4: Closing connection
            try {
                in.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    void sendMessage(String msg) {
        try {
            out.writeObject(msg);
            out.flush();
            System.out.println("client>" + msg);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    void parseTask(String msg) {
        String[] tasks;
        boolean status;
        tasklist = new ArrayList<>();
        tasks = msg.split("<br>");
        for (int i = 1; i < tasks.length; i++) {
            String[] task_el;
            task_el = tasks[i].split(",");
            if (task_el[2].equals("1")) {
                status = true;
            } else {
                status = false;
            }
            ArrayList<String> assignees;
            ArrayList<String> tags;
            String[] arr_ass;
            String[] arr_tags;
            arr_tags = task_el[6].split(";");
            tags = new ArrayList<>(Arrays.asList(arr_tags));

            arr_ass = task_el[7].split(";");
            assignees = new ArrayList<>(Arrays.asList(arr_ass));

            Task task = new Task(task_el[3], task_el[0], task_el[1], assignees, tags, status, task_el[4]);
            tasklist.add(task);
        }
        for (Task t : tasklist) {
            System.out.println(t.taskId + t.taskName + t.deadline + t.assignees.toString() + t.tags.toString() + t.status + t.categName);
        }
    }

    public static void main(String args[]) {
        Requester client = new Requester();
        client.run();
    }
}