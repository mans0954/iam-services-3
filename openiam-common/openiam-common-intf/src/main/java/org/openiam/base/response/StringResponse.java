package org.openiam.base.response;

import org.openiam.base.ws.Response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by alexander on 12/08/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StringResponse", propOrder = {
})
public class StringResponse extends BaseDataResponse<String> {
}
