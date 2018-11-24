package wallet;

import javax.ejb.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class WalletBean {

	/**
	 * Map of PLAYERS ID to WALLET VALUE.
	 */
	private final Map<Long, Long> wallet = new HashMap<>();

	public long addValue(long playerId, long value) {
		return wallet.merge(playerId, value, (old, added) -> old + added);
	}

	public long getValue(long playerId) {
		return wallet.getOrDefault(playerId, 0L);
	}

	public void spend1(long playerId) {
		wallet.computeIfPresent(playerId, (key, old) -> {
			if (old > 0) {
				return old - 1;
			} else {
				return 0L;
			}
		});
	}
}
