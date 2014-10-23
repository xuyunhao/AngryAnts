<?php
if(!isset($_POST['data_type']))
	die("Error: No data_type specified.");
$result = $_POST['data_type']."\n".
		  $_POST['video_id']."\n";		  
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
$filename = $path."/".date("Y-m-d H:i:s").".txt";
file_put_contents($filename, $result);
chmod($filename, 0664);

// Increment number of paths submitted for ant in DB
$antId = $_POST['antId'];
$videoId = $_POST['videoId'];
$segId = $_POST['segId'];

$db = new PDO(
        "mysql:host=mysql.cs.arizona.edu;dbname=xuyunhao_angryant",
        "angryant",
        "7yet2go");
$getNumPathsQuery = $db->prepare("
	SELECT numPaths 
	FROM ant
	WHERE antId = ? AND videoId = ? AND segId = ?
");
$getNumPathsQuery->execute(array($antId, $videoId, $segId));
$qResult = $getNumPathsQuery->fetch();
$numPaths = $qResult['numPaths'];
$numPaths = (int)$numPaths + 1;

$updateNumPathsQuery = $db->prepare("
	UPDATE ant
	SET numPaths = ?
	WHERE antId = ? AND videoId = ? AND segId = ?
");
$updateNumPathsQuery->execute(array($numPaths, $antId, $videoId, $segId));

echo "Data submitted successfully.";
?>
