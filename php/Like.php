<?php
session_start();
if (!isset($_SESSION['count']) || !isset($_SESSION['csrf'])) 
    exit("You need to login first!");

//I like.
if (isset($_POST['csrf']) && ($_POST['csrf'] == $_SESSION['csrf'])) {
    //Regenerates the token.
    $_SESSION['csrf'] = uniqid(rand(), true);
    // Likes!
    $_SESSION['count']++;
    
    echo "{$_SESSION['count']}&{$_SESSION['csrf']}";
}
?>