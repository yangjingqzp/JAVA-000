
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class TestHttpClient {

	public static void main(String[] args) {
		HttpUriRequest request = null;
		String url = "http://localhost:8088/api/hello";
		try {
			request = RequestBuilder.get().setUri(new URI(url))
				.build();
		} catch (URISyntaxException e) {
			System.out.println("URISyntaxException: "+ e.getMessage());
		}
		CloseableHttpClient httpclient = HttpClients.custom().build();
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();
			System.out.println("response String: "+ EntityUtils.toString(entity));
		} catch (IOException e) {
			System.out.println("IOException: "+e.getMessage());
		} finally {
			try {
				response.close();
				httpclient.close();
			} catch (IOException e) {
				System.out.println("IOException: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
