function clearCacheCredentials() {
    // Clear HTTP Authentication and HTTPS client certificates
    // IE6sp1 or later
    // http://msdn.microsoft.com/en-us/library/ms536979.aspx
    if (!document.execCommand('ClearAuthenticationCache', false)) {
    }
}