
attrVal = attribute.value
if (attrVal) {
    def names = attrVal.value.split(', ')
    if (names.size() > 0) {
        pUser.lastName = names[0]
    }
    if (names.size() > 1) {
        def names2 = names[1].split(' ')
        if (names2.size() > 0) {
            pUser.firstName = names2[0]
        }
        if (names2.size() > 1) {
            pUser.middleInit = names2[1]
        }
    }
}
output = ""