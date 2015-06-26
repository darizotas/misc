<?php
require_once 'BaseAuthenticator.php';

/**
 * Abstract class responsible for validating the client certificate.
 * @package darizotas_php_auth
 * @author Dario Borreguero
 * @version $Rev$
 *
 * Copyright 2014 Dario B. darizotas at gmail dot com
 * This software is licensed under a new BSD License. 
 * Unported License. http://opensource.org/licenses/BSD-3-Clause
 */
abstract class darizotas_php_auth_ClientCertBaseAuthenticator implements darizotas_php_auth_BaseAuthenticator {
    /**
     * Authenticates the client by using the provided client certificate.
     */
    public function authenticate() {
        $dn = $this->getClientCertSubject();
        return $this->isCertValid() && $this->authenticateWithCert($dn);
    }
    
    /**
     * Returns the client certificate subject.
     * The subject field follows the {@link http://en.wikipedia.org/wiki/Distinguished_Name#Directory_structure Distinguished Name convention}
     * @return Map with the client certificate subject fields, where the keys correspond to each of 
     *         the DN fields.
     */
    public function getClientCertSubject() {
        $dn = array();
        if (isset($_SERVER['SSL_CLIENT_S_DN'])) {
            // http://httpd.apache.org/docs/2.2/mod/mod_ssl.html
            $dnSubject = explode('/', $_SERVER['SSL_CLIENT_S_DN']);
            foreach ($dnSubject as $dnAttribute) {
                $pieces = explode('=', $dnAttribute);
                if (count($pieces) > 0) {
                    $dn[$pieces[0]] = isset($pieces[1])? $pieces[1] : '';
                }
            }
        }
        return $dn;
    }
    
    /**
     * Returns true whether the client certificate is valid.
     * @return True whether the client certificate is valid.
     */
    protected abstract function isCertValid();
    
    /**
     * Returns true whether the authentication process is successful with the current client certificate.
     */
    protected abstract function authenticateWithCert(array $dn);
}
?>