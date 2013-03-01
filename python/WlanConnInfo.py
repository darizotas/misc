"""Proof of concept to get the SSID of the current Wlan connection on a Windows XP.

It uses the wlanapi.wlanapiwrapper module.

Copyright 2013 Dario B. darizotas at gmail dot com
This software is licensed under a new BSD License. 
Unported License. http://opensource.org/licenses/BSD-3-Clause
"""
from wlanapi.wlanapiwrapper import *
import sys

class WlanConnError(Exception):
  """Exception to be thrown when an error occurs calling to the wlanapiwrapper module"""
  pass
  
class WlanConnInfo:
  """Class responsible for dealing with wlanapiwrapper module to query about information
  regarding the current connection.
  """
  
  def __init__(self):
    """Initialises the class.
    If an error occurs while getting the handle, it will raise a WlanConnError.
    """
   
    # Opens handle for Wlan API
    CLIENT_VERSION_XP = 1
    self.hClient = HANDLE()
    currentVersion = DWORD()
    ret = WlanOpenHandle(CLIENT_VERSION_XP, None, byref(currentVersion), byref(self.hClient))
    if ret != ERROR_SUCCESS:
      raise WlanConnError(FormatError(ret))

    # Finds the interfaces
    self.pInterfaceList = pointer(WLAN_INTERFACE_INFO_LIST())
    ret = WlanEnumInterfaces(self.hClient, None, byref(self.pInterfaceList))
    if ret != ERROR_SUCCESS:
      raise WlanConnError(FormatError(ret))

    # Now we know how many interfaces are. Let's resize the interface info.
    self.ifaces = self._resize(self.pInterfaceList.contents.InterfaceInfo, 
      self.pInterfaceList.contents.dwNumberOfItems)

      
  def __del__(self):
    """Destroys the class."""
    WlanFreeMemory(self.pInterfaceList)
   
   
  def _resize(self, array, newSize):
    """Returns a resized instance of the given array from the memory address where it is
    located.
    """
    return (array._type_*newSize).from_address(addressof(array))
      
  def isConnected(self, ssid):
    """Returns true whether it is currently connected to the Wlan identified by the given
    ssid.
    It will raise a WlanConnError, if an error occurs.
    """

    connected = False
    # For each interface
    for iface in self.ifaces:
      print "Interface: %s" % (iface.strInterfaceDescription)
      if iface.isState == wlan_interface_state_connected.value:
        # Query for the connection status.
        pConnAttributes = pointer(WLAN_CONNECTION_ATTRIBUTES())
        dataSize = DWORD()
        opCodeType = wlan_opcode_value_type_invalid
        ret = WlanQueryInterface(self.hClient, byref(iface.InterfaceGuid), 
          wlan_intf_opcode_current_connection, None, byref(dataSize), byref(pConnAttributes),
          byref(opCodeType))
        if ret != ERROR_SUCCESS:
          raise WlanConnError(FormatError(ret))

        try:
          # Are we connected?
          if pConnAttributes.contents.isState == wlan_interface_state_connected.value:
            print "Status: connected to network"
            pAttributes = pConnAttributes.contents.wlanAssociationAttributes
            currentSSID = ''.join(map(chr, 
              pAttributes.dot11Ssid.ucSSID[:pAttributes.dot11Ssid.uSSIDLength]))
              
            connected = connected or (currentSSID.upper() == ssid.upper())
          else:
            print "Status: not active"
        finally:
            WlanFreeMemory(pConnAttributes)
            
    return connected

# Main
if sys.argv[1:]:
  # Retrieves the ssid name.
  ssid = sys.argv[1]
  try:
    info = WlanConnInfo()
    if info.isConnected(ssid):
      print "Associated to %s" % ssid
    else:
      print "Not associated to %s" % ssid
  except WlanConnError as err:
    print err
    
else:
  print 'Usage: WlanConnInfo.py SSID'
