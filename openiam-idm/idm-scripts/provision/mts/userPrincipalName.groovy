
def userPrincipalName = user.userAttributes?.get('userPrincipalName')?.value

output = userPrincipalName ? userPrincipalName : lg.login + '@MTSAllstream.com' 


