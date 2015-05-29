<#
.SYNOPSIS
    This function checks that the given folders and their corresponding baseline ACLs match
    against the existing ACLs.
.DESCRIPTION
    This function checks that the given folders and their corresponding baseline ACLs match
    against the existing ACLs. It makes use of Check-Compliance.
    
    The folders and baseline ACLs are read from a CSV file that must have the following headers:
    Folder : Path to the folder to check ACLs.
    Owner  : User or group account owner of the folder. Only the first appearance of this
             attribute will be taken into account.
    IdentityReference : User or group account associated to the access rule.
    FileSystemRights : Type of operation associated with the access rule.
    AccessControlType : Specifies whether to allow or deny the operation.
    
    All these headers will allow to build the security descriptor and, specifically the last
    three, the access rules (ACLs).
.PARAMETER config
    The path to file that contains the folders and their corresponding baseline ACLs 
    that they must comply with.
.EXAMPLE
    C:\PS>Check-AclBatch -config "C:\My baseline.csv"
.LINK
   Check-Acl
    Import-Csv
    https://msdn.microsoft.com/en-us/library/system.security.principal.ntaccount(v=vs.110).aspx
    https://msdn.microsoft.com/en-us/library/system.security.accesscontrol.filesystemrights(v=vs.110).aspx
    https://msdn.microsoft.com/en-us/library/w4ds5h86(v=vs.110).aspx
.NOTES
    Author: Dario B. (darizotas at gmail dot com)
    Date:   May 26, 2015
        
    Copyright 2015 Dario B. darizotas at gmail dot com
    This software is licensed under a new BSD License.
    Unported License. http://opensource.org/licenses/BSD-3-Clause
#>
Function Check-AclBatch {
    Param(
        [Parameter(Mandatory=$true)]
        [string]$config
    )
    
    if (Test-Path -path $config) {
        $baseline = @{}
        Write-Host "[*] Importing baseline..."
        try {
            Import-Csv $config | foreach {
                # Only created the first time.
                if ($baseline[$_.Folder] -ne $null) {
                    $sd = $baseline[$_.Folder]
                } else {
                    Write-Host "[+] Creating security descriptor for $($_.Folder) ..."
                    $sd = New-Object System.Security.AccessControl.DirectorySecurity
                    # Owner
                    if ($_.Owner) {
                        Write-Host "`tSetting owner..."
                        $owner = New-Object System.Security.Principal.NTAccount($_.Owner)
                        $sd.setOwner($owner)
                    }
                    
                    $baseline[$_.Folder] = $sd
                }
                Write-Host "`tAdding ACL..."
                # ACL
                $user = New-Object System.Security.Principal.NTAccount($_.IdentityReference)
                $rights = [System.Security.AccessControl.FileSystemRights]$_.FileSystemRights
                $type = [System.Security.AccessControl.AccessControlType]$_.AccessControlType
                # Hardcoded
                $InheritanceFlag = [System.Security.AccessControl.InheritanceFlags]"ContainerInherit, ObjectInherit" 
                $PropagationFlag = [System.Security.AccessControl.PropagationFlags]::None
                $ace = New-Object System.Security.AccessControl.FileSystemAccessRule `
                    ($user, $rights, $InheritanceFlag, $PropagationFlag, $type)
                $sd.AddAccessRule($ace)
            }
            Write-Host "[ok] Baseline imported"

        } Catch [system.exception] {
            Write-Host -f red "[err] Baseline format is incorrect."            Write-Host -f red "Headers expected:"
            Write-Host -f red "`tFolder`t: Path to the folder to check ACLs"
            Write-Host -f red "`tOwner`t: User or group account owner of the folder"
            Write-Host -f red "`t`thttps://msdn.microsoft.com/en-us/library/system.security.principal.ntaccount(v=vs.110).aspx"
            Write-Host -f red "`tIdentityReference`t: User or group account associated to the access rule"
            Write-Host -f red "`t`thttps://msdn.microsoft.com/en-us/library/system.security.principal.ntaccount(v=vs.110).aspx"
            Write-Host -f red "`tFileSystemRights`t: type of operation associated with the access rule"
            Write-Host -f red "`t`thttps://msdn.microsoft.com/en-us/library/system.security.accesscontrol.filesystemrights(v=vs.110).aspx"
            Write-Host -f red "`tAccessControlType`t: specifies whether to allow or deny the operation"
            Write-Host -f red "`t`thttps://msdn.microsoft.com/en-us/library/w4ds5h86(v=vs.110).aspx"
        }
        
        Write-Host "[*] Checking compliance..."
        $baseline.GetEnumerator() | foreach { 
            Check-Acl -path $_.key -baseline $_.value 
        }
    } else {
        Write-Host -f red "[err] $config does not exist."
    }
}

<#
.SYNOPSIS
    This function checks that the existing ACLs of the given folder comply with the 
    given baseline ACLs.
.DESCRIPTION
    This function checks that the existing ACLs (access rules) defined in the given folder 
    comply with the given baseline ACLs.
    The check focuses on validating the properties: IdentityReference, FileSystemRights and 
    AccessControlType of the access rule are exactly the same. 
    If included in the baseline, it also checks for the Owner.
    
    It will highlight in red those baseline ACLs that do not match.
    It will highlight in blue the existing ACLs that do not belong to the baseline.
.PARAMETER path
    The path to the folder/file to check.
.PARAMETER baseline
    Baseline security descriptor to comply with.
    https://msdn.microsoft.com/en-us/library/system.security.accesscontrol.directorysecurity(v=vs.110).aspx
.EXAMPLE
    C:\PS>$owner = New-Object System.Security.Principal.NTAccount("BUILTIN\Administrators") 
    C:\PS>$securityDescriptor = New-Object System.Security.AccessControl.DirectorySecurity
    C:\PS>$securityDescriptor.setOwner($owner)
    C:\PS>$InheritanceFlag = [System.Security.AccessControl.InheritanceFlags]"ContainerInherit, ObjectInherit" 
    C:\PS>$PropagationFlag = [System.Security.AccessControl.PropagationFlags]::None 
    C:\PS>$user = New-Object System.Security.Principal.NTAccount("DOMAIN\OtherAccount") 
    C:\PS>$rights = [System.Security.AccessControl.FileSystemRights]"ReadAndExecute" 
    C:\PS>$objType =[System.Security.AccessControl.AccessControlType]::Allow 
    C:\PS>$objACE = New-Object System.Security.AccessControl.FileSystemAccessRule `
    ($user, $rights, $InheritanceFlag, $PropagationFlag, $objType) 
    C:\PS>$securityDescriptor.AddAccessRule($objACE) 
    C:\PS>C:\PS>$user = New-Object System.Security.Principal.NTAccount("DOMAIN\YourAccount") 
    C:\PS>$rights = [System.Security.AccessControl.FileSystemRights]"Write" 
    C:\PS>$objType =[System.Security.AccessControl.AccessControlType]::Allow 
    C:\PS>$objACE = New-Object System.Security.AccessControl.FileSystemAccessRule `
    ($user, $rights, $InheritanceFlag, $PropagationFlag, $objType) 
    C:\PS>$securityDescriptor.AddAccessRule($objACE) 
    C:\PS>Check-Acl -path "C:\My_Folder" -baseline $securityDescriptor
.EXAMPLE
    C:\PS>$owner = New-Object System.Security.Principal.NTAccount("BUILTIN\Administrators") 
    C:\PS>$securityDescriptor = New-Object System.Security.AccessControl.DirectorySecurity
    C:\PS>$securityDescriptor.setOwner($owner)
    C:\PS>$user = New-Object System.Security.Principal.NTAccount("DOMAIN\YourAccount") 
    C:\PS>$rights = [System.Security.AccessControl.FileSystemRights]"FullControl" 
    C:\PS>$InheritanceFlag = [System.Security.AccessControl.InheritanceFlags]"ContainerInherit, ObjectInherit" 
    C:\PS>$PropagationFlag = [System.Security.AccessControl.PropagationFlags]::None 
    C:\PS>$objType =[System.Security.AccessControl.AccessControlType]::Allow 
    C:\PS>$objACE = New-Object System.Security.AccessControl.FileSystemAccessRule `
    ($user, $rights, $InheritanceFlag, $PropagationFlag, $objType) 
    C:\PS>$securityDescriptor.AddAccessRule($objACE) 
    C:\PS>Check-Acl -path "\\My_Remote_Folder" -baseline $securityDescriptor
.LINK
   https://technet.microsoft.com/en-us/library/ff730951
    https://technet.microsoft.com/en-us/library/cc781716(v=ws.10).aspx
    http://blogs.technet.com/b/josebda/archive/2010/11/09/how-to-handle-ntfs-folder-permissions-security-descriptors-and-acls-in-powershell.aspx
    Get-Acl
    Set-Acl
.NOTES
    Author: Dario B. (darizotas at gmail dot com)
    Date:   May 26, 2015
        
    Copyright 2015 Dario B. darizotas at gmail dot com
    This software is licensed under a new BSD License.
    Unported License. http://opensource.org/licenses/BSD-3-Clause
#>
Function Check-Acl {
    Param(
        [Parameter(Mandatory=$true)]
        [string]$path,
        [Parameter(Mandatory=$true)]
        [System.Security.AccessControl.DirectorySecurity]$baseline
    )
    
    Write-Host "[-] Checking ACLs for $path"
    # First things, first. The ACL.
    $acl = Get-Acl -Path $path 
    # Ownership
    if ($baseline.Owner) {
        Write-Host "[>>] Validating baseline Owner:",$baseline.Owner
        if ($acl.Owner -eq $baseline.Owner) {
            write-host "`t[ok]"
        } else {
            Write-Host -f red "`t[err] defined Owner is different:",$acl.Owner
        }
    }
    # Array that saves those rules processed.
    $matches = @()
    # Access
    foreach ($rule in $baseline.Access) {
        Write-Host "[>>] Validating baseline DACL:"
        Write-Host "`tFileSystemRights`t:",$rule.FileSystemRights
        Write-Host "`tAccessControlType`t:",$rule.AccessControlType
        Write-Host "`tIdentityReference`t:",$rule.IdentityReference
        # Search each rule defined in the baseline within the current DACLs
        $r = $acl.Access | where {$_.IdentityReference -eq $rule.IdentityReference -and `
            $_.AccessControlType -eq $rule.AccessControlType -and `
            $_.FileSystemRights -eq $rule.FileSystemRights }
            
        if ($r -ne $null) {
            write-host "`t[ok]"

            # RemoveAccessRule does not work if $r is inherited. Then, we have to keep it in an array.
            #$acl.RemoveAccessRule($r)
            $matches += $r
        } else {
            Write-Host -f red "`t[err] it does not exist"
        }
    }
    # Remaining rules
    foreach ($rule in $acl.Access) {
        # Only those rules not existing previously
        $r = $matches | where {$_.IdentityReference -eq $rule.IdentityReference -and `
            $_.AccessControlType -eq $rule.AccessControlType -and `
            $_.FileSystemRights -eq $rule.FileSystemRights }
        if ($r -eq $null) {
            Write-Host "[<<] Existing DACL:"
            Write-Host "`tFileSystemRights`t:",$rule.FileSystemRights
            Write-Host "`tAccessControlType`t:",$rule.AccessControlType
            Write-Host "`tIdentityReference`t:",$rule.IdentityReference
            Write-Host -f blue "`t[warn] it is not defined in the baseline"
        }
    }
    Write-Host "[+] done"
}
