package login;

import entity.Player;
import repository.PlayerRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Stateless
@Path("/login")
public class LoginController {

	@Inject
	private PlayerRepository playerRepository;

	@POST
	public LoginResponseDto login(LoginRequestDto dto) {
		Player player = playerRepository.findByUsername(dto.getUsername())
			.orElseGet(() -> {
				Player p = new Player();
				p.setUsername(dto.getUsername());
				p.setPushToken(dto.getPushToken());
				return playerRepository.saveAndFlushAndRefresh(p);
			});

		if (!player.getPushToken().equals(dto.getPushToken())) {
			player.setPushToken(dto.getPushToken());
		}

		return new LoginResponseDto(player.getId());
	}
}
