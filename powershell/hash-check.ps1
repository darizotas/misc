# This script is a wrapper of the Get-Hash cmdlet of the PSCX Module and generates
# an output file using hash-check format.
#
# Copyright 2013 Dario B. darizotas at gmail dot com
# This software is licensed under a new BSD License. 
# Unported License. http://opensource.org/licenses/BSD-3-Clause


[CmdletBinding()]
Param(
  [Parameter(Mandatory=$true)]
  [string]$source,

  #[Parameter(Mandatory=$True)] 
  [ValidateSet("SHA1", "MD5", "SHA256", "SHA512", "RIPEMD160")]
  [string]$algorithm = "SHA1")

# Does the path given exists?
if (Test-Path -Path $source) {
  Write-Host "Generating $algorithm hashes..."
  
  # Calculate the hashes of the files contained in the given path
  $hashes = dir $source -Recurse | Where-Object {!$_.psiscontainer} | Get-Hash -Algorithm $algorithm
  
  # Output path and file where to write those hashes.
  if (Test-Path -Path $source -PathType leaf) {
    $parent = Split-Path $source -parent
    $outfile = $source + '.' + $algorithm
  } else {
    $parent = Split-Path $source -parent
    $outfile = $parent + '\' + (Split-Path $source -Leaf) + '.' + $algorithm
  }
  $stream = [System.IO.StreamWriter] $outfile

  # Loop the hashes and writes them using hash-check format.
  foreach ($h in $hashes) {
    $path = $h.Path.Replace($parent + '\', "*")
    $stream.WriteLine($h.HashString + "`t" + $path)
  }
  $stream.close()
  
  Write-Host "HashCheck file [$outfile] has been successfully created."
} else {
  Write-Host "The given path $source does not exist"
}