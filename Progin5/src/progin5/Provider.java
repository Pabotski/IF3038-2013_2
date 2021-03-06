package progin5;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Provider extends Thread {

    Socket clientSocket;
    Socket connection = null;
    ObjectOutputStream out;
    ObjectInputStream in;
    String message;
    String[] cmd;
    String result = "";

    Provider(Socket clientSoc) {
        clientSocket = clientSoc;
        start();
    }

    @Override
    public void run() {
        try {
            System.out.println("New Thread Started to Run");
            //3. get Input and Output streams
            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());
            sendMessage("Connection successful");
            //4. The two parts communicate via the input and output streams
            System.out.println("tes1");
            do {
                try {
                    message = (String) in.readObject();
                    System.out.println("client>" + message);
                    cmd = message.split(" ");
                    if (cmd[0].equals("login")) {
                        result = urlReader("http://nicholasrio.ap01.aws.af.cm/rest/authentication?usr=" + cmd[1] + "&psw=" + cmd[2]);
                        if (result.equals("1")) {
                            sendMessage("sok login");
                        } else {
                            sendMessage("ga boleh login");
                        }
                    } else if (cmd[0].equals("showtask")) {
                        result = urlReader("http://nicholasrio.ap01.aws.af.cm/rest/showTaskdong?username=" + cmd[1]);
                        if (!result.equals("")) {
                            sendMessage(result);
                        } else {
                            sendMessage("No Task");
                        }
                    } else if (cmd[0].equals("sync")) {
                        String[] listchange;
                        listchange = cmd[1].split(";");
                        boolean error = false;
                        for (int i = 0; i < listchange.length; i++) {
                            String[] tochange;
                            tochange = listchange[i].split(",");
                            result = urlReader("http://nicholasrio.ap01.aws.af.cm/sync/taskStatusService.php?idtask=" + tochange[0] + "&checked=" + tochange[1] + "&timestamp=" + tochange[2]);
                        }
                        sendMessage("sync success");
                    } else if (cmd[0].equals("changestatus")) {
                        String[] tochange;
                        tochange = cmd[1].split(",");
                        result = urlReader("http://nicholasrio.ap01.aws.af.cm/sync/taskStatusService.php?idtask=" + tochange[0] + "&checked=" + tochange[1] + "&timestamp=" + tochange[2]);
                        if (result.equals("1")) {
                            sendMessage("change status success");
                        } else {
                            sendMessage("failed change status");
                        }
                    } else if (cmd[0].equals("end")) {
                        sendMessage("end");
                    }
                } catch (ClassNotFoundException classnot) {
                    System.err.println("Data received in unknown format");
                }
            } while (!message.equals("end"));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    void sendMessage(String msg) {
        try {
            out.writeObject(msg);
            out.flush();
            System.out.println("server>" + msg);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public String urlReader(String url) throws MalformedURLException, IOException {
        URL dest = new URL(url);
        BufferedReader input = new BufferedReader(
                new InputStreamReader(dest.openStream()));
        String inputLine;
        while ((inputLine = input.readLine()) != null) {
            result += inputLine;
        }
        input.close();
        return result;
    }

    public static void main(String args[]) throws InterruptedException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8888);
            try {
                while (true) {
                    System.out.println("Waiting For Connection");
                    new Provider(serverSocket.accept());
                }
            } catch (IOException e) {
                System.out.println("Accept Failed");
            }
        } catch (IOException ex) {
            System.out.println("Could Not Listen on Port 8888");
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Could not close port: 2222.");
                System.exit(1);
            }
        }
    }
}