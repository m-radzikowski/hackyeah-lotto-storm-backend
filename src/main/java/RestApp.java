import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath(RestApp.BASE_PATH)
public class RestApp extends Application {

	public static final String BASE_PATH = "/rest/v1";
}
