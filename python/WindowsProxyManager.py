"""Class that manages the Internet proxy configuration.

Copyright 2014 Dario B. darizotas at gmail dot com
This software is licensed under a new BSD License. 
Unported License. http://opensource.org/licenses/BSD-3-Clause
"""
from wininet.wininetwrapper import *

# http://msdn.microsoft.com/en-us/library/windows/desktop/aa385384%28v=vs.85%29.aspx
# https://stackoverflow.com/questions/18117652/how-to-use-ctypes-windll-wininet-internetqueryoptionw-in-python
# https://support.microsoft.com/kb/226473
def disable():
	option = (INTERNET_PER_CONN_OPTION * 1)()
	option[0].dwOption = INTERNET_PER_CONN_FLAGS
	option[0].Value.dwValue = PROXY_TYPE_DIRECT
	
	list = INTERNET_PER_CONN_OPTION_LIST()
	# Fill in the list structure
	list.dwSize = sizeof(INTERNET_PER_CONN_OPTION_LIST)
	# NULL == LAN, otherwise connectoid name.
	list.pszConnection = None
	# Fill in the options
	list.dwOptionCount = len(option)
	list.dwOptionError = 0
	list.pOptions = option

	nSize = c_ulong(sizeof(INTERNET_PER_CONN_OPTION_LIST))
	print 'Updating Internet Proxy options...'
	if InternetSetOption(None, INTERNET_OPTION_PER_CONNECTION_OPTION, byref(list), nSize):
		print 'Internet Proxy options udpated!'
	else:
		print 'Internet Proxy options could not be updated'
		
	# Notifies the changes to the browser (Internet Explorer)
	InternetSetOption(None, INTERNET_OPTION_SETTINGS_CHANGED, None, 0)
	InternetSetOption(None, INTERNET_OPTION_REFRESH, None, 0)
	

if __name__ == '__main__':
	disable()