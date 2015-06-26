<?php
/**
 * Forces the client to forget the HTTP authentication credentials cached as it always responds
 * with HTTP 401.
 * @package darizotas_php_dnie
 * @author Dario Borreguero
 * @version $Rev$
 *
 * Copyright 2014 Dario B. darizotas at gmail dot com
 * This software is licensed under a new BSD License. 
 * Unported License. http://opensource.org/licenses/BSD-3-Clause
 */

header('HTTP/1.1 401 Unauthorized', true, 401);
//http_response_code(401);
?>