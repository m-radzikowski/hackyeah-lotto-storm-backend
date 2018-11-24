package push;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.ejb.Stateless;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

@Stateless
public class PushService {

	private static final String FCM_API = "https://fcm.googleapis.com/v1/projects/organic-zephyr-223511/messages:send";

	private final Client client = ClientBuilder.newClient();

	public void sendNotification(String pushToken, String title, String body) {
		Notification notification = new Notification(title, body);
		Message message = new Message(pushToken, notification);
		try {
			send(new Push(message));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void send(Push push) throws IOException {
		String accessToken = obtainGoogleCredential().getAccessToken();

		try {
			String result = client.target(FCM_API)
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.post(Entity.json(push), String.class);

			System.out.println("FCM push result: " + result);
		} catch (BadRequestException e) {
			System.err.println("Failed to send push notification: " + e.getResponse().readEntity(String.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private GoogleCredential obtainGoogleCredential() throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream authFile = classLoader.getResourceAsStream("firebase.json");
		GoogleCredential credential = GoogleCredential.fromStream(authFile);
		GoogleCredential scoped = credential.createScoped(
			Collections.singletonList(
				"https://www.googleapis.com/auth/firebase.messaging"
			)
		);
		scoped.refreshToken();
		return scoped;
	}

	@Setter
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public class Push {
		private Message message;
	}

	@Setter
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonRootName(value = "message")
	public class Message {
		private String token;
		private Notification notification;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	private static class Notification {
		private String title;
		private String body;
	}
}
