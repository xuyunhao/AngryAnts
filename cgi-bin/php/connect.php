<?php

    // One connection file so that we don't have to have this same code
    // at the top of every page.

    $db = new PDO(
        "mysql:host=mysql.cs.arizona.edu;dbname=xuyunhao_angryant",
        "angryant",
        "7yet2go"
    );

	//include("Smarty-3.1.8/libs/Smarty.class.php");
	//$smarty = new Smarty;
?>
