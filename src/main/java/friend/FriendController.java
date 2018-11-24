package friend;

import entity.Player;
import repository.PlayerRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Stateless
@Path("/friend")
public class FriendController {

	@Inject
	private PlayerRepository playerRepository;

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
	public void sendNotification(NotifyRequestDto dto) {
		// TODO Send notify
		friendNotifications.addNotification(dto.getUserId(), dto.getFriendId());
	}
}
