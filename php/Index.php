<!--
 * Index page.
 * @package darizotas_php
 * @author Dario Borreguero
 * @version $Rev$
 *
 * Copyright 2014 Dario B. darizotas at gmail dot com
 * This software is licensed under a new BSD License. 
 * Unported License. http://opensource.org/licenses/BSD-3-Clause
-->
<html>
    <head>
        <title>Login page</title>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    </head>
    <body>
        <p>Click in the button if you want to access to the private area by using your client certificate.</p>
        <a href="dnie/Authenticate.php">Login</a>
        <?php
        if (isset($_GET['err'])) {
            print '<span>Incorrect credentials, try again</span>';
        }
        ?>
    </body>
</html>
