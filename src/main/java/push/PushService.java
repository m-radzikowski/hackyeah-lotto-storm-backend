package push;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Stateless
public class PushService {

//	private static final String FCM_API = "https://fcm.googleapis.com/v1/projects/expo-xxi/messages:send";
//
//	private ParticipantRepository participantRepository;
//
//	@Inject
//	public PushService(ParticipantRepository participantRepository) {
//		this.participantRepository = participantRepository;
//	}
//
//	public void sendNotificationParticipants(Iterable<Participant> participants, String message) {
//		participants.forEach(participant -> {
//			if (participant.getToken() != null && !participant.getToken().trim().isEmpty()) {
//				try {
//					Notification notification = new Notification("Expo", message);
//					Push push = new Push(participant.getToken(), notification);
//					String response = sendNotification(push);
//
//					if (response != null) {
//						System.out.println("Sent push to Firebase with response: " + response);
//					}
//				} catch (JsonProcessingException e) {
//					System.out.println(e.toString());
//				} catch (HttpClientErrorException e) {
//					if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
//						System.out.println("Participant token not found on firebase - updating participant.");
//
//						participant.setToken(null);
//						participantRepository.save(participant);
//					} else {
//						System.out.println(e.toString());
//					}
//				}
//			} else {
//				System.out.println("Participant " + participant.getFirstName() + " " + participant.getLastName() + " has no registered device.");
//			}
//		});
//	}
//
//	public String sendNotification(Push push) throws JsonProcessingException, HttpClientErrorException {
//		String response = null;
//
//		ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
//		String json = objectMapper.writeValueAsString(push);
//
//		HttpEntity<String> request = new HttpEntity<>(json);
//		try {
//			CompletableFuture<String> pushNotification = this.send(request);
//			CompletableFuture.allOf(pushNotification).join();
//
//			response = pushNotification.get();
//		} catch (InterruptedException | ExecutionException | IOException e) {
//			e.printStackTrace();
//		}
//		return response;
//	}
//
//	@Async
//	CompletableFuture<String> send(HttpEntity<String> entity) throws IOException, HttpClientErrorException {
//		RestTemplate restTemplate = new RestTemplate();
//
//		InputStream stream = new ClassPathResource("expo-xxi-firebase.json").getInputStream();
//		GoogleCredential googleCred = GoogleCredential.fromStream(stream);
//		GoogleCredential scoped = googleCred.createScoped(
//			Collections.singletonList(
//				"https://www.googleapis.com/auth/firebase.messaging"
//			)
//		);
//		scoped.refreshToken();
//
//		ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
//		interceptors.add(new HeaderRequestInterceptor("Authorization", "Bearer " + scoped.getAccessToken()));
//		interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));
//		restTemplate.setInterceptors(interceptors);
//
//		String firebaseResponse = restTemplate.postForObject(FCM_API, entity, String.class);
//		return CompletableFuture.completedFuture(firebaseResponse);
//	}
}
