/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Protocol.Requester;
import UI.LoginScreen;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Jeremy Joseph Hanniel
 */
public class BangDesktop {

    private static Requester requester;
    public static boolean onMain;
    public static String username;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        requester = new Requester();

        LoginScreen.init(args);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (onMain) {
                    System.out.println("U P D A T E");
                    requester.Connect("dummy");
                    if (requester.getResponse() != null) {
                        System.out.println("CONNECTED 1");
                        System.out.println("readlog = "+readLog());
                        if (!"".equals(readLog())) {
                            requester.Connect("dummy");
                            if (requester.getResponse() != null) {
                                System.out.println("CONNECTED 2");
                                requester.Connect("sync~" + readLog());
                                System.out.println(requester.getResponse());
                                if (requester.getResponse().split(":")[1].equals("1")) {
                                    clearLog();
                                }
                            }
                        } else {
                        }
                    }
                }
            }
        }, 5000, 3000);
    }

    public static void clearLog() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(username + ".txt"))) {
            bw.write("");
            bw.close();
        } catch (IOException ex) {
            ex.getMessage();
        };
    }

    public static String readLog() {
        String result = "";
        try (BufferedReader br = new BufferedReader(new FileReader(username + ".txt"))) {
            String temp = null;
            while (!(temp = br.readLine()).isEmpty()) {
                result += temp + ";";
            }
            
            br.close();
        } catch (IOException ex) {
            ex.getMessage();
        } finally {
            return result;
        }
    }
}
