
def String baseURL = req.getNotificationParam("BASE_URL").valueObj
def String token = req.getNotificationParam("TOKEN").valueObj



emailStr = "Dear " + user.firstName + " " + user.lastName + ": \n\n" +
 	"A  request has been created to reset your password and unlock your account. Please click on the URL below to answer a set of identity verification questions. \n" + 
 	"After your identity has been successfully verified, you will be to enter a new password and unlock your account. \n\n" + 
 	"\n\n" +
 	baseURL + "/pub/secureIdentityVerification.selfserve?token="  +  token + " \n\n" +
 	"If you did not create this request, then please contact your security administrator. \n" 	
 	
output=emailStr