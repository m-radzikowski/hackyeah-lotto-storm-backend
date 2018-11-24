package login;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Stateless
@Path("/login")
public class LoginController {

	@GET
	public String hello() {
		return "Hello LOTTO!";
	}

	@POST
	public LoginResponseDto login(LoginRequestDto dto) {
		System.out.println("Login user: " + dto.getUsername());
		return new LoginResponseDto(1);
	}
}
