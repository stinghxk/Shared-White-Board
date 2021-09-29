import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

public class ServerController
{
    private StringProperty serverIPStr = new SimpleStringProperty();
    private StringProperty serverPortStr = new SimpleStringProperty();


    ServerSocket serverSocket;
    static Map<String, ArrayList<Message>> connectedClients = new HashMap<>();


    @FXML
    public TextField serverIP;

    @FXML
    public TextField serverPort;

    @FXML
    private TextArea systemLog;

    public ServerController(String serverIP, String serverPort)
    {
        if ( serverIP != null)
            serverIPStr.set(serverIP);
        else
        {
            try
            {
                serverIPStr.set(InetAddress.getLocalHost().getHostAddress().toString());
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
            }
        }
        serverPortStr.set(serverPort);
    }

    public void initialize()
    {

        serverIP.setText(serverIPStr.get());
        serverPort.setText(serverPortStr.get());

        // construct ServerSocket
        try {
            serverSocket = new ServerSocket(5000);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println(serverSocket.toString());

        //accept new clients ever 100ms through start()
        java.util.Timer t = new java.util.Timer();
        t.schedule(new TimerTask() {

            @Override
            public void run() {
                start();
            }
        }, 100, 100);

    }

    public void start()
    {
        try
        {
            serverSocket = new ServerSocket(Integer.valueOf(serverPortStr.getValue()));

            systemLog.setText("Server is opened,listening to port: " + serverPortStr.getValue());

            try
            {

                    //Listen to client requests and start a thread to process
                    Socket socket = serverSocket.accept();
                    ServerThread thread = new ServerThread(socket,connectedClients);

                    thread.start();
//                    connectedClients.add(thread);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }



    public TextArea getSystemLog()
    {
        return systemLog;
    }

    public void setSystemLog(TextArea systemLog)
    {
        this.systemLog = systemLog;
    }


}
