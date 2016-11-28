/*
 * Copyright 2010-2014, CloudBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openiam.idm.srvc.audit.syslogs;

import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public enum SysLogFacility implements Comparable<SysLogFacility> {

    KERN(0, "KERN"),
    USER(1 << 3, "USER"),
    MAIL(2 << 3, "MAIL"),
    DAEMON(3 << 3, "DAEMON"),
    AUTH(4 << 3, "AUTH"),
    SYSLOG(5 << 3, "SYSLOG"),
    LPR(6 << 3, "LPR"),
    NEWS(7 << 3, "NEWS"),
    UUCP(8 << 3, "UUCP"),
    CRON(9 << 3, "CRON"),
    AUTHPRIV(10 << 3, "AUTHPRIV"),
    FTP(11 << 3, "FTP"),
    NTP(12 << 3, "NTP"),
    AUDIT(13 << 3, "AUDIT"),
    ALERT(14 << 3, "ALERT"),
    CLOCK(15 << 3, "CLOCK"),
    LOCAL0(16 << 3, "LOCAL0"),
    LOCAL1(17 << 3, "LOCAL1"),
    LOCAL2(18 << 3, "LOCAL2"),
    LOCAL3(19 << 3, "LOCAL3"),
    LOCAL4(20 << 3, "LOCAL4"),
    LOCAL5(21 << 3, "LOCAL5"),
    LOCAL6(22 << 3, "LOCAL6"),
    LOCAL7(23 << 3, "LOCAL7");

    private final static Map<String, SysLogFacility> facilityFromLabel = new HashMap<String, SysLogFacility>();
    private final static Map<Integer, SysLogFacility> facilityFromNumericalCode = new HashMap<Integer, SysLogFacility>();

    static {
        for (SysLogFacility facility : SysLogFacility.values()) {
            facilityFromLabel.put(facility.label, facility);
            facilityFromNumericalCode.put(facility.numericalCode, facility);
        }
    }

    private final int numericalCode;

    @NotNull
    private final String label;

    private SysLogFacility(int numericalCode, @NotNull String label) {
        this.numericalCode = numericalCode;
        this.label = label;
    }

    @NotNull
    public static SysLogFacility fromNumericalCode(int numericalCode) throws IllegalArgumentException {
        SysLogFacility facility = facilityFromNumericalCode.get(numericalCode);
        if (facility == null) {
            return null;
        }
        return facility;
    }


    public static SysLogFacility fromLabel(String label) throws IllegalArgumentException {
        if (label == null || label.isEmpty())
            return null;

        SysLogFacility facility = facilityFromLabel.get(label);
        if (facility == null) {
            return null;
        }
        return facility;
    }

    public int numericalCode() {
        return numericalCode;
    }

    public String label() {
        return label;
    }

    public static Comparator<SysLogFacility> comparator() {
        return new Comparator<SysLogFacility>() {
            @Override
            public int compare(SysLogFacility f1, SysLogFacility f2) {
                return Integer.compare(f1.numericalCode, f2.numericalCode);
            }
        };
    }
}
