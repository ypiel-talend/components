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
package org.talend.components.mongodb.common;

import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.component.runtime.JarRuntimeInfo;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;
import org.talend.daikon.runtime.RuntimeUtil;
import org.talend.daikon.sandbox.SandboxedInstance;

/**
 * Common definition for mongodb components
 */
public abstract class MongoDBDefinition extends AbstractComponentDefinition {

    public static final boolean USE_CURRENT_JVM_PROPS = true;

    public static final String RUNTIME_MVN_URL = "mvn:org.talend.components/components-mongodb-runtime";

    public static final String RUNTIME_MVN_GROUP_ID = "org.talend.components";

    public static final String RUNTIME_MVN_ARTIFACT_ID = "components-mongodb-runtime";

    public static final String SOURCE_OR_SINK_CLASS = "org.talend.components.mongodb.runtime.MongoDBSourceOrSink";
    
    public static final String BULK_LOAD_RUNTIME_CLASS = "org.talend.components.mongodb.runtime.MongoDBBulkLoadRuntime";

    public static final String SOURCE_CLASS = "org.talend.components.mongodb.runtime.MongoDBSource";

    public static final String SINK_CLASS = "org.talend.components.mongodb.runtime.MongoDBSink";

    /** Provides {@link SandboxedInstance}s. */
    private static SandboxedInstanceProvider sandboxedInstanceProvider = SandboxedInstanceProvider.INSTANCE;

    public MongoDBDefinition(String componentName, ExecutionEngine... engineOthers) {
        super(componentName, ExecutionEngine.DI, engineOthers);
    }

    @Override
    public String[] getFamilies() {
        return new String[] { "Databases/MongoDB", "Big Data/MongoDB" }; //$NON-NLS-1$
    }

    @Override
    public Property[] getReturnProperties() {
        return new Property[] { RETURN_ERROR_MESSAGE_PROP };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ComponentProperties>[] getNestedCompatibleComponentPropertiesClass() {
        return new Class[] { MongoDBConnectionProperties.class };
    }

    public static RuntimeInfo getCommonRuntimeInfo(String clazzFullName) {
        return new JarRuntimeInfo(RUNTIME_MVN_URL,
                DependenciesReader.computeDependenciesFilePath(RUNTIME_MVN_GROUP_ID, RUNTIME_MVN_ARTIFACT_ID), clazzFullName);
    }

    /**
     * Set provider of {@link SandboxedInstance}s.
     *
     * <p>
     * The method is intended for debug/test purposes only and should not be used in production.
     *
     * @param provider provider to be set, can't be {@code null}
     */
    public static void setSandboxedInstanceProvider(SandboxedInstanceProvider provider) {
        sandboxedInstanceProvider = provider;
    }

    /**
     * Get current provider of {@link SandboxedInstance}s.
     *
     * @return provider
     */
    public static SandboxedInstanceProvider getSandboxedInstanceProvider() {
        return sandboxedInstanceProvider;
    }

    /**
     * Get {@link SandboxedInstance} for given runtime object class and <b>not</b> using current JVM properties.
     *
     * @see #getSandboxedInstance(String, boolean)
     * @see SandboxedInstanceProvider
     *
     * @param runtimeClassName full name of runtime object class
     * @return sandboxed instance
     */
    public static SandboxedInstance getSandboxedInstance(String runtimeClassName) {
        return getSandboxedInstance(runtimeClassName, false);
    }

    /**
     * Get {@link SandboxedInstance} for given runtime object class.
     *
     * @see SandboxedInstanceProvider
     *
     * @param runtimeClassName full name of runtime object class
     * @param useCurrentJvmProperties whether to use current JVM properties
     * @return sandboxed instance
     */
    public static SandboxedInstance getSandboxedInstance(String runtimeClassName, boolean useCurrentJvmProperties) {
        return sandboxedInstanceProvider.getSandboxedInstance(runtimeClassName, useCurrentJvmProperties);
    }

    /**
     * Provides {@link SandboxedInstance} objects.
     */
    public static class SandboxedInstanceProvider {

        /** Shared instance of provider. */
        public static final SandboxedInstanceProvider INSTANCE = new SandboxedInstanceProvider();

        /**
         * Get {@link SandboxedInstance} for given runtime object class.
         *
         * @param runtimeClassName full name of runtime object class
         * @param useCurrentJvmProperties whether to use current JVM properties
         * @return sandboxed instance
         */
        public SandboxedInstance getSandboxedInstance(final String runtimeClassName, final boolean useCurrentJvmProperties) {
            ClassLoader classLoader = MongoDBDefinition.class.getClassLoader();
            RuntimeInfo runtimeInfo = MongoDBDefinition.getCommonRuntimeInfo(runtimeClassName);
            if (useCurrentJvmProperties) {
                return RuntimeUtil.createRuntimeClassWithCurrentJVMProperties(runtimeInfo, classLoader);
            } else {
                return RuntimeUtil.createRuntimeClass(runtimeInfo, classLoader);
            }
        }
    }

}
