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

package org.talend.components.mongodb.util;

import com.mongodb.BasicDBObject;

class DBObjectUtil {

    private BasicDBObject object = null;

    /**
     * Put value to embedded document
     * If have no embedded document, put the value to root document
     * 
     * @param parentNode Parent node path
     * @param currentName Current node name
     * @param value Value of current node
     */
    public void put(String parentNode, String currentName, Object value) {
        if (parentNode == null || "".equals(parentNode)) {
            object.put(currentName, value);
        } else {
            String objNames[] = parentNode.split("\\.");
            BasicDBObject lastNode = getParentNode(parentNode, objNames.length - 1);
            lastNode.put(currentName, value);
            BasicDBObject parenttNode = null;
            for (int i = objNames.length - 1; i >= 0; i--) {
                parenttNode = getParentNode(parentNode, i - 1);
                parenttNode.put(objNames[i], lastNode);
                lastNode = (BasicDBObject) parenttNode.clone();
            }
            object = lastNode;
        }
    }

    /**
     * Get node(embedded document) by path configuration
     *
     * @param parentNode Parent node path
     * @param index Index of embedded document
     * @return
     */
    public BasicDBObject getParentNode(String parentNode, int index) {
        BasicDBObject basicDBObject = object;
        if (parentNode == null || "".equals(parentNode)) {
            return object;
        } else {
            String objNames[] = parentNode.split("\\.");
            for (int i = 0; i <= index; i++) {
                basicDBObject = (BasicDBObject) basicDBObject.get(objNames[i]);
                if (basicDBObject == null) {
                    basicDBObject = new BasicDBObject();
                    return basicDBObject;
                }
                if (i == index) {
                    break;
                }
            }
            return basicDBObject;
        }
    }

    public void putKeyNode(String parentNode, String currentName, Object value) {
        if (parentNode == null || "".equals(parentNode) || ".".equals(parentNode)) {
            put(parentNode, currentName, value);
        } else {
            put("", parentNode + "." + currentName, value);
        }
    }

    public BasicDBObject getObject() {
        return this.object;
    }

    public void setObject(BasicDBObject object) {
        this.object = object;
    }

}