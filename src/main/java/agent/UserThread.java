package agent;

import common.Server;
import common.Transceiver;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by ali on 2/28/17.
 */
public class UserThread extends Thread {
    private ArrayList<Server> roots;
    private Socket socket;
    private RequestHandler reqHandler;


    UserThread(Socket socket, ArrayList<Server> roots) {
        this.socket = socket;
        reqHandler = new RequestHandler();
        this.roots = roots;
    }

    public void run() {
        System.out.println("New Thread!");
        String line;
        while (true) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while ((line = br.readLine()) != null) {

                    String type = reqHandler.parseType(line);
                    switch (type) {
                        case "search":
                            System.out.println("Search!");
                            JSONObject jo = reqHandler.createSearchJson();
                            String response = handleSearch(jo);
                            System.out.println(response);
                            break;
                        case "add":
                            System.out.println("Add");
                            break;
                        case "update":
                            System.out.println("Update");
                            break;
                        case "wrong":
                            System.out.println("Bad Input");
                            break;
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            break;
        }
        System.out.println("Thread Ended!");
    }

    private String handleSearch(JSONObject jo) throws IOException {
        String response = "";
        for (Server root :
                roots) {
            System.out.println("sending: " + jo.toString() + " to ip:" + root.ip + ":" + root.port);
            Transceiver agent = new Transceiver(root.ip, root.port);
            agent.send(jo.toString() + '\n');
            response = agent.receive();
            if (response != null)
                break;
        }
        return response;
    }
}
