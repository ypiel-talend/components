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
package org.talend.components.google.avro;

import java.math.BigDecimal;

import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.converter.AbstractAvroConverter;

/**
 * Converts {@link BigDecimal} data to Avro compatible {@link Double} type
 */
public class BigDecimalDoubleConverter extends AbstractAvroConverter<BigDecimal, Double> {

    /**
     * Sets Double avro schema and {@link BigDecimal} type as datum class
     * 
     * @param clazz
     * @param schema
     */
    public BigDecimalDoubleConverter() {
        super(BigDecimal.class, AvroUtils._double());
    }

    @Override
    public BigDecimal convertToDatum(Double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Double convertToAvro(BigDecimal value) {
        return value.doubleValue();
    }

}
