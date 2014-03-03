"""Script that establishes a session in a network managed by Cisco Web Authentication.

This script requests for re-establishing a session in a network managed by Cisco Web 
Authentication.

Copyright 2013 Dario B. darizotas at gmail dot com
This software is licensed under a new BSD License. 
Unported License. http://opensource.org/licenses/BSD-3-Clause
"""

import httplib, urllib, socket
import sys
import re
class CiscoWebAuthParser:
  """Class responsible for parsing responses regarding messages from Cisco Web Authentication.
  
  Particularly it parses the response from "login.html".
  """

  def isConnected(self, html):
    """Returns true whether it is already established a session in the Cisco Web Authentication.
    
    Particularly it parses the title tag within the response from "login.html" 
    """
    # Parses for <title>Logged in</title>
    match = re.search('<title>Logged In</title>', html, re.I)
    return match
    
  def getMessage(self, html, flag):
    """Returns the message contained by the given flag (err, info) if enabled (value=1)."""
    pattern = 'NAME="' + flag + '_flag"\s+(?:\w+="\d+"\s+)*VALUE="1"'
    match = re.search(pattern, html, re.I)
    if match:
      msgPattern = 'NAME="' + flag + '_msg"\s+(?:\w+="\d+"\s+)*VALUE="([\w\s,\.]+)"'
      msgMatch = re.search(msgPattern, html, re.I)
      if msgMatch:
        return flag + ': ' + msgMatch.group(1)  
      else:
        return flag
    else:
      return ''
      
# Main
if __name__ == '__main__':

  if len(sys.argv) == 4:
    # Retrieves host, username and password.
    host = sys.argv[1]
    username = sys.argv[2]
    password = sys.argv[3]
    # Prepares the HTTPS request
    connection = httplib.HTTPSConnection(host)
    url = "/login.html"
    params = urllib.urlencode({\
      'buttonClicked': 4, \
      'err_flag': 0, 'err_msg': '', 'info_flag': 0, 'info_msg': '', \
      'redirect_url': '', 'username': username, 'password': password \
      })
    headers = {\
      'Content-Type': 'application/x-www-form-urlencoded', \
    }
    print "Connecting Cisco Web Authentication..."
    try:
      connection.request("POST", url, params, headers)
      response = connection.getresponse()
    except (httplib.HTTPException, socket.error) as ex:
      print ex
      sys.exit(1)

    # 100 Continue.
    if response.status == 200:
      body = response.read()
      # Replaces new lines.
  #    body = body.replace("\r\n", "").replace("\n", "")
      parser = CiscoWebAuthParser()
      if parser.isConnected(body):
        print 'Session restablished!'
      else:
        # Check for an error.
        msg = parser.getMessage(body, 'err')
        if msg:
          print msg
        else:
          # Check whether for an informative message.
          msg = parser.getMessage(body, 'info')
          if msg:
            print msg
          else:
            print 'I don\'t know how we arrived here. Check the Web:'
            print body
    else:
      print response.status, response.reason

    connection.close()  
  else:
    print 'Usage: CiscoWebAuth.py host username password'