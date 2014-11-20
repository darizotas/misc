"""Class that manages the Internet proxy configuration.

Copyright 2014 Dario B. darizotas at gmail dot com
This software is licensed under a new BSD License. 
Unported License. http://opensource.org/licenses/BSD-3-Clause
"""
from wininet.wininetwrapper import *

class WinProxySettings:
    def current(self, option):
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
        print 'Retrieving current Internet Proxy options...'
        if InternetQueryOption(None, INTERNET_OPTION_PER_CONNECTION_OPTION, byref(list), byref(nSize)):
            print '[Done]'
            return True
        else:
            print '[Error] Internet Proxy options could not be retrieved!'
            return False

    def change(self, option):
        print 'Checking new Internet Proxy options...'
        if not self._check(option):
            return False
        print '[Done]'
            
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
            print '[Done]'

            # Notifies the changes to the browser (Internet Explorer)
            InternetSetOption(None, INTERNET_OPTION_SETTINGS_CHANGED, None, 0)
            InternetSetOption(None, INTERNET_OPTION_REFRESH, None, 0)

            return True
        else:
            print '[Error] Internet Proxy options could not be updated.'
            return False
            
    def _check(self, option):
        urlEnabled = False
        urlValid = False
        serverEnabled = False
        serverValid = False
        for opt in option:
            # Connection flags.
            if (opt.dwOption == INTERNET_PER_CONN_FLAGS):
                if (opt.Value.dwValue & PROXY_TYPE_PROXY):
                    serverEnabled = True
                if (opt.Value.dwValue & PROXY_TYPE_AUTO_PROXY_URL):
                    urlEnabled = True
            # Autoconfig URL
            if (opt.dwOption == INTERNET_PER_CONN_AUTOCONFIG_URL and len(opt.Value.pszValue) > 0):
                urlValid = True
            # Static server
            if (opt.dwOption == INTERNET_PER_CONN_PROXY_SERVER and len(opt.Value.pszValue) > 0):
                serverValid = True
        
        if urlEnabled and not urlValid:
            print '[Warning] Automatic configuration option is selected, but no URL is provided. Relying on former Internet proxy settings.'

        if serverEnabled and not serverValid:
            print '[Warning] Static proxy server option is selected, but no server address is provided. Relying on former Internet proxy settings.'

        #return ( not urlEnabled or urlValid) and (not serverEnabled or serverValid)
        return True
