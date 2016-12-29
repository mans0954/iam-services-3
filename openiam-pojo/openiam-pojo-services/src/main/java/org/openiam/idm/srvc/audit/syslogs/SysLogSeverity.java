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

public enum SysLogSeverity {
    EMERGENCY(0, "EMERGENCY"),
    ALERT(1, "ALERT"),
    CRITICAL(2, "CRITICAL"),
    ERROR(3, "ERROR"),
    WARNING(4, "WARNING"),
    NOTICE(5, "NOTICE"),
    INFORMATIONAL(6, "INFORMATIONAL"),
    DEBUG(7, "DEBUG");

    // mapping
    private final static Map<String, SysLogSeverity> severityFromLabel = new HashMap<String, SysLogSeverity>();
    private final static Map<Integer, SysLogSeverity> severityFromNumericalCode = new HashMap<Integer, SysLogSeverity>();

    static {
        for (SysLogSeverity severity : SysLogSeverity.values()) {
            severityFromLabel.put(severity.label, severity);
            severityFromNumericalCode.put(severity.numericalCode, severity);
        }
    }

    private final int numericalCode;
    @NotNull
    private final String label;

    private SysLogSeverity(int numericalCode, @NotNull String label) {
        this.numericalCode = numericalCode;
        this.label = label;
    }

    public static SysLogSeverity fromNumericalCode(int numericalCode) throws IllegalArgumentException {
        SysLogSeverity severity = severityFromNumericalCode.get(numericalCode);
        if (severity == null) {
            return null;
        }
        return severity;
    }

    public static SysLogSeverity fromLabel(String label) throws IllegalArgumentException {
        if (label == null || label.isEmpty())
            return null;

        SysLogSeverity severity = severityFromLabel.get(label);
        if (severity == null) {
            return null;
        }
        return severity;
    }

    public int numericalCode() {
        return numericalCode;
    }

    @NotNull
    public String label() {
        return label;
    }

    public static Comparator<SysLogSeverity> comparator() {
        return new Comparator<SysLogSeverity>() {
            @Override
            public int compare(SysLogSeverity s1, SysLogSeverity s2) {
                return Integer.compare(s1.numericalCode, s2.numericalCode);
            }
        };
    }
}

