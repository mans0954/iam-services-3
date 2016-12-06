package org.openiam.base.response.data;

import org.openiam.idm.srvc.ui.theme.dto.UITheme;

/**
 * Created by alexander on 06/12/16.
 */
public class UIThemeResponse extends BaseDataResponse<UITheme> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UIThemeResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
