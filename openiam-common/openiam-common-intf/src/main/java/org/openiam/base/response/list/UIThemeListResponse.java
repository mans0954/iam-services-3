package org.openiam.base.response.list;

import org.openiam.idm.srvc.ui.theme.dto.UITheme;

/**
 * Created by alexander on 06/12/16.
 */
public class UIThemeListResponse extends BaseListResponse<UITheme> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UIThemeListResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
