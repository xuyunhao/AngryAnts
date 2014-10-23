This file contains instructions on how to use the data files stored in the following subdirectories of this folder:

ANT_PATHS folder:

The first line of the file should always read "ANT_PATH".
The second line of the file is the ID of the video from which this data came from.
The third line of the file is the ID of the ant that is being traced.
All following lines are data points, where each point contains three pieces of information:
(1) The time in seconds at which this frame occurs in the video.
(2) The x-coordinate of the ant's current position
(2) The y-coordinate of the ant's current position
In summary, the file will look like this:

	ANT_PATH
	video-id
	ant-id
	time / x / y
	time / x / y
	time / x / y
	.
	.
	.


START_POINTS folder:

The first line of the file should always read "START_POINTS".
The second line of the file is the ID of the video from which this data came from.
The third line of the file is the time in seconds of the frame of the video from which this data came from:
All following lines are data points, where each point contains two pieces of information:
(2) The x-coordinate of the ant's current position
(2) The y-coordinate of the ant's current position
In summary, the file will look like this:

	data-type
	video-id
	time
	x / y
	x / y
	x / y
	.
	.
	.
