<?php
	include("connect.php");

	$vid = $_GET['vid'];
	$aid = $_GET['aid'];
	$vid2 = $_GET['vid2'];
	$credential = $_GET['credential'];
	$uid = 0;
	$check_for_user_query = $db->prepare("
        SELECT COUNT(`username`) FROM `user`
        WHERE
            `credential` = :credential
    ");

    $check_for_user_query->execute(
        array(
			':credential' => $credential
        )
    );

    if ($check_for_user_query->fetchColumn(0) == 0)
    {
        $uid = 0;
    } else {
		$get_user_query = $db->prepare("
			SELECT `userid` FROM `user`
			WHERE
				`credential` = :credential
		");
		$get_user_query->execute(array(
			':credential' => $credential
		));
		$result = $get_user_query->fetch();
		$uid = $result['userid'];
	}
	
	// insert into path table
	$get_pathid_query = $db->prepare("
		SELECT COUNT(`pid`) FROM `path`
		WHERE
		`vid`=:vid AND
		`aid`=:aid AND
		`vid2`=:vid2
	");
	$get_pathid_query->execute(array(
		':vid' => $vid,
		':aid' => $aid,
		':vid2' => $vid2
	));
	$newpid = $get_pathid_query->fetchColumn(0);
	//die($newpid."===".$vid."===".$aid);
	$filename="v".$vid.$vid2."_a".$aid."_u".$uid."_p".$newpid.".txt";
	$insert_path_query = $db->prepare("
		INSERT INTO `path` (`pid`, `vid`, `vid2`, `aid`, `userid`, `filename`) VALUES
		(:pid, :vid, :vid2, :aid, :uid, :filename);
	");
	$insert_path_query->execute(array(
		':pid' => $newpid,
		':vid' => $vid,
		':vid2' => $vid2,
		':aid' => $aid,
		':uid' => $uid,
		':filename' => $filename
	));
	
	// update ant table
	$get_times_query = $db->prepare("
		SELECT `picked` FROM `ant`
		WHERE
		`vid`=:vid AND `aid`=:aid AND `vid2`=:vid2
	");
	$get_times_query->execute(array(
		':vid' => $vid,
		':aid' => $aid,
		':vid2' => $vid2
	));
	$result = $get_times_query->fetch();
	$times = $result['picked'];
	$update_ant_query = $db->prepare("
		UPDATE `ant` SET `picked`=:picked
		WHERE `vid`=:vid AND `aid`=:aid AND `vid2`=:vid2
	");
	$update_ant_query->execute(array(
		':picked' => ($times + 1),
		':vid' => $vid,
		':aid' => $aid,
		':vid2' => $vid2
	));
	
	$fileData=file_get_contents('php://input');
	$fhandle=fopen("data/".$filename, 'w');
	fwrite($fhandle, $fileData);
	fclose($fhandle);
	echo("Done uploading");
	include("chmod.php");
?>
