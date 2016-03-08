package org.openiam.service.integration.connector;

import java.util.List;

import org.junit.Test;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorSearchBean;
import org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService;
import org.openiam.service.integration.AbstractKeyNameServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ConnectorServiceTest extends AbstractKeyNameServiceTest<ProvisionConnectorDto, ProvisionConnectorSearchBean> {

	@Autowired
	@Qualifier("connectorServiceClient")
	private ProvisionConnectorWebService service;
	
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
		return service.save(t);
	}

	@Override
	protected Response delete(ProvisionConnectorDto t) {
		return service.removeProvisionConnector(t.getId());
	}

	@Override
	protected ProvisionConnectorDto get(String key) {
		return service.getProvisionConnector(key);
	}

	@Override
	public List<ProvisionConnectorDto> find(
			ProvisionConnectorSearchBean searchBean, int from, int size) {
		return service.getProvisionConnectors(searchBean, from, size);
	}

	@Test
	public void foo() {
		
	}
}
