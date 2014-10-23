<?php

	function saltgen($length)
	{
		$characters='qwertyuiopasdfghjklzxcvbnm1234567890';
		$salt = '';
		for ($i = 0; $i < $length; ++$i) {
			$ind = mt_rand(0, 35);
			$ch = $characters{$ind};
			$salt .= $ch;
		}
		return $salt;
	}
    // Step 1: start the session

    //session_name('chat_hw10');
    //session_start();

    // Step 2: connect to the database

    include("connect.php");

	// Get Salt for a user
	
	// $get_salt = $db->prepare("
	// 		SELECT `salt` FROM `users`
	// 		WHERE
	// 			`username` = :username
	// 	");
	// 	$get_salt->execute(
	// 		array(
	// 			':username' => $_POST['username']
	// 		)
	// 	);
	// 	
	// 	$saltarray = $get_salt->fetch();
	// 	
	// 	$salt = $saltarray['salt'];

    // Step 3: Check if a user with the attempted login info  exists

	if ($_POST['username'] == 'guest') {
		die("Credential:0\nHighestScore:0\nHighestMultiplier:0");
	}

    $check_for_user_query = $db->prepare("
        SELECT COUNT(`username`) FROM `user`
        WHERE
            `username` = :username
        AND
            `password` = :password
    ");

    $check_for_user_query->execute(
        array(
            ':username' => $_POST['username'],
            ':password' => $_POST['password']
        )
    );

    if ($check_for_user_query->fetchColumn(0) == 0)
    {
        die("Invalid login information!");
    }

    // Step 4: Store that this user logged in successfully in the session
    // array for later

    //$_SESSION['username'] = $_POST['username'];

    //header("Location: home.php");
	$update_credential_query = $db->prepare("
		UPDATE `user` SET
			`credential` = :credential
		WHERE
			`username` = :username
	");
	$salt = saltgen(32);
	$update_credential_query->execute(
		array(
			':username' => $_POST['username'],
			':credential' => $_POST['username'].$salt
		)
	);
	$get_score_query = $db->prepare("
		SELECT `highest_score`, `highest_multiplier` FROM `user`
		WHERE
		`username`=:username
	");
	$get_score_query->execute(array(
		':username' => $_POST['username']
	));
	$result = $get_score_query->fetch();
	
	die("Credential:".$_POST['username'].$salt."\nHighestScore:".$result['highest_score']."\nHighestMultiplier:".$result['highest_multiplier']);
?>
