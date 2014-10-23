/**
 * @author Paul Shen
 * 
 * The Video class encapsulates the video metadata (id, file name, # frames) and the starting 
 * locations of the ants to be tracked
 */

package model;

import java.util.ArrayList;

public class Video {

	private int groupId;
	private String vName;
	private String fileName;
	private int alphaWidth;
	private int alphaHeight;
	private float alphaFrameRate;
	private int maxFrames;
	
	private ArrayList<Ant> ants; 
	
	/**
	 * @param groupId 		The video's internal id, used to group video segments from the same original video together
	 * @param vName			The name of the video
	 * @param fileName 		The file name or URL of the video
	 * @param alphaWidth	The video's original width
	 * @param alphaHeight	The video's original height
	 * @param maxFrames 	The number of frames to be clicked on by the user
	 * @param alphaFrameRate	The video's original framerate, used if the video is to be played at its normal 1x speed
	 * @param start 		The starting locations of ants to be tracked
	 */
	public Video(int groupId, String vName, String fileName, int alphaWidth, int alphaHeight, 
			int maxFrames, float alphaFrameRate, ArrayList<Ant> ants) {
		this.groupId = groupId;
		this.vName = vName;
		this.fileName = fileName;
		this.alphaWidth = alphaWidth;
		this.alphaHeight = alphaHeight;
		this.maxFrames = maxFrames;
		this.alphaFrameRate = alphaFrameRate;
		this.ants = ants;
	}
	
	/**
	 * @return The video's id
	 */
	public int getGroupId() {
		return groupId;
	}
	
	/**
	 * @return The video's original width.
	 */
	public int getAlphaWidth() {
		return alphaWidth;
	}
	
	/**
	 * @return The video's original height.
	 */
	public int getAlphaHeight() {
		return alphaHeight;
	}
	
	/**
	 * @return The number of frames to be clicked on by the user
	 */
	public int getMaxFrames() {
		return maxFrames;
	}
	
	/**
	 * @return The name of the video
	 */
	public String getName() {
		return vName;
	}
	
	/**
	 * @return The file name or URL of the video
	 */
	public String getFileName() {
		return fileName;
	}
	
	public float getFrameRate() {
		return alphaFrameRate;
	}
	
	/**
	 * @return The starting locations of the ants to be tracked
	 */
	public ArrayList<Ant> getAnts() {
		return ants;
	}
}
