package friend;

import entity.Player;
import push.PushService;
import repository.PlayerRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Stateless
@Path("/friend")
public class FriendController {

	@Inject
	private PlayerRepository playerRepository;

	@Inject
	private PushService pushService;

	@Inject
	private FriendNotifications friendNotifications;

	@POST
	@Path("/find")
	public FriendResponseDto find(FriendRequestDto dto) {
		Player player = playerRepository.findByUsername(dto.getUsername())
			.orElseThrow(NotFoundException::new);

		return new FriendResponseDto(player.getId());
	}

	@POST
	@Path("/notify")
	public Response sendNotification(NotifyRequestDto dto) {
		Player sender = playerRepository.findById(dto.getUserId()).orElseThrow(NotFoundException::new);
		Player friend = playerRepository.findById(dto.getFriendId()).orElseThrow(NotFoundException::new);

		pushService.sendNotification(friend.getPushToken(), sender.getUsername() + " zaprasza Cię do gry!", dto.getText());

		friendNotifications.addNotification(dto.getUserId(), dto.getFriendId());

		return Response.ok().build();
	}
}
