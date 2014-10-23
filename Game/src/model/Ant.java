/**
 * @author Paul Shen
 * 
 * Encapsulates data for the ant associated with a video in a user's game instance.
 */

package model;

public class Ant {

	private int id;
	private Click start;
	private Click end;
	
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
}
