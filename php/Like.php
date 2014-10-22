<?php
/**
 * Increments the 'like' count.
 * @package darizotas_php
 * @author Dario Borreguero
 * @version $Rev$
 *
 * Copyright 2014 Dario B. darizotas at gmail dot com
 * This software is licensed under a new BSD License. 
 * Unported License. http://opensource.org/licenses/BSD-3-Clause
 */

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