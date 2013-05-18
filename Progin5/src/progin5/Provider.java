package progin5;

import java.io.*;
import java.net.*;

public class Provider {

    ServerSocket providerSocket;
    Socket connection = null;
    ObjectOutputStream out;
    ObjectInputStream in;
    String message;
    String[] cmd;
    String result = "";

    Provider() {
    }

    void run() {
        try {
            //1. creating a server socket
            providerSocket = new ServerSocket(8888, 10);
            //2. Wait for connection
            System.out.println("Waiting for connection");
            connection = providerSocket.accept();
            System.out.println("Connection received from " + connection.getInetAddress().getHostName());
            //3. get Input and Output streams
            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());
            sendMessage("Connection successful");
            //4. The two parts communicate via the input and output streams
            do {
                try {
                    message = (String) in.readObject();
                    System.out.println("client>" + message);
                    cmd = message.split(",");
                    if (cmd[0].equals("login")) {
                        result = urlReader("http://nicholasrio.ap01.aws.af.cm/rest/authentication?usr=ArieDoank&psw=12345123");
                        if (result.equals("1")) {
                            sendMessage("sok login");
                        } else {
                            sendMessage("ga boleh login");
                        }
                    } else if (cmd[0].equals("showtask")) {
                        result = urlReader("http://nicholasrio.ap01.aws.af.cm/rest/showTask?q=0&username=ArieDoank");
                        if(result.equals(""))
                            sendMessage(result);
                        else{
                            sendMessage("No Task");
                        }
                    } else if (cmd[0].equals("sync")) {
                        result = urlReader("http://nicholasrio.ap01.aws.af.cm/sync/taskStatusService.php?idtask=20&checked=1&timestamp=130517134000");
                        sendMessage("sync");
                    } else if (cmd[0].equals("logout")) {
                        sendMessage("logout");
                    }
                } catch (ClassNotFoundException classnot) {
                    System.err.println("Data received in unknown format");
                }
            } while (!message.equals("logout"));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            //4: Closing connection
            try {
                in.close();
                out.close();
                providerSocket.close();
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
            System.out.println(result);
        }
        input.close();
        return result;
    }

    public static void main(String args[]) {
        Provider server = new Provider();
        while (true) {
            server.run();
        }
    }
}