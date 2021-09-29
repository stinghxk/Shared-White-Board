import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ServerThread extends Thread
{
    DataInputStream dis;
    DataOutputStream dos;
    OutputStream outputStream;
    InputStream inputStream;

    Socket remoteClient;
    private String myName;


    Map<String, ArrayList<Message>> connectedClients;; // keep track of all the other clients connected to the Server
    ArrayList<String> drawingLog;


    public ServerThread(Socket remoteClient, Map connectedClients)
    {
        this.remoteClient = remoteClient;
        this.connectedClients = connectedClients;
        try {
            this.dis = new DataInputStream(remoteClient.getInputStream());
            this.dos = new DataOutputStream(remoteClient.getOutputStream());
            this.inputStream = remoteClient.getInputStream();
            this.outputStream = remoteClient.getOutputStream();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void run()
    {
        while(true) // main protocol decode loop
        {
            try
            {
                InputStream socketIn = inputStream;
                ObjectInputStream in = new ObjectInputStream(socketIn);
//            System.out.println(socket);

                OutputStream socketOut = outputStream;
                ObjectOutputStream out = new ObjectOutputStream(socketOut);

                Message clientMessage = (Message) in.readObject();
                String username = clientMessage.getUsername();
                if (myName == null)
                    myName = username;

                if (connectedClients.containsKey(username)) {
                    out.writeObject(new Message(username, Message.JOIN_RESPONSE, null));
                    throw new Exception("There is a user with this username.");
                } else {
                    Sender sender = new Sender(out, username);
                    connectedClients.put(username, new ArrayList<>());
                    Thread senderThread = new Thread(sender);
                    senderThread.start();
                }

                do {
                    int msgType = clientMessage.getType();
                    System.out.println("msgType: " + msgType );
                    switch(msgType)
                    {
                        case Message.DRAW_OPERATION:
                            drawingLog.add(clientMessage.content());
                            
                            break;
                        default:
                            break;
                    }

                } while ((clientMessage = (Message) in.readObject()) != null);
            }
            catch (IOException e)
            {

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public String DrawingSend (ArrayList<String> drawingLog)
    {
        StringBuilder sb = new StringBuilder();
        for(String s : drawingLog)
        {
            sb.append(s);
            sb.append("-");
        }
        return sb.toString();
    }


}
