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

package org.talend.components.mongodb.runtime;

import org.apache.commons.lang3.StringUtils;

public class MongoDBRuntimeHelper {

    /**
     * Validate column name and make it match schema column syntax
     */
    public static String validateColumnName(String columnName) {
        if (columnName == null) {
            return null;
        }
        // TODO need test more column name like: "123a"
        // columnName = mapSpecialChar(columnName);
        final String underLine = "_"; //$NON-NLS-1$
        if (columnName.matches("^\\d.*")) { //$NON-NLS-1$
            columnName = underLine + columnName;
        }

        String testColumnName = columnName.replaceAll("[^a-zA-Z0-9_]", underLine); //$NON-NLS-1$

        if (StringUtils.countMatches(testColumnName, underLine) < (columnName.length() / 2)) {
            return testColumnName;
        }
        return columnName;
    }

}
