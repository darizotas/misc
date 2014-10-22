<?php
require_once "ClientCertBaseAuthenticator.php";

/**
 * This class makes use of the of DNIe to authenticate the user.
 * It relies on querying OCSP service by means of openssl command.
 * @package darizotas_php_auth
 * @author Dario Borreguero
 * @version $Rev$
 *
 * Copyright 2014 Dario B. darizotas at gmail dot com
 * This software is licensed under a new BSD License. 
 * Unported License. http://opensource.org/licenses/BSD-3-Clause
 */
class darizotas_php_auth_DNIeAuthenticator extends darizotas_php_auth_ClientCertBaseAuthenticator {
    private static $OPENSSL_BIN = '"C:\Program Files (x86)\Apache Software Foundation\Apache2.2\bin\openssl.exe"';
    
    /**
     * Returns true whether the client certificate is valid by means of OCSP.
     * @return True whether the client certificate is valid.
     */
    protected function isCertValid() {
        // Creates issuer certificate temp file
        $issuer = tempnam(sys_get_temp_dir(), "dnie_issuer");
        if (file_put_contents($issuer, $_SERVER["SSL_CLIENT_CERT_CHAIN_0"]) === false)
            return false;
            
        // Creates client certificate temp file
        $client = tempnam(sys_get_temp_dir(), "dnie_client");
        if (file_put_contents($client, $_SERVER["SSL_CLIENT_CERT"]) === false)
            return false;

        //Performs OCSP query: https://www.ietf.org/rfc/rfc2560.txt
        $ocsp = popen(self::$OPENSSL_BIN." ocsp -issuer $issuer -cert $client -url http://ocsp.dnielectronico.es", "r");
        list($filename,$result) = fscanf($ocsp, "%s %s");
        fclose($ocsp);
        //remove auxiliary files
        unlink($issuer);
        unlink($client);
        
        return $result == "good";
    }
    
    /**
     * Returns true whether the authentication process is successful with the current client certificate.
     * @return True whether the authentication process is successful.
     */
    protected function authenticateWithCert(array $dn) {
        return true;
    }
}
?>