/**
 * @author Paul Shen
 * 
 * The Click class represents the location and time at which the user attempted to click on the ant.
 */

package model;

public class Click {

	private double x;	// col
	private double y;	// row
	private double dir;
	private Behavior behavior;
	private int frameNum;
	
	/**
	 * Constructor that only initializes location with frame number 1.  Used for starting ant locations.
	 * 
	 * @param frameNum Corresponds to the frame in the video.
	 * @param x X-coordinate (column #) of the click
	 * @param y Y-coordinate (row #) of the click
	 */
	public Click(int frameNum, double x, double y) {
		this.frameNum = frameNum;
		this.x = x;
		this.y = y;
		dir = -1;
		behavior = null;
	}
	
	/**
	 * @param frameNum Corresponds to the frame in the video.
	 * @param x X-coordinate (column #) of the click
	 * @param y Y-coordinate (row #) of the click
	 * @param dir Direction ant was facing measured as plane angle in degrees counter-clockwise from positive x-axis
	 * @param behavior Observed behavior since last click
	 */
	public Click(int frameNum, double x, double y, double dir, Behavior behavior) {
		this.frameNum = frameNum;
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.behavior = behavior;
	}
	
	/**
	 * @return X-coordinate (column #) of the click.
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * @return Y-coordinate (row #) of the click.
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * @return Direction ant was facing measured as plane angle in degrees counter-clockwise from positive x-axis
	 */
	public double getDir() {
		return dir;
	}
	
	/**
	 * @return Observed behavior since last click
	 */
	public Behavior getBehavior() {
		return behavior;
	}
	
	/**
	 * @return Frame number in the video the click was performed on
	 */
	public int getFrameNum() {
		return frameNum;
	}
}
