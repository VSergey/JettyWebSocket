package our.task.JettyWebSocket;
 
import java.io.IOException;
import java.util.*;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
 
@WebSocket
public class MySocket {
    private static final String s_login = "login by name::";
    private static final String s_message_delimiter = "##";
    private static final Map<String, Session> o_users = new HashMap<>();
    private Session o_session;
    private String o_user;

    @OnWebSocketConnect
    public void onConnect(Session p_session) {
        log("Connect: " + p_session.getRemoteAddress().getAddress());
        try {
            o_session = p_session;
            o_user = o_session.toString();
            o_users.put(o_user, o_session);
            p_session.getRemote().sendString("Connected to chat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     
    @OnWebSocketMessage
    public void onText(String p_message) {
        log("text: " + p_message);
        int messageSelimiter = p_message.indexOf(s_message_delimiter);
        if(messageSelimiter != -1) {
            String user = p_message.substring(0, messageSelimiter);
            String message = p_message.substring(messageSelimiter+2);
            sendForAll(user, message);
        } else if(p_message.startsWith(s_login)) {
            o_users.remove(o_user);
            o_user = p_message.substring(s_login.length());
            o_users.put(o_user, o_session);
            sendForAll(o_user, o_user + " was connected");
            log("User '" + o_user + "' was register");
        } else {
            sendMessage(o_session, "Server", "Unknow message : "+p_message);
        }
    }

    private void sendForAll(String p_user, String p_message) {
        //send for all users message
        for(Map.Entry<String,Session> entry : o_users.entrySet()) {
            if(entry.getKey().equalsIgnoreCase(p_user)) {
                sendMessage(entry.getValue(), "                    ", p_message);
            } else {
                sendMessage(entry.getValue(), p_user, p_message);
            }
        }
    }

    private void sendMessage(Session p_session, String p_user, String p_message) {
        try {
            p_session.getRemote().sendString(p_user + " > "+p_message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketClose
    public void onClose(int p_statusCode, String p_reason) {
        o_users.remove(o_user);
        sendForAll(o_user, o_user + " was disconnected");
        log(o_user + " Close connection: statusCode=" + p_statusCode + ", reason=" + p_reason);
    }

    private void log(String p_msg) {
        System.out.println(p_msg);
    }
}
