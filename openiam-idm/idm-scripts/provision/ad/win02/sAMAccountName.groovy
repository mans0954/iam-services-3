if("EXIST" == targetSystemIdentityStatus) {
    output = null
    return output
}
def attr = user.getUserAttributes().get("sAMAccountName")
if (attr?.value) {
    output = attr.value
}else {
    output = user.employeeId
}