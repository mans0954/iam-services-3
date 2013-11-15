def it = user.roles.iterator()
if (!it.hasNext()) {
	output = "USR_SEC_DOMAIN"
} else {
	def rl = it.next()
	println("Found role " + rl.roleName)
    output = rl.serviceId?: "USR_SEC_DOMAIN"
}
