package our.task.JettyWebSocket;
 
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
 
public class SocketHandler extends WebSocketHandler {
 
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(MySocket.class);
    }   
}
