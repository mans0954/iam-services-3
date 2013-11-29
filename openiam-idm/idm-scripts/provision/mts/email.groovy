output = null
def it = user.emailAddresses?.iterator()
while (it?.hasNext()) {
    def addr = it.next()
    if (addr.metadataTypeId == 'PRIMARY_EMAIL') {
        output = addr.emailAddress
        return
    }
}
