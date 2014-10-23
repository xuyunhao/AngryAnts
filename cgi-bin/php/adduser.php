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

    // Step 1: enforce rules in case they were bypassed on the client

    if (empty($_POST['password']) || empty($_POST['username']))
    {
        die("Please fill out all fields.");
    }

    // if ($_POST['password'] != $_POST['password_rt'])
    // {
    //     die("Passwords don't match.");
    // }

    // Step 2: connect to the database

    include("connect.php");

    // Step 3: Check if a user with that name already exists
    $check_for_user_query = $db->prepare("
        SELECT COUNT(`username`) FROM `user`
        WHERE `username` = :username
    ");

    $check_for_user_query->execute(
        array(
            ':username' => $_POST['username']
        )
    );

    if ($check_for_user_query->fetchColumn(0) > 0)
    {
        die("That username is already taken.");
    }
        
    // Step 4: Create a user
    $insert_user_query = $db->prepare("
        INSERT INTO `user`
            (`username`, `password`, `credential`)
            VALUES
            (:username, :password, :credential)
    ");

	$salt = saltgen(32);
    $insert_user_query->execute(array(
        ':username' => $_POST['username'],
        ':password' => $_POST['password'],
		':credential' => $_POST['username'].$salt
    ));

    die("Credential:".$_POST['username'].$salt."\nHighestScore:0\nHighestMultiplier:0");

?>
