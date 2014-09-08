/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.idm.srvc.synch.srcadapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.synch.domain.SynchReviewEntity;
import org.openiam.idm.srvc.synch.dto.*;
import org.springframework.stereotype.Component;

/**
 * Scan AD for any new records, changed users, or delete operations and then synchronizes them back into OpenIAM.
 * @author suneet
 *
 */
@Component
public class ActiveDirectoryAdapter extends AbstractSrcAdapter{
    private static final Log log = LogFactory.getLog(ActiveDirectoryAdapter.class);

	protected Map<String,LineObject> lineMap = new HashMap<String,LineObject>();
	protected LineObject lineHeader = new LineObject();

    @Override
    public Response testConnection(SynchConfig config) {
        throw new UnsupportedOperationException("Not implemented yet..");
    }

    @Override
    public SyncResponse startSynch(final SynchConfig config) {
        return startSynch(config, null, null);
    }

    @Override
    public SyncResponse startSynch(SynchConfig config, SynchReviewEntity sourceReview, SynchReviewEntity resultReview) {
        throw new UnsupportedOperationException("Not implemented yet..");
	}

}
