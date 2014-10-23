AntTracerTools v0.1
by Paul Shen

General description: Provides the tools for adding the information of a new video to the master video information text file
used by the game, comparing paths in the same video.

1.  Adding information of a new video:
Procedure:
(0) Convert the original video to the .flv or .mp4 format with a fixed framerate of 50 fps and a fixed resolution height of
540 (if the aspect ratio of the original video is 16:9, the resolution will be 960x540, otherwise the width will have to
be calculated), and split the video into any number of segments if necessary.  Follow the video conversion guide.
(1) Open the newly converted video(s) in AntTracerTools by either finding them in their directory or dragging, upon which you will be prompted for the original width, height, framerate
and framerate of the video.  Refer to the original, uncompressed, non-converted video for this information.
(2) Tag the starting locations of the ants on the first frame.  After you're done, seek to the starting position of the
next segment in the video if you plan on one.  Otherwise, click done.  Either way, you will need to give the video a name
and the file path (either local or streaming http url) you will use for that segment.
(3) Segment the videos using the base filenames you entered (refer to the video conversion guide).  


2.  Viewing different paths in the same video
- The paths are grouped by the <video, ant> pair they belong to, and such paths are located in the
  file "AntTracerTool.jar/completed_paths/<video name>/<ant id>.txt"
  or from the project directory: "./bin/completed_paths/<video name>/<ant id>.txt"
- The format of the file is
	
	<5 lines used for Frechet calculations> // Assumed to be a fixed # lines
	<Number of paths in file>
	<Number of points in 1st path = m>
	<Point 1 x-coord> <Point 1 y-coord> <Point 1 frame #>
	...
	<Point m x-coord> <Point m y-coord> <Point m frame #> 
	<Number of points in 2nd path>
	...
	...
	<Number of points in nth path>
	...
	<Other stuff>

Procedure:
(0)  Add paths files to the .jar file (I recommend 7zip on Windows)
IGNORE-> (1)  Select the analyze video data option from the main menu.
(2)  Select a video from the panel of videos
(3)  Choose an ant by its ID # from the drop-down list, adjust the opactiy and line thickness if wanted
(4)  Seek to a frame in the video  
(5)  Save snapshot of the video to the current directory