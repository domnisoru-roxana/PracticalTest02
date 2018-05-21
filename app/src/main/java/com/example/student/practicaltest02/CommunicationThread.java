package com.example.student.practicaltest02;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by student on 21.05.2018.
 */

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");
            String command = bufferedReader.readLine();

            if (command == null || command.length() == 0) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Received command from cl: " + command);

            HashMap<String, AlarmInfo> data = serverThread.getData();
            String ipAddress = socket.getInetAddress().toString();

            switch (command) {
                case Constants.SET:
                    String arg = bufferedReader.readLine();
                    if (arg== null || arg.length() == 0) {
                        Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client");
                        return;
                    }
                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] Received arg from cl: " + arg);
                    String tokens[] = arg.split(",");
                    String hour = tokens[0];
                    String min = tokens[1];

                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] Received arg from cl: " + hour + " " + min);
                    data.put(ipAddress, new AlarmInfo(Integer.valueOf(hour), Integer.valueOf(min)));

                    break;
                case Constants.RESET:
                    if (data.containsKey(ipAddress)) {
                        data.remove(ipAddress);
                    }
                    break;
                case Constants.POLL:
                    String response = null;
                    if (data.containsKey(ipAddress)) {
                        response = "active";
                        Socket socketTime = new Socket("utcnist.colorado.edu", 13);
                        BufferedReader bufferedReaderTime = Utilities.getReader(socketTime);

                        String line = bufferedReaderTime.readLine();
                        Log.i(Constants.TAG, "read line: " + line);
                        line = bufferedReaderTime.readLine();
                        Log.i(Constants.TAG, "read line: " + line);

                        String tokensTime[] = line.split(" ");

                        String time = tokensTime[2];

                        Log.i(Constants.TAG, "read line time : " + time);

                        String token2[] = time.split(":");

                        String h = token2[0];
                        String m = token2[1];

                        Log.i(Constants.TAG, "read line time : " + h + "      " + m);

                        AlarmInfo alarmInfo = data.get(ipAddress);
                        if (alarmInfo.getHour() > Integer.valueOf(h)) {
                            response = "active";
                        }
                        else if (alarmInfo.getHour() == Integer.valueOf(h) && alarmInfo.getMinute() > Integer.valueOf(m)) {
                            response = "active";
                        }
                        else {
                            response = "inactive";
                        }

                        socketTime.close();
                    }
                    else {
                        response = "none";
                    }
                    printWriter.println(response);
                    printWriter.flush();
                    break;
            }

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
