package org.openiam.provision.service;

import org.openiam.provision.dto.srcadapter.SourceAdapterRequest;

/**
 * Created by zaporozhec on 4/10/16.
 */
public interface SourceAdapterPreProcessor  {
    public int perform(SourceAdapterRequest request) throws Exception;
}
