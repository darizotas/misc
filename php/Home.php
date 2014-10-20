<?php
session_start();
if (!isset($_SESSION['count'])) 
    exit("You need to login first!");
    
// Token to avoid csrf attacks
$_SESSION['csrf'] = uniqid(rand(), true);
?>
<html>
    <head>
        <title>Home page</title>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
        <script type="text/javascript" src="xhr.js"></script>
        <script type="text/javascript">
            function doSubmit(form) {
                // Generates the payload.
                var payload = "";
                for (var i = 0; i < form.length; i++) {
                    payload = payload + ((i > 0)? "&" : "") + form[i].name + "=" + form[i].value;
                }
                // Makes the request.
                xhrSimpleRequest(form.method, form.action, null, null, payload, 'session_likes', 'csrf');
                
                return true;
            }
        </script>
    </head>
    <body>
        <p>Welcome to your home page.</p>
        <p>Number of likes: <span id="session_likes"><?php print $_SESSION['count'] ?></span></p>
        <form name="like" method="post" action="Like.php" onsubmit="return !doSubmit(this, 'session_likes');">
            <input type="submit" id="buttonLike" name="buttonLike" value="Like" />
            <input type="hidden" id="csrf" name="csrf" value="<?php print $_SESSION['csrf'];?>" />
        </form>
        
        <a href="Logout.php">Click here to logout.</a>
    </body>
</html>