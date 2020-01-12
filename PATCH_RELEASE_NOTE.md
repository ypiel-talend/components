---
version: 6.4.1
module: https://talend.poolparty.biz/coretaxonomy/49
product:
- https://talend.poolparty.biz/coretaxonomy/49
- https://talend.poolparty.biz/coretaxonomy/183
---

# TPS-3638 <!-- mandatory -->

| Info             | Value |
| ---------------- | ---------------- |
| Patch Name       | Patch\_20200108\_TPS-3638\_v1-6.4.1 |
| Release Date     | 2020-01-08 |
| Target Version   | 6.4.1 |
| Product affected | Talend Data Preparation |

## Introduction <!-- mandatory -->

This is a self-contained patch.

**NOTE**: For information on how to obtain this patch, reach out to your Support contact at Talend.

## Fixed issues <!-- mandatory -->

This patch contains the following fixes:

- TPS-3638 [6.4.1] Unable to use Hive in JDBC Component (TDI-40793) (https://jira.talendforge.org/browse/TDI-40793)

## Prerequisites <!-- mandatory -->

Consider the following requirements for your system:

- Talend Data Preparation 6.4.1 must be installed.


## Installation <!-- mandatory -->

1. Stop the Data Preparation server:

    ```
    # Unix 
    ./dataprep/stop.sh

    # Windows
    dataprep\stop.bat
    ```

2. Stop the TComp server:

    ```
    # Unix 
    ./dataprep/services/tcomp/stop.sh

    # Windows
    .\dataprep\services\tcomp\stop.bat
    ```
	
5. Backup the following files and folders:
    You can backup the the following folder : `dataprep/services/tcomp/.m2/org/talend/components/components-jdbc-runtime/0.19.7`

### Installing the patch <!-- if applicable -->
From your Talend DataPreparation installation directory (such as `Talend-6.4.1/dataprep/`):
1. Unzip the following archive in the `services/tcomp/` folder

2. Restart services tcomp service

    ```
    # Unix 
    ./dataprep/start.sh

    # Windows
    dataprep\start.bat
    ```
3. Stop the Data Preparation server:

    ```
    # Unix 
    ./dataprep/start.sh

    # Windows
    dataprep\start.bat
    ```

## Uninstallation <!-- if applicable -->

1. Stop the Data Preparation server:

    ```
    # Unix 
    ./dataprep/stop.sh

    # Windows
    dataprep\stop.bat
    ```

2. Stop the TComp server:

    ```
    # Unix 
    ./dataprep/services/tcomp/stop.sh

    # Windows
    .\dataprep\services\tcomp\stop.bat
    ```

3. Restore the backuped folder `dataprep/services/tcomp/.m2/org/talend/components/components-jdbc-runtime/0.19.7`

## Affected files for this patch <!-- if applicable -->

The following files are installed by this patch:

- dataprep/services/tcomp/.m2/org/talend/components/components-jdbc-runtime/0.19.7/components-jdbc-runtime-0.19.7.jar
- dataprep/services/tcomp/.m2/org/talend/components/components-jdbc-runtime/0.19.7/components-jdbc-runtime-0.19.7-bundle.jar
- dataprep/services/tcomp/.m2/org/talend/components/components-jdbc-runtime/0.19.7/components-jdbc-runtime-0.19.7-sources.jar
