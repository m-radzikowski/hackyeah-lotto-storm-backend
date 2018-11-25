package wallet;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Stateless
@Path("/wallet")
public class WalletController {

	@Inject
	private WalletBean wallet;

	@GET
	@Path("/{id}")
	public WalletStateDto getBalance(@PathParam("id") long playerId) {
		return new WalletStateDto(wallet.getValue(playerId));
	}

	@GET
	@Path("/{id}/add/{amount}")
	public WalletStateDto addValue(@PathParam("id") long playerId, @PathParam("amount") long amount) {
		return new WalletStateDto(wallet.addValue(playerId, amount));
	}
}
