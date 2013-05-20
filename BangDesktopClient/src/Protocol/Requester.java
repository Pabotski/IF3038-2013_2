/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Protocol;

/**
 *
 * @author Endy
 */
import Utils.Task;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Requester {

    Socket requestSocket;
    ObjectOutputStream out;
    ObjectInputStream in;
    String message;
    String response;
    boolean connected = false;

    public String getResponse() {
        return response;
    }
    
    ArrayList<Task> tasklist;

    public ArrayList<Task> getTasklist() {
        return tasklist;
    }

    public boolean isConnected() {
        return connected;
    }
    
    public Requester() {
    }

    public void Connect(String msg) {
        message = null;
        response = null;
        connected = false;
        try {
            //1. creating a socket to connect to the server
            requestSocket = new Socket("localhost", 8888);
            System.out.println("Connected to localhost in port 8888");
            //2. get Input and Output streams
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());
            //3: Communicating with the server
            sendMessage(msg);
            do {
                try {
                    message = (String) in.readObject();
                    
                    if (message.split(":")[0].equals("Login") || message.equals("connected") || message.split(":")[0].equals("sync")) {
                        response = message;
                    }
                    
                    System.out.println("server>" + message);
                    
                    if (msg.split("~")[0].equals("showtask")) {
                        parseTask(message);
                    }
                    connected = true;
                    sendMessage("end");
                } catch (ClassNotFoundException classNot) {
                    System.err.println("data received in unknown format");
                }
            } while (!message.equals("end"));
            
            
        } catch (SocketException ex) {
            System.err.println(ex.getMessage());
            System.out.println("Caught by SOCKET EXCEPTION");
            
        } catch (IOException ex) {
            ex.getMessage();
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

    public void sendMessage(String msg) {
        try {
            out.writeObject(msg);
            out.flush();
            System.out.println("client>" + msg);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void parseTask(String msg) {
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
    }
}