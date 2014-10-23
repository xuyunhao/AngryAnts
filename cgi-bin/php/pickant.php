<?php
	include("connect.php");
	
	$get_ant_query = $db->prepare("
		SELECT `vid`, `vid2`, `aid` FROM `ant`
		WHERE
			`picked` = (
				SELECT MIN(`picked`) FROM `ant`
			) LIMIT 5
	");
	
	$get_ant_query->execute();
	$antArray = $get_ant_query->fetchAll();
	$ind = mt_rand(0, count($antArray) - 1);
	die("vid:".$antArray[$ind]['vid']."\nvid2:".$antArray[$ind]['vid2']."\naid:".$antArray[$ind]['aid']);
?>