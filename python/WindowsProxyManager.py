"""Class that manages the Internet proxy configuration.

Copyright 2014 Dario B. darizotas at gmail dot com
This software is licensed under a new BSD License. 
Unported License. http://opensource.org/licenses/BSD-3-Clause
"""
from wininet.winproxysettings import *
#from defusedxml.ElementTree import parse
                
# https://support.microsoft.com/kb/226473
# For both

def current():
    option = (INTERNET_PER_CONN_OPTION * 5)()
    option[0].dwOption = INTERNET_PER_CONN_AUTOCONFIG_URL
    option[1].dwOption = INTERNET_PER_CONN_AUTODISCOVERY_FLAGS
    option[2].dwOption = INTERNET_PER_CONN_FLAGS
    option[3].dwOption = INTERNET_PER_CONN_PROXY_BYPASS
    option[4].dwOption = INTERNET_PER_CONN_PROXY_SERVER
    
    settings = WinProxySettings()
    if settings.current(option):
        print 'Current Internet Proxy options:'
        print 'Autoconfig URL:      %s' % option[0].Value.pszValue
        windll.kernel32.GlobalFree(option[0].Value.pszValue)
        #print 'Autodetect:          %i' % option[1].Value.dwValue
        print 'Proxy type:          %i' % option[2].Value.dwValue
        print 'Proxy bypass URLs:   %s' % option[3].Value.pszValue
        windll.kernel32.GlobalFree(option[3].Value.pszValue)
        print 'Static Proxy server: %s' % option[4].Value.pszValue
        windll.kernel32.GlobalFree(option[4].Value.pszValue)
    

# http://msdn.microsoft.com/en-us/library/windows/desktop/aa385384%28v=vs.85%29.aspx
# https://stackoverflow.com/questions/18117652/how-to-use-ctypes-windll-wininet-internetqueryoptionw-in-python
def disable():
    option = (INTERNET_PER_CONN_OPTION * 1)()
    option[0].dwOption = INTERNET_PER_CONN_FLAGS
    option[0].Value.dwValue = PROXY_TYPE_DIRECT
    
    settings = WinProxySettings()
    settings.change(option)

def change():
    option = (INTERNET_PER_CONN_OPTION * 1)()
    option[0].dwOption = INTERNET_PER_CONN_FLAGS
    option[0].Value.dwValue = PROXY_TYPE_PROXY
    
    settings = WinProxySettings()
    settings.change(option)
    

if __name__ == '__main__':
    #current()
    disable()  
    #change()
