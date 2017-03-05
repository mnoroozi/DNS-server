package agent;

import common.Server;
import common.Transceiver;
import json.RequestHandler;
import json.Response;
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
    private ServerRepository serverRepository;


    UserThread(Socket socket, ArrayList<Server> roots) {
        this.socket = socket;
        this.reqHandler = new RequestHandler();
        this.roots = roots;
        this.serverRepository = ServerRepository.getRepository();
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
                            Response response = handleSearch(reqHandler);
                            sendResultToUser(response);
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

    private Response handleSearch(RequestHandler req) throws IOException, JSONException {
        Response r = checkCatchedRepository(req);
        if(r != null)
            return r;

        JSONObject jo = reqHandler.createSearchJson();
        for (Server root: roots) {
            if(reqHandler.getSearchType().equals("iterative"))
                r = iterativeSearch(jo, root.port);
            else
                r =  defaultSearch(jo, root.port);

            if(r != null && r.isFinal())
                break;
        }
        if(r != null && r.isFound())
            serverRepository.store(new Server(req.getRequest(), r.getResponse(), 0, r.getValidTime()));

        return r;
    }

    private Response checkCatchedRepository(RequestHandler req) throws JSONException {
        if(serverRepository.contains(req.getRequest())) {
            Server s = serverRepository.get(req.getRequest());
            return new Response(s.ip, true, true, s.validTime);
        } else
            return null;
    }

    private Response defaultSearch(JSONObject jo , int port) throws IOException {
        System.out.println("recursive search!!!");
        String response = "";
        Transceiver agent = new Transceiver("localhost", port);
        agent.send(jo.toString() + '\n');
        response = agent.receive();

        try {
            return new Response(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Response iterativeSearch(JSONObject jo , int port) throws IOException, JSONException {
        System.out.println("iterative search!!!");
        String response = "";
        Response resp = defaultSearch(jo, port);

        if(resp != null) {
            if(!resp.isFinal()) {
                return iterativeSearch(jo, Integer.parseInt(resp.getResponse()));
            } else if(!resp.isFound()) {
                return new Response("",true, false,0);
            }
        }

        return resp;
    }

    private void sendResultToUser(Response response) throws IOException {
        Transceiver t = new Transceiver(this.socket);
        if(response.isFound())
            t.send(response.getResponse() + '\n');
        else
            t.send("Not Found!\n");
    }
}
