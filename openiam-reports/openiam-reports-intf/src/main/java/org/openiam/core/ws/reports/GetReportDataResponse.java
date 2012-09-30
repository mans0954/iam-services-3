package org.openiam.core.ws.reports;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.openiam.base.ws.Response;
import org.openiam.core.dto.reports.TestReportUserDto;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "getInfoByReportNameResult"
})
public class GetReportDataResponse extends Response {

    @XmlElement(name = "GetDataByReportNameResult")
    protected GetReportDataResponse.GetInfoByReportNameResult getInfoByReportNameResult;

    public GetInfoByReportNameResult getGetInfoByReportNameResult() {
        return getInfoByReportNameResult;
    }

    public void setGetInfoByReportNameResult(GetInfoByReportNameResult getInfoByReportNameResult) {
        this.getInfoByReportNameResult = getInfoByReportNameResult;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "content"
    })
    @XmlSeeAlso({
    TestReportUserDto.class
    })
    public static class GetInfoByReportNameResult {

        @XmlMixed
        @XmlAnyElement(lax = true)
        protected List<Object> content;

        public void setContent(List<Object> content) {
            this.content = content;
        }

        public List<Object> getContent() {
            if (content == null) {
                content = new ArrayList<Object>();
            }
            return this.content;
        }

    }

}
