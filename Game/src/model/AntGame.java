/**
 * @author Paul Shen
 * 
 * Stores the information about the game: the current user, all the users that have played the game, the metadata of all the
 * videos.  Provides the methods for writing paths to file, saving and loading users, and changing users.
 */

package model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public class AntGame extends Observable {
	
	private User currUser;
	//private List<User> users;
	private LinkedList<Video> videos;
	
//	public static void main(String[] args) {
//		AntGame game = new AntGame("");
//	}
	
	/**
	 * No user specified, set current user to default.
	 * @throws FileNotFoundException 
	 */
	public AntGame(String userName) {
		// Read in video information from text file with the format for each video:
		// <video id>,<video name>,<video file name>,<alpha width>,<alpha height>,<scaled width>,<max frames>
		// <start1  X> <start1  Y>,<start2 X> <start2 Y>,...,<start10 X> <start10 Y>,
		// <ant #1 id> <ant #1 start X>, <ant #1 start Y>, <ant #1 end X>, <ant #1 end Y>,
		// <ant #2 id> <ant #2 start X>, <ant #2 start Y>, <ant #2 end X>, <ant #2 end Y>,
		// .
		// .
		// $
		// <video id>...
		videos = new LinkedList<Video>();

		ArrayList<Ant> ants = new ArrayList<Ant>();
		String line, name, fileName;
		int id, alphaWidth, alphaHeight, maxFrames, start, end, antId, antStartX, antStartY, antEndX, antEndY;
		float alphaFrameRate;
		name = "";
		fileName = "";
		id = -1;
		alphaWidth = -1;
		alphaHeight = -1;
		maxFrames = -1;
		alphaFrameRate = (float) -1.0;
		
		InputStream is = null;
		BufferedReader br = null;
		boolean onFirstLn = true;	// reading first line of a video's info
		
		try {
			is = getClass().getResourceAsStream("/video_info.txt");
			br = new BufferedReader(new InputStreamReader(is));
			
			// Parse video information line by line
			while ((line = br.readLine()) != null) {
				// Read first line of one video's information
				if (onFirstLn) {
					start = 0;				// start and end index of current field being read
					end = 0;
					
					end = line.indexOf(',', start);
					id = Integer.parseInt(line.substring(start, end));
					
					start = end + 1;		// move start index past comma delimiter
					end = line.indexOf(',', end + 1);
					name = line.substring(start, end);
					
					start = end + 1;
					end = line.indexOf(',', end + 1);
					fileName = line.substring(start, end);
					
					start = end + 1;
					end = line.indexOf(',', end + 1);
					alphaWidth = Integer.parseInt(line.substring(start, end));
					
					start = end + 1;
					end = line.indexOf(',', end + 1);
					alphaHeight = Integer.parseInt(line.substring(start, end));
					
					start = end + 1;
					end = line.indexOf(',', end + 1);
					maxFrames = Integer.parseInt(line.substring(start, end));
					
					start = end + 1;
					end = line.indexOf(',', end + 1);
					alphaFrameRate = Float.parseFloat(line.substring(start, end));
					
					onFirstLn = false;
				}
				else if (line.charAt(0) == '$') {
					// add video
					videos.add(new Video(id, name, fileName, alphaWidth, alphaHeight, maxFrames, 
							alphaFrameRate, ants));
					ants = new ArrayList<Ant>();
					onFirstLn = true;
				}
				else {
					start = 0;
					
					end = line.indexOf(',');
					antId = Integer.parseInt(line.substring(start, end));
					
					start = end + 1;
					end = line.indexOf(',', end + 1);
					antStartX = Integer.parseInt(line.substring(start, end));
					
					start = end + 1;
					end = line.indexOf(',', end + 1);
					antStartY = Integer.parseInt(line.substring(start, end));
					
					start = end + 1;
					end = line.indexOf(',', end + 1);
					antEndX = Integer.parseInt(line.substring(start, end));
					
					start = end + 1;
					end = line.indexOf(',', end + 1);
					antEndY = Integer.parseInt(line.substring(start, end));
					
					ants.add(new Ant(antId, new Click(1, antStartX, antStartY), new Click(maxFrames, antEndX, antEndY)));
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (br != null) br.close();
				if (is != null) is.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Test writing to server through CGI script
//		Path testPath = new Path(videos.get(0).getStarting().get(0));
//		testPath.setName(Long.toString(System.currentTimeMillis()));
//		for (int i = 0; i < 63; i++) {
//			testPath.addClick(new Click(5*i, 5*i, 20, Behavior.NONE, i));
//		}
//		
//		try {
//			URL fileUrl = new URL("http://cgi.cs.arizona.edu/cgi-bin/projects/angryants/writepath.cgi");
//			URLConnection url = fileUrl.openConnection();
//			url.setUseCaches(false);
//			
//			// Specify content type to imitate HTML form data (enctype="<>" in HTML)
////			url.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // default
//			url.setRequestProperty("Content-Type", "text/plain"); // spaces encoded to '+'
////			url.setRequestProperty("Content-Type", "multipart/form-data"); // no encoding
//			
//			url.setDoInput(true);
//	        url.setDoOutput(true);
//	        
////	        SecurityManager sm = System.getSecurityManager();
//	        
//	        // Retrieve and format localhost's host name and MAC address for host identifier
//	        InetAddress netAddr = InetAddress.getLocalHost();
//	        NetworkInterface netInterface = NetworkInterface.getByInetAddress(netAddr);
//	        System.out.println("HostName: " + netAddr.getHostName());
//	        String output = "";
//	        
//	        try {
//	        	byte[] mac = netInterface.getHardwareAddress();
//		        String macHex = "";
//		        String tmpHex = "";
//		        for (int j = 0; j < mac.length; j++) {
//		        	tmpHex = Integer.toHexString(0xFF & mac[j]);
//		        	if (tmpHex.length() == 1) {
//		        		macHex += "0";
//		        	}
//		        	macHex += tmpHex + "-";
//		        }
//		        macHex = macHex.substring(0, macHex.length() - 1);
//		        System.out.println("Mac Addr: " + macHex);
//		        output = netAddr.getHostName() + "_" + macHex + "/";
//	        } catch (SocketException se) {
//	        	System.out.println("Permission denied for address fetch.");
//	        }
//
//	        // Send everything as parametized POST request, start with host identifier
////	        String output = "hostId=" + netAddr.getHostName() + " " + macHex + "&";
////	        String output = netAddr.getHostName() + " " + macHex + "/";
//	        
//	        // Add the file name to be written to
//	        output += videos.get(0).getFileName() + "-(" + testPath.getStart().getRow() + "," + 
//	        		testPath.getStart().getCol() + ").txt" + "/";
//	        
//	        // Add the path name
//	        output += testPath.getName() + "/";
//	        
//	        // Add the clicks
//	        for (Click c : testPath.getPath()) {
//	        	output += c.getRow() + " " + c.getCol() + " " + c.getDir() + " " + c.getTime() +  
//	        			  " " + c.getBehavior() + "/";
//	        }
//	        output += '\n'; // end output with newline
//	        
//	        // Create output stream (with auto flush) and specify length of message
//	        url.setRequestProperty("Content-length", output.length() + "");
//	        PrintStream printOut = new PrintStream(url.getOutputStream(), true);
//	        
//	        // Send the string and close the stream
//	        printOut.print(output);
//	        printOut.flush();
//	        printOut.close();
//	        System.out.println("PrintStream Closed");
//	        
//	        // Read response from script and print
//	        BufferedReader inRead = new BufferedReader(new InputStreamReader(
//	        		url.getInputStream()));
//	        
//	        String inStr;
//	        while ((inStr = inRead.readLine()) != null) {
//	        	System.out.println(inStr);
//	        }
//	        
//	        inRead.close();
//		}
//		catch (MalformedURLException mue) {		// might need better error handling
//			System.err.println("URL not found");
//		}
//		catch(IOException ioe) {
//			System.err.println("File not found");
//			ioe.printStackTrace();
//		}
		
		
	}
	
	/**
	 * Adds the name of a path and the values of its clicks to a file on a remote server.  There
	 * is a file for each starting ant in each video.
	 * 
	 * @param vName Name of the video
	 * @param p The path to be added
	 */
	public void addPath(String vName, Path p) {
		//currUser.addPath(vName, p);
		//saveUser();
		
		// append path to text file, sorted by video name and starting ant
		// format:
		// <path name>
		// <Click #1 time>,<Click #1 x-value>,<Click #1 y-value>,<Click #1 direction> 
		// <Click #2 time>,<Click #2 x-value>,<Click #2 y-value>,<Click #2 direction> 
		// ...
		// <Click #n time>,<Click #n x-value>,<Click #n y-value>,<Click #n direction>
		try {
			URL fileUrl = new URL("http://cgi.cs.arizona.edu/cgi-bin/projects/angryants/writepath2.cgi");
			URLConnection url = fileUrl.openConnection();
			url.setUseCaches(false);
			
			// Specify content type to imitate HTML form data (enctype="<>" in HTML)
//					url.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // default
			url.setRequestProperty("Content-Type", "text/plain"); // spaces encoded to '+'
//					url.setRequestProperty("Content-Type", "multipart/form-data"); // no encoding
			
			url.setDoInput(true);
	        url.setDoOutput(true);
	        
//			        SecurityManager sm = System.getSecurityManager();
	        
	        // Retrieve and format localhost's host name for host identifier
	        InetAddress netAddr = InetAddress.getLocalHost();
	        
	        Video v = null;
	        for (Video tmp : videos) {
	        	if (tmp.getFileName().equals(vName)) {
	        		v = tmp;
	        		break;
	        	}
	        }
	        if (v == null) {
	        	// throw error, "Video could not be found"
	        }
	        
	        // Send all data in one string delimited by forward slashes, starting with the file 
	        // name to be written to
	        String output = v.getName() + "_" + p.getAnt().getId() + ".txt" + "/";
	        
	        // Add the canonical host name (device name or local ip addr if unavailable) and path name
	        output += p.getName() +  " " + netAddr.getCanonicalHostName() + "/";
	        
	        // Add the clicks, scale them to original resolution of video; allow 2 decimal places
	        double scale = (double) v.getAlphaHeight()/540;
	        DecimalFormat decFormat = new DecimalFormat("#.##");
	        for (Click c : p.getPath()) {
	        	output += c.getFrameNum() + "," + decFormat.format(c.getX()*scale) + "," + 
	        			decFormat.format(c.getY()*scale) + "," + decFormat.format(c.getDir()) + "/";
	        }
	        output += '\n'; // end output with newline
	        
	        // Create output stream (with auto flush) and specify length of message
	        url.setRequestProperty("Content-length", output.length() + "");
	        PrintStream printOut = new PrintStream(url.getOutputStream(), true);
	        
	        // Send the string and close the stream
	        printOut.print(output);
	        printOut.flush();
	        printOut.close();
//	        System.out.println("PrintStream Closed");
	        
	        // Read response from script and print
	        BufferedReader inRead = new BufferedReader(new InputStreamReader(
	        		url.getInputStream()));
	        
//	        String inStr;
//	        while ((inStr = inRead.readLine()) != null) {
//	        	System.out.println(inStr);
//	        }
	        
	        inRead.close();
		}
		catch (MalformedURLException mue) {		// might need better error handling
			System.err.println("URL not found");
		}
		catch(IOException ioe) {
			System.err.println("File not found");
			ioe.printStackTrace();
		}
		
//		setChanged();
//		notifyObservers(0); 	// notify controller that path has successfully been recorded
	}
	
	public List<Path> getPaths(String vName) {
		return currUser.getPaths(vName);
	}
	
	public LinkedList<Video> getVideos() {
		return videos;
	}
	
	public void removeLastPath(String vName) {
		currUser.removeLastPath(vName);
	}
}
