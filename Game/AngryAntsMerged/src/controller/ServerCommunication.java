package controller;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerCommunication {

	AntController controller;

	public ServerCommunication(AntController antcontroller) {
		controller = antcontroller;
	}

	public String sendRequest(String typeRequest, String user, String password) {
		if (typeRequest.equals("LOGIN")) {
			String response = excutePost(
					"http://cgi.cs.arizona.edu/cgi-bin/projects/angryants/php/processlogin.php",
					"username=" + user + "&" + "password=" + password);
			System.out.println(response);
			if (response.indexOf("Invalid login information!") >= 0) {
				return "LoginIncorrect";
			}
			return response;
		} else if (typeRequest.equals("CREATE")) {
			String response = excutePost(
					"http://cgi.cs.arizona.edu/cgi-bin/projects/angryants/php/adduser.php", "username="
							+ user + "&" + "password=" + password);
			if (response.indexOf("That username is already taken.") >= 0) {
				return "AlreadyExists";
			}
			System.out.println(response);
			return response;

		}else if(typeRequest.equals("PICK")){
			String response = excutePost(
					"http://cgi.cs.arizona.edu/cgi-bin/projects/angryants/php/pickant.php", "");
			return response;
		}
		return "";
	}
	
	public String uploadFile(String contents, String user, String vidID, String antId, String vidId2){
		return excutePost("http://cgi.cs.arizona.edu/cgi-bin/projects/angryants/php/handleupload.php?vid="+vidID +"&aid=" + antId + "&vid2="+vidId2 +"&credential=" + user, contents);
	}

	public static String excutePost(String targetURL, String urlParameters) {
		URL url;
		HttpURLConnection connection = null;
		try {
			// Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();

		} catch (Exception e) {

			e.printStackTrace();
			return null;

		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
	}
}
