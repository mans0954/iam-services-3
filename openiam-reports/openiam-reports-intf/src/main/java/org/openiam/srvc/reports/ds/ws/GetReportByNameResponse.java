package org.openiam.srvc.reports.ds.ws;

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
import org.openiam.srvc.reports.ds.dto.TestReportUserDto;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "getInfoByReportNameResult"
})
@XmlRootElement(name = "GetReportByNameResponse")
public class GetReportByNameResponse extends Response {

    @XmlElement(name = "GetInfoByReportNameResult")
    protected GetReportByNameResponse.GetInfoByReportNameResult getInfoByReportNameResult;

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
