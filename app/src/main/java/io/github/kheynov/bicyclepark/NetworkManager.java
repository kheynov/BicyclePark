package io.github.kheynov.bicyclepark;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

import static io.github.kheynov.bicyclepark.Util.Constants.infoTag;

class NetworkManager{
    private String hostname;
    private int port;
    private Scanner inStream;
    private PrintWriter outStream;
    private Socket clientSocket;

    private boolean isResponseUpdated = false;

    NetworkManager(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    private ServerResponse request(final String message) {
        Log.i(infoTag, "Sending request : " + message);
        final ServerResponse response = new ServerResponse();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket(hostname, port);
                    inStream = new Scanner(clientSocket.getInputStream());
                    outStream = new PrintWriter(clientSocket.getOutputStream());

                    if (clientSocket != null) {
                        outStream.write(new String(message.getBytes(), StandardCharsets.UTF_8));
                        outStream.flush();
                    }

                    while (!inStream.hasNext());
                    Log.i(infoTag, "Has new message");
                    String receive = inStream.nextLine();
                    Log.i(infoTag, receive);
                    try {
                        JSONObject object = new JSONObject(receive);
                        Log.i(infoTag, object.toString());
                        if (object.has("type")) {
                            response.setType(object.getString("type"));
                        }
                        if (object.has("response")) {
                            response.setMsg((String) object.get("response"));
                        }
                        if (object.has("status")) {
                            response.setStatus((String) object.get("status"));
                        }
                        if (object.has("park_states")) {
                            ArrayList<String> parks_states = new ArrayList<>();
                            JSONArray array = object.getJSONArray("park_states");
                            for (int i = 0; i < array.length(); i++) {
                                parks_states.add(array.getString(i));
                            }
                            response.setParksState(parks_states);
                        }
                        isResponseUpdated = true;
                        handleResponse(response);

                    } catch (JSONException e) {
                        close();
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    close();
                }

            }
        }).start();
        return response;
    }

    ServerResponse getParksStatus() {
        return request("/status");
    }

    void openPark(int id, String password) {
        request("/open/" + id + "/" + password);
    }

    void closePark(int id, String hash) {
        request("/close/" + id + "/" + hash);

    }

    boolean isResponseUpdated() {
        return isResponseUpdated;
    }

    private void close() {
        inStream.close();
        outStream.close();
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleResponse(ServerResponse response) {
        Log.i(infoTag, "------------------------------");
        Log.i(infoTag + "  type: ", response.getType());
        Log.i(infoTag + "  result: ", response.getMsg());
        Log.i(infoTag + "  parks count: ", String.valueOf(response.getParks_count()));
        Log.i(infoTag + "  status: ", response.getStatus());
        Log.i(infoTag + "  parks states: ", response.getParksState().toString());
        Log.i(infoTag, "------------------------------");
    }

    void clearResponseUpdated() {
        isResponseUpdated = false;
    }
}
