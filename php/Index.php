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
