// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.components.mongodb.error;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import java.util.Arrays;
import java.util.Collection;

import org.talend.components.mongodb.MongoDBFamilyDefinition;
import org.talend.daikon.exception.error.ErrorCode;

public enum MongoDBErrorCodes implements ErrorCode {

    UNABLE_TO_RETRIEVE_COLLECTION("UNABLE_TO_RETRIEVE_COLLECTION", SC_INTERNAL_SERVER_ERROR),
    UNABLE_TO_RETRIEVE_COLLECTION_FIELDS("UNABLE_TO_RETRIEVE_COLLECTION_FIELDS", SC_INTERNAL_SERVER_ERROR);

    public static final String PRODUCT_TALEND_COMPONENTS = "TCOMP";

    private final String code;

    private final int httpStatus;

    private final Collection<String> contextEntries;

    private MongoDBErrorCodes(String code, int httpStatus, String... contextEntries) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.contextEntries = Arrays.asList(contextEntries);
    }

    @Override
    public String getProduct() {
        return PRODUCT_TALEND_COMPONENTS;
    }

    @Override
    public String getGroup() {
        return MongoDBFamilyDefinition.NAME;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public Collection<String> getExpectedContextEntries() {
        return contextEntries;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NetSuiteErrorCode{");
        sb.append("code='").append(code).append('\'');
        sb.append(", httpStatusCode=").append(code);
        sb.append(", expectedContextEntries=").append(contextEntries);
        sb.append(", product='").append(getProduct()).append('\'');
        sb.append(", group='").append(getGroup()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
