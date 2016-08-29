package org.openiam.service.integration.connector;

import java.util.List;

import org.junit.Test;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorSearchBean;
import org.openiam.service.integration.AbstractKeyNameServiceTest;

public class ConnectorServiceTest extends AbstractKeyNameServiceTest<ProvisionConnectorDto, ProvisionConnectorSearchBean> {
	
	@Override
	protected ProvisionConnectorDto newInstance() {
		return new ProvisionConnectorDto();
	}

	@Override
	protected ProvisionConnectorSearchBean newSearchBean() {
		return new ProvisionConnectorSearchBean();
	}

	@Override
	protected Response save(ProvisionConnectorDto t) {
		return provisionConnectorWebServiceClient.save(t);
	}

	@Override
	protected Response delete(ProvisionConnectorDto t) {
		return provisionConnectorWebServiceClient.removeProvisionConnector(t.getId());
	}

	@Override
	protected ProvisionConnectorDto get(String key) {
		return provisionConnectorWebServiceClient.getProvisionConnector(key);
	}

	@Override
	public List<ProvisionConnectorDto> find(
			ProvisionConnectorSearchBean searchBean, int from, int size) {
		return provisionConnectorWebServiceClient.getProvisionConnectors(searchBean, from, size);
	}

	@Test
	public void foo() {
		
	}
}
