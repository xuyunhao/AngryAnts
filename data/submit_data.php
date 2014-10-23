<?php
if(!isset($_POST['data_type']))
	die("Error: No data_type specified.");
$result = $_POST['video_id']."\n";		  
if($_POST['data_type']=="ANT_PATH"){
	$result .= $_POST['ant_id']."\n";
	$path = "ant_paths";
}else if($_POST['data_type']=="START_POINTS"){
	$result .= $_POST['time_frame']."\n";
	$path = "start_points";
}else{
	$result .= "ERROR: Unknown data_type: ".$_POST['data_type']."\n";
	$path = "errors";
}
$result .= $_POST['data'];
$filename = $path."/".$_POST['ant_id']."-".date("Y-m-d-H-i-s").".txt";
file_put_contents($filename, $result);
chmod($filename, 0664);
echo "Data submitted successfully.";
?>