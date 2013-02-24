"""Proof of concept to get the SSID of the current Wlan connection on a Windows XP.

It uses the wlanapi.wlanapiwrapper module.

Copyright 2013 Dario B. darizotas at gmail dot com
This software is licensed under a new BSD License. 
Unported License. http://opensource.org/licenses/BSD-3-Clause
"""
from wlanapi.wlanapiwrapper import *

def customresize(array, new_size):
  """Returns a resized instance of the given array from the memory address where it is
     located."""
  return (array._type_*new_size).from_address(addressof(array))
  
  
# Opens handle for Wlan API
CLIENT_VERSION_XP = 1
currentVersion = DWORD()
hClient = HANDLE()
ret = WlanOpenHandle(CLIENT_VERSION_XP, None, byref(currentVersion), byref(hClient))
if ret != ERROR_SUCCESS:
  exit(FormatError(ret))
# Finds the interfaces
pInterfaceList = pointer(WLAN_INTERFACE_INFO_LIST())
ret = WlanEnumInterfaces(hClient, None, byref(pInterfaceList))
if ret != ERROR_SUCCESS:
  exit(FormatError(ret))

try:
  # Now we know how many interfaces are. Let's resize the interface info.
  ifaces = customresize(pInterfaceList.contents.InterfaceInfo, 
    pInterfaceList.contents.dwNumberOfItems)
  # For each interface
  for iface in ifaces:
    print "Interface: %s" % (iface.strInterfaceDescription)
    if iface.isState == wlan_interface_state_connected.value:
      # Query for the connection status.
      pConnAttributes = pointer(WLAN_CONNECTION_ATTRIBUTES())
      dataSize = DWORD()
      opCodeType = wlan_opcode_value_type_invalid
      ret = WlanQueryInterface(hClient, byref(iface.InterfaceGuid), 
        wlan_intf_opcode_current_connection, None, byref(dataSize), byref(pConnAttributes),
        byref(opCodeType))
      if ret != ERROR_SUCCESS:
          exit(FormatError(ret))

      try:
        # Are we connected?
        if pConnAttributes.contents.isState == wlan_interface_state_connected.value:
          pAttributes = pConnAttributes.contents.wlanAssociationAttributes
          print "SSID: %s" % ''.join(map(chr, pAttributes.dot11Ssid.ucSSID[:pAttributes.dot11Ssid.uSSIDLength]))
      finally:
          WlanFreeMemory(pConnAttributes)
finally:
  WlanFreeMemory(pInterfaceList)