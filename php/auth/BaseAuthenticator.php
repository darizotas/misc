<?php
/**
 * Interface responsible for authenticating in the site.
 * @package darizotas_php_auth
 * @author Dario Borreguero
 * @version $Rev$
 *
 * Copyright 2014 Dario B. darizotas at gmail dot com
 * This software is licensed under a new BSD License. 
 * Unported License. http://opensource.org/licenses/BSD-3-Clause
 */
interface darizotas_php_auth_BaseAuthenticator {
    /**
     * Returns true whether the client has been authenticated in the server.
     * @return True whether the client authenticated in the server.
     */
    public function authenticate();
}
?>