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
package org.talend.components.api.test.performance.example;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.talend.components.api.test.performance.ContiPerfRuleAdaptor;
import org.talend.components.api.testcomponent.TestComponentDefinition;
import org.talend.components.api.testcomponent.TestComponentProperties;

/**
 * Example of performance test using ContiPerf lib.
 */
public class ComponentPropertiesInitializationTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Rule
    public ContiPerfRuleAdaptor perfAdaptor = new ContiPerfRuleAdaptor();

    /**
     * Max and average time is set not to fail the build.
     */
    @Test
    @PerfTest(invocations = 5, threads = 1)
    @Required(max = 300, average = 150)
    public void test() {
        TestComponentDefinition cd = new TestComponentDefinition();

        TestComponentProperties prop = (TestComponentProperties) cd.createRuntimeProperties();
        prop.init();
    }

    /**
     * Test which shows that the time tracking is working. Method test() doesn't show that the performance benchmark
     * works fine, as the time is always very small. To show that we need some test, which will take more time than the
     * test() method.
     */
    @Test
    @PerfTest(invocations = 5, threads = 1)
    @Required(max = 300, average = 150)
    public void testShowResults() throws InterruptedException {
        Thread.sleep(150);
    }

}
