package org.openiam.idm.srvc.continfo.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.continfo.domain.EmailEntity;

import java.util.List;

/**
 * Created by vitalia on 5/31/16.
 */
public interface EmailDAO extends BaseDao<EmailEntity, String> {

     List<EmailEntity> getEmailsForUser (final String userId, final int from, final int size);





}
