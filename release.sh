#! /bin/bash

###
# Documentation/Help------------------------------------------------------------
###

help(){
  echo "Usage: ./release.sh [OPTION] [OPTION_PARAMETER]..."
  echo "Releases tcomp v0 components"
  echo
  echo "Requirements:"
  echo "* This script uses git, so it should be installed"
  echo "* Make release.sh file executable. Run \"sudo chmod +x release.sh\" to do it"
  echo "* Launch this script from root directory of components repository"
  echo "* Checkout commit from which relese should be done before launching this script"
  echo
  echo "Options:"
  echo "-h, --help    display this help and exit"
  echo "-b, --bump    bumps version of specified module. Requires 3 parameters:"
  echo "                1: module name (for commit message only)"
  echo "                2: previous version"
  echo "                3: new version"
  echo "              example: ./release.sh --bump daikon 0.31.11-SNAPSHOT 0.31.11"
  echo "-m            to be used only when script is launched from master branch;"
  echo "                additionally creates maintenance branch and bumps version"
  echo "                on master branch"

  exit 0
}

###
# Constants---------------------------------------------------------------------
###

# Daikon version maven property used to read Daikon version from parent pom
DAIKON_VERSION_PROPERTY="daikon\.version"

# Component version maven property used to read Component version from parent pom
COMPONENT_VERSION_PROPERTY="components\.version"

# Regular expression which should match standard maven version, which consists of 3 numbers (major.minor.patch)
# e.g. 0.12.34 should match this regexp
ARTIFACT_VERSION_REGEXP="[0-9]+\.[0-9]+\.[0-9]+(-SNAPSHOT)?"

# Jenkins build server URL
CI_SERVER="https://ci-common.datapwn.com/view/Components/"

# Component repository URL
# Fork Component repository and change original URL with forked to test this script
# REPOSITORY=https://github.com/gonchar-ivan/components/
REPOSITORY=https://github.com/Talend/components/

# Component reposiroty directory on user's local machine
REPO_DIR=$PWD

###
# Global variables--------------------------------------------------------------
###

# current version
pre_release_version=""

# version to be used for release
release_version=""

# next development version
post_release_version=""

# major part of maven version
major_version=""

# minor part of maven version
minor_version=""

# "boolean" variale which specifies if it is release from master
master_release="false"

# next SNAPSHOT version on master
new_master_version=""

# name of current branch (should be either maintenance or master)
current_branch=""

# temporary branch name
temp_branch=""

###
# Functions---------------------------------------------------------------------
###

##
# waits for user input. It is used when user interaction is required
# doesn't accept parameters
##
checkAnswer(){
  echo
  echo -n "Press Ctrl+C to quit. Press Enter to continue...    "
  read ANS
  if [ "$ANS" != "" ]; then
    echo "Exiting"
    exit 0
  fi
  echo
}

##
# validates that version entered by user is valid maven version (corresponds to ARTIFACT_VERSION_REGEXP regexp)
# if it doesn't then script exists with code 1
# accepts 1 parameter:
# $1 - version to be checked
##
validateVersion(){
  if [[ ! $1 =~ $ARTIFACT_VERSION_REGEXP ]]; then
    echo "Incorrect maven version: ${1} Version should correspond to following regexp ${ARTIFACT_VERSION_REGEXP}"
    echo "Exiting"
    exit 2
  fi
}

welcome(){
  echo
  echo "Welcome to TCOMP release script!"
  echo "You are going to release Components from $REPOSITORY repository"
  echo
}

##
# asks user to recheck that he checkout correct commit for release
# uses 1 global variable:
# $current_branch
##
checkCommit(){
  current_branch=`git rev-parse --abbrev-ref HEAD`
  echo "Before starting release make sure you checkout correct branch and commit"
  echo "Branch: $current_branch"
  echo "Commit: `git log -1 --pretty=%B`"
  echo
  echo "Make sure you executed following command to get latest commits"
  echo
  echo "git pull origin ${current_branch}"
  checkAnswer
}

##
# compute current (pre release) version, release version and next (post release) version
# uses 3 global variables:
# $pre_release_version
# $release_version
# $post_release_version
##
computeVersions(){
  pre_release_version=`grep "$COMPONENT_VERSION_PROPERTY" components-parent/pom.xml | grep -o -E "$ARTIFACT_VERSION_REGEXP"`
  release_version=${pre_release_version%"-SNAPSHOT"}
  # maven version consists of 3 numbers: major (API) version, minor (feature) and patch (bugfix)
  # if release_version = 0.28.5, then major version = 0
  major_version=`echo ${release_version} | cut -d "." -f "1"`
  # if release_version = 0.28.5, then major version = 28
  minor_version=`echo ${release_version} | cut -d "." -f "2"`
  # if release_version = 0.28.5, then patch version = 5
  patch_version=`echo ${release_version} | cut -d "." -f "3"`
  new_patch_version=`expr $patch_version + 1`
  post_release_version="${major_version}.${minor_version}.${new_patch_version}-SNAPSHOT"

  echo "Component pre release version is $pre_release_version"
  echo "Component release version will be $release_version"
  echo "Component post release version will be $post_release_version"
  if [ $master_release = "true" ]; then
    new_minor_version=`expr $minor_version + 1`
    new_master_version="${major_version}.${new_minor_version}.0-SNAPSHOT"
    echo "Component master version will be $new_master_version"
  fi
  echo
}

##
# creates and checkouts temporary branch (to recheck that script bumped only components version, but not other)
# accepts 1 parameter:
# $1 - version suffix for temp branch name
##
checkoutTempBranch(){
  temp_branch=temp/bump/${1}
  git checkout -b $temp_branch
  echo
}

##
# uses find and sed to replace original version ($1 parameter) with new version ($2 parameter)
# replaces version in pom.xml and archetype.properties files
# archetype.properties file stores version for Components archetype project (maybe we should disable this project?)
# accepts 2 parameters:
# $1 - previous version
# $2 - new version
##
changeVersion(){
  find $REPO_DIR -name 'archetype.properties' -exec sed -i "s/${1}/${2}/g" {} \;
  find $REPO_DIR -name 'pom.xml' -exec sed -i "s/${1}/${2}/g" {} \;
  echo
}

##
# uses git to commit version bump
# accepts 1 parameter:
# $1 - module to bump
# $2 - new version
##
commitVersion(){
  git add .
  git commit -m "chore: bump ${1} version to ${2}"
  echo
}

##
# pushes temporary branch (to recheck that script bumped only components version, but not other)
# uses $temp_branch global variable as parameter
##
pushTempBranch(){
  git push origin $temp_branch
  echo
}

##
# asks user to review bump commit before merging to release branch
# provides github link for convenience
# uses $current_branch and $temp_branch global variables as parameters
# also uses $REPOSITORY constant
##
reviewBump(){
  echo "Please review bump commit with the following link"
  echo "${REPOSITORY}compare/${current_branch}...${temp_branch}?expand=1"
  checkAnswer
}

##
# pushes bump commit and removes temporary branch
#
# uses 2 global variables:
# $current_branch
# $temp_branch
##
pushBump(){
  git checkout $current_branch
  echo
  git merge $temp_branch
  echo
  git push origin $current_branch
  echo
  # remove temp branch from github
  git branch -D $temp_branch
  git push origin --delete $temp_branch
  echo
}

##
# creates and pushes bump commit
# accepts 2 parameters:
# $1 - module to bump; used only in commit message
# $2 - previous version
# $3 - new version
##
bumpVersion(){
  # validate parameters
  if [ "$#" -ne 3 ]; then
    echo "Illegal number of parameters. Should be 3: module to bump; previous version; new version"
    echo "Exiting"
    exit 1
  fi
  validateVersion "$2"
  validateVersion "$3"

  checkoutTempBranch "$3"
  changeVersion "$2" "$3"
  commitVersion "$1" "$3"
  pushTempBranch
  echo "(*) Review bump commit $3"
  echo
  reviewBump
  echo "(*) Push bump commit $3"
  echo
  pushBump
}

##
# creates, pushes a release tag and suggests to create release notes
# uses 1 global variable:
# $release_version
##
pushTag(){
  # ensure release build was successful
  echo "Please wait until release build is finished successfully"
  echo "If the build failed, quit this script, fix a problem and restart build"
  checkAnswer

  # at this point HEAD should be on release commit
  # create and push tag
  git tag release/${release_version}
  git push origin release/${release_version}
  echo

  # create release notes on github
  echo "Please create release notes on github using following link"
  echo "${REPOSITORY}releases/new?tag=release%2F${release_version}"
  checkAnswer
}

##
# makes required action in case of release started from master:
# creates maintenance branch, bumpes version on master and checkouts maintenance branch
# uses global variables:
# $major_version
# $minor_version
# $pre_release_version
# $new_master_version
# $current_branch
##
createMaintenanceAndBumpMaster(){
  ### Create maintenance branch
  maintenance_branch="maintenance/${major_version}.${minor_version}"
  echo "(*) Create ${maintenance_branch} branch"
  git branch ${maintenance_branch}
  created_branch=`git branch --list | grep ${maintenance_branch}`
  # validate created branch
  if [ -z "$created_branch" ]; then
    echo "${maintenance_branch} branch wasn't created for some reason"
  else
    echo "created branch ${created_branch}"
  fi
  echo

  ### Bump version on master_release
  echo "(*) Bump Components version on master branch"
  echo
  bumpVersion "Components" "$pre_release_version" "$new_master_version"

  ### Checkout on maintenance branch
  echo "(*) Checkout ${maintenance_branch} branch"
  git checkout ${maintenance_branch}
  echo
  current_branch=${maintenance_branch}

  ### Push maintenance branch
  git push origin ${maintenance_branch}
}

##
# prints Daikon version and bumpes it if user types new version
##
checkDaikonVersion(){
  echo "It is assumed all other dependencies were already updated"
  daikon_version=`grep "$DAIKON_VERSION_PROPERTY" components-parent/pom.xml | grep -o -E "$ARTIFACT_VERSION_REGEXP"`
  echo "Daikon version to be used is $daikon_version"
  echo "Press Enter to continue..."
  echo -n "  or input Daikon version to be used in the form of x.y.z...    "
  read ANS
  if [ "$ANS" != "" ]; then
    daikon_prev_version=$daikon_version
    daikon_version=$ANS
    echo "Changing daikon version to $daikon_version"
    bumpVersion "Daikon" "$daikon_prev_version" "$daikon_version"
  fi
  echo
}

###
# Main script-------------------------------------------------------------------
###

### Parse options
option=$1
case $option in
  # if no option then do nothing and launch release
  "")
  ;;
  "-h"|"--help")
  help
  ;;
  "-b"|"--bump")
  bumpVersion $2 $3 $4
  exit 0
  ;;
  "-m")
  # sets flag that release starts from master and maintenance branch should be created
  master_release=true
  ;;
  *)
  echo "Unrecognized option: $option"
  echo "Launching --help"
  help
  ;;
esac

welcome

### Ask to recheck if checkout was done on correct commit
echo "(*) Checkout correct commit"
echo
checkCommit
computeVersions

if [ $master_release = "true" ]; then
  createMaintenanceAndBumpMaster
fi

### Check (and bump) Daikon version
echo "(*) Check Daikon version"
echo
checkDaikonVersion

### Bump Components version to release
echo "(*) Bump Components version to release version"
echo
bumpVersion "Components" "$pre_release_version" "$release_version"
# ensure CI build started
echo "Please ensure release build started (or start it manually) on ${CI_SERVER}"
checkAnswer

### Push release tag
echo "(*) Push release tag"
echo
pushTag

### Bump Component version to post release version
echo "(*) Bump Component version to post release version"
echo
bumpVersion "Components" "$release_version" "$post_release_version"
# ensure CI build started
echo "(Optionally) Please ensure CI build started (or start it manually) on ${CI_SERVER}"
checkAnswer

### Publish message in slack channels
echo "(*) Post message in slack"
echo
echo "Please post following message in di-releases (and optionally to #eng-releases) slack channels"
echo "\`tcompV0 ${release_version}\` released"
echo "SE: (paste link to CI build)"
echo "EE: (paste link to CI build)"
echo "Docker: (paste docker link here)"
checkAnswer

### Bump tcomp version in Studio
echo "(*) Bump version in Studio"
echo
echo "Please bump tcomp version in Studio"
echo "Following wiki page https://in.talend.com/15997888 describes how to do it"
checkAnswer

### Script finished
echo "Release finished"
