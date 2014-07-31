package org.openiam.idm.srvc.synch.service;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.SynchReviewDozerConverter;
import org.openiam.dozer.converter.SynchReviewRecordDozerConverter;
import org.openiam.idm.searchbeans.SynchReviewSearchBean;
import org.openiam.idm.srvc.synch.domain.SynchReviewEntity;
import org.openiam.idm.srvc.synch.domain.SynchReviewRecordEntity;
import org.openiam.idm.srvc.synch.domain.SynchReviewRecordValueEntity;
import org.openiam.idm.srvc.synch.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
@Service("synchReviewService")
public class SynchReviewServiceImpl implements SynchReviewService {

    @Autowired
    SynchReviewDozerConverter synchReviewDozerConverter;

    @Autowired
    SynchReviewRecordDozerConverter synchReviewRecordDozerConverter;

    @Autowired
    SynchReviewDAO synchReviewDao;

    @Autowired
    SynchReviewRecordDAO synchReviewRecordDao;

    @Autowired
    protected IdentitySynchService synchService;

    @Override
    @Transactional(readOnly = true)
    public List<SynchReview> findBeans(SynchReviewSearchBean searchBean, int from, int size) {
        List<SynchReviewEntity> entities = synchReviewDao.getByExample(searchBean, from, size);
        return synchReviewDozerConverter.convertToDTOList(entities, searchBean.isDeepCopy());
    }

    @Override
    @Transactional(readOnly = true)
    public Integer countBeans(SynchReviewSearchBean searchBean) {
        return synchReviewDao.count(searchBean);
    }

    @Override
    public Response deleteByIds(List<String> deleteIds) {
        Response resp = new Response(ResponseStatus.SUCCESS);
        if (CollectionUtils.isNotEmpty(deleteIds)) {
            for (String id: deleteIds) {
                SynchReviewEntity entity = synchReviewDao.findById(id);
                synchReviewDao.delete(entity);
            }
        }
        return resp;
    }

    @Override
    public Response delete(String synchReviewId) {
        Response resp = new Response(ResponseStatus.SUCCESS);
        if (StringUtils.isNotEmpty(synchReviewId)) {
            SynchReviewEntity entity = synchReviewDao.findById(synchReviewId);
            synchReviewDao.delete(entity);
        }
        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public SynchReviewRecord getHeaderReviewRecord(String synchReviewId) {
        return synchReviewRecordDozerConverter.convertToDTO(
                synchReviewRecordDao.getHeaderReviewRecord(synchReviewId), true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SynchReviewRecord> getRecordsBySynchReviewId(String synchReviewId, int from, int size) {
        return synchReviewRecordDozerConverter.convertToDTOList(
                synchReviewRecordDao.getRecordsBySynchReviewId(synchReviewId, from, size), true);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getRecordsCountBySynchReviewId(String synchReviewId) {
        return synchReviewRecordDao.getRecordsCountBySynchReviewId(synchReviewId);
    }

    @Override
    public SynchReviewResponse updateSynchReview(SynchReviewRequest synchReviewRequest) {
        Date modifyTime = new Date();
        SynchReviewResponse response = new SynchReviewResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        SynchReviewEntity entity = synchReviewDao.findById(synchReviewRequest.getSynchReviewId());
        if (entity == null) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(String.format("Synch Review with ID %s is not found",
                    synchReviewRequest.getSynchReviewId()));
            return response;
        }
        if (CollectionUtils.isNotEmpty(synchReviewRequest.getReviewRecords())) {
            updateRecordsValues(entity, synchReviewRequest.getReviewRecords());
        }
        entity.setSkipSourceValid(synchReviewRequest.isSkipSourceValid());
        entity.setSkipRecordValid(synchReviewRequest.isSkipRecordValid());
        entity.setModifyTime(modifyTime);
        synchReviewDao.save(entity);

        return response;
    }

    @Override
    public SynchReviewResponse executeSynchReview(SynchReviewRequest synchReviewRequest) {
        Date executeTime = new Date();
        SynchReviewResponse response = new SynchReviewResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        SynchReviewEntity entity = synchReviewDao.findById(synchReviewRequest.getSynchReviewId());
        if (entity == null) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(String.format("Synch Review with ID %s is not found",
                    synchReviewRequest.getSynchReviewId()));
            return response;
        }
        if (CollectionUtils.isNotEmpty(synchReviewRequest.getReviewRecords())) {
            updateRecordsValues(entity, synchReviewRequest.getReviewRecords());
        }

        entity.setSkipSourceValid(synchReviewRequest.isSkipSourceValid());
        entity.setSkipRecordValid(synchReviewRequest.isSkipRecordValid());
        entity.setModifyTime(executeTime);
        entity.setExecTime(executeTime);

        synchService.startSynchReview(entity); // Starts synch from SynchReview a source
        synchReviewDao.save(entity);
        return response;
    }

    private void updateRecordsValues(SynchReviewEntity entity, List<SynchReviewRecord> records) {
        if (CollectionUtils.isNotEmpty(records)) {
            for (SynchReviewRecord rec : records) {
                String synchReviewRecordId = rec.getSynchReviewRecordId();
                if (StringUtils.isNotEmpty(synchReviewRecordId)) {
                    SynchReviewRecordEntity recordEntity = null;
                    if (entity != null && CollectionUtils.isNotEmpty(entity.getReviewRecords())) {
                        for (SynchReviewRecordEntity r : entity.getReviewRecords()) {
                            if (StringUtils.equals(r.getSynchReviewRecordId(), synchReviewRecordId)) {
                                recordEntity = r;
                                break;
                            }
                        }
                    }
                    if (recordEntity != null) {
                        if (CollectionUtils.isNotEmpty(recordEntity.getReviewValues())) {
                            for (SynchReviewRecordValueEntity val: recordEntity.getReviewValues()) {
                                if (CollectionUtils.isNotEmpty(rec.getReviewValues())) {
                                    for (SynchReviewRecordValue newVal : rec.getReviewValues()) {
                                        if (StringUtils.equals(newVal.getSynchReviewRecordValueId(),
                                                val.getSynchReviewRecordValueId())) {
                                            if (!StringUtils.equals(val.getValue(), newVal.getValue())) {
                                                val.setValue(newVal.getValue());
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
