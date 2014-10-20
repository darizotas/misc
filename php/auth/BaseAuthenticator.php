<?php
/**
 * Abstract class responsible for authenticating in the site.
 */
interface darizotas_php_auth_BaseAuthenticator {
    /**
     * Returns true whether the client has been authenticated in the server.
     * @return True whether the client authenticated in the server.
     */
    public function authenticate();
}
?>