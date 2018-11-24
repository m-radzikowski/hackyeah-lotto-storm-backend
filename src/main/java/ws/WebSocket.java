package ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import controller.StormController;
import model.dto.CommandDto;
import model.dto.LotteryTicketDto;
import model.dto.StormDto;
import model.dto.WinnerDto;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@ServerEndpoint("/storm")
@Singleton
public class WebSocket {
    private Set<Session> clientSessions = Collections.synchronizedSet(new HashSet<>());
    private Set<Session> serverSessions = Collections.synchronizedSet(new HashSet<>());
    private ObjectMapper objectMapper = new ObjectMapper();
    @Inject
    private StormController stormController;
//    @Resource
//    private ManagedScheduledExecutorService scheduledExecutorService;

    @OnOpen
    public void open(Session session) throws JsonProcessingException {
        if (session.getQueryString().equals("client")) {
            clientSessions.add(session);
        } else {
            serverSessions.add(session);
            List<StormDto> storms = stormController.generate(1);
            List<CommandDto> commands = storms.stream().map(storm -> new CommandDto(storm.getId(), CommandDto.Type.CREATE)).collect(Collectors.toList());
            session.getAsyncRemote().sendText(objectMapper.writeValueAsString(commands));
//            scheduledExecutorService.scheduleAtFixedRate(this::send, 0, 5, TimeUnit.SECONDS);
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
            LotteryTicketDto lotteryTicketDto = objectMapper.readValue(message, LotteryTicketDto.class);
            StormDto stormDto = stormController.find(lotteryTicketDto.getLng(), lotteryTicketDto.getLat());
            stormDto.setCurrent(stormDto.getCurrent() + 1);
            stormController.updateCurrent(stormDto);
            if (stormDto.getCurrent().equals(stormDto.getMax())) {
                session.getAsyncRemote().sendText(objectMapper.writeValueAsString(new WinnerDto(lotteryTicketDto, stormDto)));
                stormController.remove(stormDto.getId());
                List<StormDto> storms = stormController.generate(1);
                List<CommandDto> commands = storms.stream().map(storm -> new CommandDto(storm.getId(), CommandDto.Type.CREATE)).collect(Collectors.toList());
                commands.add(new CommandDto(stormDto.getId(), CommandDto.Type.REMOVE));
                session.getAsyncRemote().sendText(objectMapper.writeValueAsString(commands));
            }
        } else {
            List<StormDto> storms = objectMapper.readValue(message, new TypeReference<List<StormDto>>(){});
            storms.forEach(stormDto -> stormController.updatePosition(stormDto));
            clientSessions.forEach(clientSession -> {
                try {
                    clientSession.getAsyncRemote().sendText(objectMapper.writeValueAsString(storms));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
