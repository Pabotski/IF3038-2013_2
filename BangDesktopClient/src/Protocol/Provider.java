package Protocol;

import java.io.*;
import java.net.*;

public class Provider extends Thread {

    protected Socket clientSocket;
    ObjectOutputStream out;
    ObjectInputStream in;
    String message;
    String[] cmd;
    String result = "";

    Provider(Socket clientSoc) {
        clientSocket = clientSoc;
        start();
    }

    public void run() {
        try {
            System.out.println("Connection received from " + clientSocket.getInetAddress().getHostName());
            //3. get Input and Output streams
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(clientSocket.getInputStream());
            sendMessage("Connection successful");
            //4. The two parts communicate via the input and output streams
            do {
                try {
                    result = "";
                    message = (String) in.readObject();
                    System.out.println("client>" + message);
                    cmd = message.split("~");
                    System.out.println(message);
                    if (cmd[0].equals("login")) {
                        result = urlReader("http://nicholasrio.ap01.aws.af.cm/rest/authentication?usr=" + cmd[1] + "&psw=" + cmd[2]);
                        System.out.println("result nih : " + result + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        if (result.equals("1")) {
                            sendMessage("Login:1");
                        } else {
                            sendMessage("Login:0");
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
                        System.out.println(message);
                        listchange = cmd[1].split(";");
                        boolean error = false;
                        for (int i = 0; i < listchange.length; i++) {
                            String[] tochange;
                            tochange = listchange[i].split(",");
                            result = urlReader("http://nicholasrio.ap01.aws.af.cm/sync/taskStatusService.php?idtask=" + tochange[0] + "&checked=" + tochange[1] + "&timestamp=" + tochange[2]);
                        }
                        sendMessage("sync:1");
                    } else if (cmd[0].equals("changestatus")) {
                        String[] tochange;
                        tochange = cmd[1].split(",");
                        result = urlReader("http://nicholasrio.ap01.aws.af.cm/sync/taskStatusService.php?idtask=" + tochange[0] + "&checked=" + tochange[1] + "&timestamp=" + tochange[2]);
                        System.out.println("http://nicholasrio.ap01.aws.af.cm/sync/taskStatusService.php?idtask=" + tochange[0] + "&checked=" + tochange[1] + "&timestamp=" + tochange[2]);
                        if (result.equals("1")) {
                            sendMessage("change:1");
                        } else {
                            sendMessage("change:0");
                        }
                    } else if (cmd[0].equals("dummy")) {
                        sendMessage("connected");
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
            //4: Closing connection
            System.out.println("Server thread closes connection");
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

    public static void main(String args[]) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(8888);
            System.out.println("Server socket created on port 8888.");

            try {
                while (true) {
                    System.out.println("Waiting for Connection.");
                    new Provider(serverSocket.accept());
                }
            } catch (IOException ex) {
                System.err.println("Accept connection failed.");
                System.exit(1);
            }
        } catch (IOException ex) {
            System.err.println("Could not listen on port 8888.");
            System.exit(1);
        } finally {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                System.err.println("Could not close port 8888.");
                System.exit(1);
            }
        }
    }
}