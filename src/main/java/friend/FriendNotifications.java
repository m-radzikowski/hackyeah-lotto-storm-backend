package friend;

import push.PushService;
import repository.PlayerRepository;
import wallet.WalletBean;

import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.*;

@Singleton
public class FriendNotifications {

	@Inject
	private PlayerRepository playerRepository;

	@Inject
	private PushService pushService;

	@Inject
	private WalletBean wallet;

	/**
	 * Map of notification RECEIVERS to SENDERS.
	 */
	private final Map<Long, List<Long>> notifications = new HashMap<>();

	public void addNotification(long fromId, long toId) {
		this.notifications.putIfAbsent(toId, new ArrayList<>());
		this.notifications.get(toId).add(fromId);
	}

	public void benefitFriendIfAny(long playerId) {
		Optional.ofNullable(notifications.remove(playerId))
			.ifPresent(friends -> friends.forEach(friendId -> {
				playerRepository.findById(friendId).ifPresent(friend -> {
					wallet.addValue(friend.getId(), 1);
					pushService.sendNotification(friend.getPushToken(), "Otrzymujesz DARMOWY kupon", friend.getUsername() + " zagrał dzięki Twojemu zaproszeniu, otrzymujesz 1 darmowy kupon!");
				});
			}));
	}
}
