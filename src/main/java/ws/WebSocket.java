package ws;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.dto.StormDto;

import javax.ejb.Singleton;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

@ServerEndpoint("/storm")
@Singleton
public class WebSocket {
    private Set<Session> clientSessions = Collections.synchronizedSet(new HashSet<>());
    private Set<Session> serverSessions = Collections.synchronizedSet(new HashSet<>());
    private ObjectMapper objectMapper = new ObjectMapper();
    private List<StormDto> storms = Collections.synchronizedList(new ArrayList<>());

    @OnOpen
    public void open(Session session) {
        if (session.getQueryString().equals("client")) {
            clientSessions.add(session);
        } else {
            serverSessions.add(session);
        }
    }

    @OnClose
    public void close(Session session) {
        if (session.getQueryString().equals("client")) {
            clientSessions.remove(session);
        } else {
            serverSessions.remove(session);
        }
    }

    @OnError
    public void onError(Throwable error) {
    }

    @OnMessage
    public void handleMessage(String message, Session session) throws IOException {
        if (session.getQueryString().equals("client")) {

        } else {
            storms = objectMapper.readValue(message, new TypeReference<List<StormDto>>(){});
        }

    }
}
