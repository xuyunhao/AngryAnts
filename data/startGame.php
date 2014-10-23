<?php
	$db = new PDO(
        "mysql:host=mysql.cs.arizona.edu;dbname=xuyunhao_angryant",
        "angryant",
        "7yet2go"
	);

	// Select ants with lowest number of paths and randomly select one
    $getAntQuery = $db->prepare("
        SELECT antId, videoId, segId
        FROM ant
		WHERE
			numPaths = (
				SELECT MIN(numPaths) FROM ant
            )
        LIMIT 10
    ");
    $getAntQuery->execute();
	$ants = $getAntQuery->fetchAll();
    $antIndex = mt_rand(0, count($ants) - 1);
	$antId = $ants[$antIndex]['antId'];
	$videoId = $ants[$antIndex]['videoId'];
	$segId = $ants[$antIndex]['segId'];

	// Find ant start coordinates
	$getAntStartQuery = $db->prepare("
		SELECT x, y
		FROM coordinate, knownpath, path
		WHERE 
			antId = ? AND
        	videoId = ? AND
        	segId = ? AND
        	knownpath.pathId = path.pathId AND
        	coordinate.pathId = path.pathId AND
        	frameNum = 1
	");
	$getAntStartQuery->execute(array($antId, $videoId, $segId));
	$startCoord = $getAntStartQuery->fetch();
	$x = $startCoord['x'];
	$y = $startCoord['y'];

	// Find video information
	$getVideoQuery = $db->prepare("
		SELECT width, height, youTubeUrl 
		FROM video 
		WHERE 
			videoId = ? AND
			segId = ?
		ORDER BY frameRate
		");
	$getVideoQuery->execute(array($videoId, $segId));
	$videoParams = $getVideoQuery->fetchAll();
	$width = $videoParams[0]['width'];
	$height = $videoParams[0]['height'];
	$url1 = $videoParams[0]['youTubeUrl'];
	$url2 = $videoParams[1]['youTubeUrl'];
			
	print ("antId=$antId&videoId=$videoId&segId=$segId&x=$x&y=$y&width=$width&height=$height&url1=$url1&url2=$url2");
?>
