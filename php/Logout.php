<?php
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
                //AJAX request to logout the user. Server will answer HTTP 401 in order to clear HTTP credentials.
                xhrSimpleRequest('get', 'dnie/ClearCredentials.php', 'dummy', 'dummy', null, null, null);
            }
            //alert(document.cookie);

            setTimeout(function(){window.location = 'Index.php';}, 1500)
        </script>
    </body>
</html>
