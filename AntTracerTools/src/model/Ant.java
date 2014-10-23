/**
 * @author Paul Shen
 * 
 * Encapsulates data for the ant associated with a video in a user's game instance.
 */

package model;

import java.util.LinkedList;

public class Ant {

	private int id;
	private Click start;
	private Click end;
	private LinkedList<Path> completed = new LinkedList<Path>();
	
	public Ant(int id, Click start, Click end) {
		this.id = id;
		this.start = start;
		this.end = end;
	}
	
	public int getId() {
		return id;
	}
	
	public Click getStart() {
		return start;
	}
	
	public Click getEnd() {
		return end;
	}
	
	public void addCompletedPath(Path p) {
		completed.add(p);
	}
	
	public LinkedList<Path> getCompleted() {
		return completed;
	}
}
