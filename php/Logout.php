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
        <script type="text/javascript" src="clearcrendentials.js"></script>
    </head>
    <body>
        <p>
        Clear your SSL cache in the Browser to make sure your certificate is not cached.
        Bye!
        </p>
        <script type="text/javascript">
            xhrSimpleRequest('get', 'dnie/ClearCredentials.php', 'dummy', 'dummy', null, null, null);

            //alert(document.cookie);
            clearCacheCredentials();
            //alert(document.cookie);

            setTimeout(function(){window.location = 'Index.php';}, 1500)
        </script>
    </body>
</html>
