package ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import controller.StormController;
import friend.FriendNotifications;
import model.dto.CommandDto;
import model.dto.LotteryTicketDto;
import model.dto.StormDto;
import model.dto.WinnerDto;
import wallet.WalletBean;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ServerEndpoint("/storm")
@Singleton
public class WebSocket {
	private Set<Session> clientSessions = Collections.synchronizedSet(new HashSet<>());
	private Set<Session> serverSessions = Collections.synchronizedSet(new HashSet<>());
	private Set<Session> webSessions = Collections.synchronizedSet(new HashSet<>());
	private ObjectMapper objectMapper = new ObjectMapper();
	@Inject
	private StormController stormController;
	@Inject
	private WalletBean walletBean;
	@Inject
	private FriendNotifications friendNotifications;

	@OnOpen
	public void open(Session session) throws JsonProcessingException {
		if (session.getQueryString().equals("client")) {
			clientSessions.add(session);
		} else if (session.getQueryString().equals("server")) {
			serverSessions.add(session);
			List<StormDto> storms = stormController.generate(5);
			List<CommandDto> commands = storms.stream().map(storm -> new CommandDto(storm.getId(), CommandDto.Type.CREATE)).collect(Collectors.toList());
			session.getAsyncRemote().sendText(objectMapper.writeValueAsString(commands));
		} else if (session.getQueryString().equals("web")) {
			webSessions.add(session);
		}
	}

	@OnClose
	public void close(Session session) {
		if (session.getQueryString().equals("client")) {
			clientSessions.remove(session);
		} else if (session.getQueryString().equals("server")) {
			serverSessions.remove(session);
			stormController.removeAll();
		} else if (session.getQueryString().equals("web")) {
			webSessions.remove(session);
		}
	}

	@OnError
	public void onError(Throwable error) {
	}

	@OnMessage
	public void handleMessage(String message, Session session) throws IOException {
		if (session.getQueryString().equals("client")) {
			LotteryTicketDto lotteryTicketDto = objectMapper.readValue(message, LotteryTicketDto.class);
			StormDto stormDto = stormController.find(lotteryTicketDto.getLat(), lotteryTicketDto.getLng());

			if (stormDto != null) {
				stormDto.setCurrent(stormDto.getCurrent() + 1);
				stormController.updateCurrent(stormDto);
				if (stormDto.getCurrent().equals(stormDto.getMax())) {
					String winnerMsg = objectMapper.writeValueAsString(new WinnerDto(lotteryTicketDto, stormDto));
					clientSessions.forEach(clientSessions -> {
						clientSessions.getAsyncRemote().sendText(winnerMsg);
					});
					webSessions.forEach(webSession -> webSession.getAsyncRemote().sendText(winnerMsg));

					List<StormDto> storms = stormController.generate(1);
					List<CommandDto> commands = storms.stream().map(storm -> new CommandDto(storm.getId(), CommandDto.Type.CREATE)).collect(Collectors.toList());
					commands.add(new CommandDto(stormDto.getId(), CommandDto.Type.REMOVE));
					serverSessions.forEach(serverSession -> {
						try {
							serverSession.getAsyncRemote().sendText(objectMapper.writeValueAsString(commands));
						} catch (JsonProcessingException e) {
							e.printStackTrace();
						}
					});
					stormController.remove(stormDto.getId());
				}
				walletBean.spend1(lotteryTicketDto.getUserId());
				friendNotifications.benefitFriendIfAny(lotteryTicketDto.getUserId());
			}
		} else if (session.getQueryString().equals("server")) {
			List<StormDto> storms = objectMapper.readValue(message, new TypeReference<List<StormDto>>() {
			});
			storms.forEach(stormDto -> stormController.updatePosition(stormDto));

			Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
			sessions.addAll(clientSessions);
			sessions.addAll(webSessions);

			sessions.forEach(clientSession -> {
				try {
					clientSession.getAsyncRemote().sendText(objectMapper.writeValueAsString(stormController.getAll()));
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			});
		}
	}
}
