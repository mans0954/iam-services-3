output = null
def it = user.addresses?.iterator()
if (it?.hasNext()) {
    output = it?.next()?.state?: null
}