package friend;

import javax.ejb.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class FriendNotifications {

	/**
	 * Map of notification RECEIVERS to SENDERS.
	 */
	private final Map<Long, List<Long>> notifications = new HashMap<>();

	public void addNotification(long fromId, long toId) {
		this.notifications.putIfAbsent(toId, new ArrayList<>());
		this.notifications.get(toId).add(fromId);
	}
}
