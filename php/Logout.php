<?php
/**
 * Logout page.
 * @package darizotas_php
 * @author Dario Borreguero
 * @version $Rev$
 *
 * Copyright 2014 Dario B. darizotas at gmail dot com
 * This software is licensed under a new BSD License. 
 * Unported License. http://opensource.org/licenses/BSD-3-Clause
 */

session_start();
unset($_SESSION);
session_destroy();
?>
<html>
    <head>
        <title>Logout page</title>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
        <script type="text/javascript" src="xhr.js"></script>
    </head>
    <body>
        <p>
        Clear your SSL cache in the Browser to make sure your certificate is not cached.
        Bye!
        </p>
        <script type="text/javascript">
            //alert(document.cookie);

            // Clear HTTP Authentication and HTTPS client certificates
            // IE6sp1 or later
            // http://msdn.microsoft.com/en-us/library/ms536979.aspx
            if (!document.execCommand('ClearAuthenticationCache', false)) {
                //FF and Chrome: AJAX request to logout the user. Server will answer HTTP 401 in order to clear HTTP credentials.
                //Chrome: Server side is also configured to fail with client certificate.
                xhrSimpleRequest('get', 'dnie/ClearCredentials.php', 'dummy', 'dummy', null, null, null);
            }
            //alert(document.cookie);

            setTimeout(function(){window.location = 'Index.php';}, 1500)
        </script>
    </body>
</html>
