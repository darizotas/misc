/*
 * Helper file to create XML HTTP Requests.
 *
 * Copyright 2014 Dario B. darizotas at gmail dot com
 * This software is licensed under a new BSD License. 
 * Unported License. http://opensource.org/licenses/BSD-3-Clause
 */
// Creates a simple XML HTTP Request
function xhrSimpleRequest(method, url, user, pass, payload, target, csrf) {
    var xhrObject = createXHRObject();
 	if (xhrObject) {
        // Do we care about the response?
        if (target && csrf) {
            xhrObject.onreadystatechange = function() {
                if (xhrObject.readyState == 4 && xhrObject.status == 200) {
                    var tokens = xhrObject.responseText.split("&");
                    document.getElementById(target).innerHTML = tokens[0];
                    document.getElementById(csrf).value = tokens[1];
                }
            };
        }
        // Asynchronous request.
        if (user && pass) {
            xhrObject.open(method, url, true, user, pass);
        } else {
            xhrObject.open(method, url, true);
        }
        
        if (method == 'post') {
            xhrObject.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
            xhrObject.setRequestHeader("Content-length", payload.length);
        }   
        xhrObject.send(payload);
	}
}

// Creates the XHR Object.
function createXHRObject() {
    var xhrObject = false;
   //Native implementation.
	if (window.XMLHttpRequest) {
        xhrObject = new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		try {
			xhrObject = new ActiveXObject("MSXML2.XMLHTTP");
		} catch (e) {
			try {
				xhrObject = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (e) {}
		}
	}
    return xhrObject;
}