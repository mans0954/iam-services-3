import java.util.ArrayList;
import java.util.List;
import org.openiam.idm.srvc.org.dto.Organization;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.openiam.base.BaseAttribute;
import org.openiam.base.BaseAttributeContainer;


BaseAttributeContainer attributeContainer = new BaseAttributeContainer();

def orgManager = context.getBean("orgManager")

/* Replace base DN with your Base DN */
String orgBaseDN = "ou=affiliations,dc=openiam,dc=com";

List<String> orgAffiliationList = new ArrayList<String>();
def List<Organization> affiliationList = user.userAffiliations;

println("Executing ldapUserAffiliations.groovy...");
println(" -- user affilations =" + affiliationList);


if (affiliationList != null) {
	if (affiliationList.size() > 0)  {
		for (Organization o : affiliationList) {
			// get the name of the org
			
			Organization affiliationOrg = orgManager.getOrganization(o.orgId)
			

			String qualifiedAffiliationName = "cn=" + affiliationOrg.organizationName + "," + orgBaseDN;
			
			println("-- Affiliating user to :" + qualifiedAffiliationName);
			
			attributeContainer.getAttributeList().add(new BaseAttribute(qualifiedAffiliationName, qualifiedAffiliationName, o.operation));
			
		
		}
		output = attributeContainer;
	}else {
		output = null;
	}
}else {
	output = null;
}


