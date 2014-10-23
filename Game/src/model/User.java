/**
 * @author Paul Shen
 * 
 * The User class encapsulates a user's metadata (id, name) and data (the paths constructed by the
 * user). 
 */

package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class User implements Serializable {

	private static final long serialVersionUID = 6643908934139093124L;
	private int id;
	private String name;
	private HashMap<String, List<Path>> paths;	// map video names to paths
	
	/**
	 * Sets the user's id and name.  Initializes hashmap of paths.
	 * 
	 * @param id
	 * @param name
	 */
	public User(int id, String name) {
		this.id = id;
		this.name = name;
		paths = new HashMap<String, List<Path>>();
	}
	
	/**
	 * @return The user's id
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * @return The user's name
	 */
	public String getName() {
		return name;
	}
	
	public boolean containsVideo(String vName) {
		return paths.containsKey(vName);
	}
	
	/**
	 * @return The paths constructed by the user
	 */
	public List<Path> getPaths(String vName) {
		return paths.get(vName);
	}
	
	/**
	 * @param v The video the path is associated with
	 * @param p The path created by the user
	 */
	public void addPath(String vName, Path p) {
		if (paths.containsKey(vName)) {		// path(s) already exist(s) for the video
			paths.get(vName).add(p);
		}
		else {							// first path for the video
			List<Path> newPath = new LinkedList<Path>();
			newPath.add(p);
			paths.put(vName, newPath);
		}
	}
	
	public void removeLastPath(String vName) {
		paths.get(vName).remove(paths.get(vName).size() - 1);
	}
}
