"""Script that establishes a session in a wireless network managed by Cisco Web Authentication.

This script requests for re-establishing a session in a wireless network managed by Cisco Web 
Authentication.

Copyright 2013 Dario B. darizotas at gmail dot com
This software is licensed under a new BSD License. 
Unported License. http://opensource.org/licenses/BSD-3-Clause
"""
from wlanapi.wlanapiwrapper import *
from wlanapi.wlanconninfo import *
from webauth.CiscoWebAuth import *
import sys


#Main
if len(sys.argv) == 5:
  # Checks if it is connected to the given wireless.
  # Retrieves the ssid name.
  ssid = sys.argv[1]
  try:
    info = WlanConnInfo()
    connected = info.isConnected(ssid)
  except WlanConnError as err:
    del info
    print err
    sys.exit(1)
  # The Wlan handles are no longer needed.
  del info
  
  if connected:
    print "Associated to %s. Let's establish a session." % ssid

    # Retrieves host, username and password.
    host = sys.argv[2]
    username = sys.argv[3]
    password = sys.argv[4]
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
#      body = body.replace("\r\n", "").replace("\n", "")
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
    print "Not associated to %s. There is nothing to do." % ssid

else:
  print 'Usage: CiscoWebAuthManager.py ssid host username password'
  sys.exit(2)