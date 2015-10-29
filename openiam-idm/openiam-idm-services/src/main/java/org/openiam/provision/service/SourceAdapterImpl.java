package org.openiam.provision.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.SearchParam;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.dto.*;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import java.beans.Beans;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by zaporozhec on 10/29/15.
 */
@WebService(endpointInterface = "org.openiam.provision.service.SourceAdapter", targetNamespace = "http://www.openiam.org/service/provision", portName = "SourceAdapterServicePort", serviceName = "SourceAdapterService")
@Component("sourceAdapter")
public class SourceAdapterImpl implements SourceAdapter {

    @Autowired
    private ProvisioningDataService provisioningDataService;
    @Autowired
    private UserDataWebService userDataService;

    @Autowired
    private UserDozerConverter userDozerConverter;

    @Autowired
    protected SysConfiguration sysConfiguration;

    final static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");

    @Override
    public SourceAdapterResponse perform(SourceAdapterRequest request) {
        SourceAdapterResponse response = new SourceAdapterResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        ProvisionUser pUser = null;
        try {
            pUser = this.convertToProvisionUser(request);
        } catch (Exception e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setError(e.getMessage());
            return response;
        }
        switch (request.getAction()) {
            case ADD: {
                pUser.setOperation(AttributeOperationEnum.ADD);
                ProvisionUserResponse provisionUserResponse = provisioningDataService.addUser(pUser);
                response.setStatus(provisionUserResponse.getStatus());
                response.setError(provisionUserResponse.getErrorText());
                break;
            }
            case MODIFY: {
                pUser.setOperation(AttributeOperationEnum.REPLACE);
                ProvisionUserResponse provisionUserResponse = provisioningDataService.modifyUser(pUser);
                response.setStatus(provisionUserResponse.getStatus());
                response.setError(provisionUserResponse.getErrorText());
                break;
            }
            case DELETE: {
                ProvisionUserResponse provisionUserResponse = provisioningDataService.deleteByUserWithSkipManagedSysList(pUser.getId(), UserStatusEnum.REMOVE, "3000", null);
                response.setStatus(provisionUserResponse.getStatus());
                response.setError(provisionUserResponse.getErrorText());
                break;
            }
            case ENABLE: {
                Response resp = provisioningDataService.disableUser(pUser.getId(), false, "3000");
                response.setStatus(resp.getStatus());
                response.setError(resp.getErrorText());
                break;
            }
            case DISABLE: {
                Response resp = provisioningDataService.disableUser(pUser.getId(), true, "3000");
                response.setStatus(resp.getStatus());
                response.setError(resp.getErrorText());
                break;
            }
            case NO_CHANGE: {
                break;
            }
            default:
                response.setStatus(ResponseStatus.FAILURE);
                response.setError("Operation not supported");
        }
        return response;
    }

    private ProvisionUser convertToProvisionUser(SourceAdapterRequest request) throws Exception {
        ProvisionUser pUser = new ProvisionUser(this.getUser(request));
        if (StringUtils.isNotBlank(request.getEmployeeId())) {
            pUser.setEmployeeId(getNULLValue(request.getEmployeeId()));
        }
        if (StringUtils.isNotBlank(request.getFirstName() )) {
            pUser.setFirstName(getNULLValue(request.getFirstName()));
        }
        if (StringUtils.isNotBlank(request.getLastName() )) {
            pUser.setLastName(getNULLValue(request.getLastName()));
        }
        if (request.getLastDate() !=null) {
            pUser.setLastDate("NULL".equals(request.getLastDate()) ? null : sdf.parse(request.getLastDate()));
        }
        if (request.getStartDate()  !=null) {
            pUser.setStartDate("NULL".equals(request.getStartDate()) ? null : sdf.parse(request.getStartDate()));
        }
        if (StringUtils.isNotBlank(request.getMaidenName() )) {
            pUser.setMaidenName(getNULLValue(request.getMaidenName()));
        }
        if (StringUtils.isNotBlank(request.getMiddleName() )) {
            pUser.setMiddleInit(getNULLValue(request.getMiddleName()));
        }
        if (StringUtils.isNotBlank(request.getNickname() )) {
            pUser.setNickname(getNULLValue(request.getNickname()));
        }
        if (StringUtils.isNotBlank(request.getPrefix() )) {
            pUser.setPrefix(getNULLValue(request.getPrefix()));
        }
        if (request.getSecondaryStatus()!=null) {
            pUser.setSecondaryStatus(request.getSecondaryStatus());
        }
        if (request.getStatus()!=null) {
            pUser.setStatus(request.getStatus());
        }
        if (StringUtils.isNotBlank(request.getSex() )) {
            pUser.setSex(getNULLValue(request.getSex()));
        }
        if (StringUtils.isNotBlank(request.getSuffix() )) {
            pUser.setSuffix(getNULLValue(request.getSuffix()));
        }
        if (StringUtils.isNotBlank(request.getTitle())) {
            pUser.setTitle(getNULLValue(request.getTitle()));
        }
        if (StringUtils.isNotBlank(request.getUserTypeId())) {
            pUser.setUserTypeInd(getNULLValue(request.getUserTypeId()));
        }
        //convert
        return pUser;
    }

    private String getNULLValue(String source) {
        return (source == null || "NULL".equals(source)) ? null : source;
    }

    private User getUser(SourceAdapterRequest request) throws Exception {

        UserSearchBean searchBean = new UserSearchBean();
        SourceAdapterKeyEnum matchAttrName = request.getKey().getName();
        String matchAttrValue = request.getKey().getValue();
        if ((matchAttrName == null || StringUtils.isBlank(matchAttrValue)) &&
                !SourceAdapterOperationEnum.ADD.equals(request.getAction())) {
            throw new Exception("Match Key is empty");
        }
        if (SourceAdapterKeyEnum.USERID.equals(matchAttrName)) {
            searchBean.setKey(matchAttrValue);
            searchBean.setUserId(matchAttrValue);
        } else if (SourceAdapterKeyEnum.PRINCIPAL.equals(matchAttrName)) {
            LoginSearchBean lsb = new LoginSearchBean();
            lsb.setLoginMatchToken(new SearchParam(matchAttrValue, MatchType.EXACT));
            lsb.setManagedSysId(sysConfiguration.getDefaultManagedSysId());
            searchBean.setPrincipal(lsb);
        } else if (SourceAdapterKeyEnum.EMAIL.equals(matchAttrName)) {
            searchBean.setEmailAddressMatchToken(new SearchParam(matchAttrValue, MatchType.EXACT));
        } else if (SourceAdapterKeyEnum.EMPLOYEE_ID.equals(matchAttrName)) {
            searchBean.setEmployeeIdMatchToken(new SearchParam(matchAttrValue, MatchType.EXACT));
        }
        searchBean.setDeepCopy(true);
        List<User> userList = userDataService.findBeans(searchBean, 0, Integer.MAX_VALUE);
        if (CollectionUtils.isNotEmpty(userList))
            return userList.get(0);
        else if (SourceAdapterOperationEnum.ADD.equals(request.getAction())) {
            new User();
        } else {
            throw new Exception("No user with such Identifier=" + matchAttrName + ":" + matchAttrValue);
        }
        return null;
    }

}
