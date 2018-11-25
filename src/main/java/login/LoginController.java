package login;

import controller.StormController;
import entity.Player;
import model.dto.StormDto;
import repository.PlayerRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Stateless
@Path("/login")
public class LoginController {

	@Inject
	private PlayerRepository playerRepository;

	@Inject
	private StormController sc;

	@GET
	public void test() {
		StormDto storm = sc.generate(1).get(0);

		storm.setLat(52.2922104);
		storm.setLng(21.0023798);

		sc.updatePosition(storm);

		System.out.println("EXPO: " + sc.find(52.2922104, 21.0023798));
		System.out.println("INSIDE: " + sc.find(52.329964, 21.240653));
		System.out.println("OUTSIDE: " + sc.find(52.333712, 21.398141));
		System.out.println("OUTSIDE 86: " + sc.find(52.354983, 22.157878));
	}

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
