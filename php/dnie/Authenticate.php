<?php
/**
 * Authenticate the user by means of the DNIe.
 * @package darizotas_php_dnie
 * @author Dario Borreguero
 * @version $Rev$
 *
 * Copyright 2014 Dario B. darizotas at gmail dot com
 * This software is licensed under a new BSD License. 
 * Unported License. http://opensource.org/licenses/BSD-3-Clause
 */


require_once (dirname(__FILE__)."/../auth/DNIeAuthenticator.php");

// Not logged in yet.
session_start();
if (!isset($_SESSION['count'])) {
    $authenticator = new darizotas_php_auth_DNIeAuthenticator();
    if ($authenticator->authenticate()) {
        session_regenerate_id();
        $_SESSION['count'] = 0;
        //session_commit();
        
    // Bad login credentials.
    } else {
        session_destroy();
        header('Location: https://localhost/php/Index.php?err=', false, 302);
        exit();
    }
}
header('Location: https://localhost/php/Home.php', false);
exit();
?>