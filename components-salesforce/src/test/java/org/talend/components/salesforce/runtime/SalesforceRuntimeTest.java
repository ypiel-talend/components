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

package org.talend.components.salesforce.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import com.sforce.ws.bind.CalendarCodec;

/**
 *
 */
public class SalesforceRuntimeTest {

    @Test
    public void testConvertDateToCalendar() throws Throwable {
        long timestamp = System.currentTimeMillis();
        Calendar calendar1 = SalesforceRuntime.convertDateToCalendar(new Date(timestamp),false);
        assertNotNull(calendar1);
        assertEquals(TimeZone.getTimeZone("GMT"), calendar1.getTimeZone());

        assertNull(SalesforceRuntime.convertDateToCalendar(null,false));

        CalendarCodec calCodec = new CalendarCodec();
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.parse("2017-10-24T20:09:14.000Z");
        Date date = dateFormat.getCalendar().getTime();
        Calendar calIgnoreTZ = SalesforceRuntime.convertDateToCalendar(date, true);

        assertEquals("2017-10-24T20:09:14.000Z", calCodec.getValueAsString(calIgnoreTZ));

    }
}
