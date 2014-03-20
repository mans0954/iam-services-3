#!/bin/bash

echo 'Will change the version in pom.xml files...'
# get current branch name
branch=$(git rev-parse --abbrev-ref HEAD)
echo 'Current branch: ' 
echo $branch

prefix_branch=$(echo $branch | cut -d \- -f 1)
echo 'Current prefix_branch: ' 
echo $prefix_branch

# get current version of the top level pom
current_version=$(mvn help:evaluate -Dexpression=project.version | grep -v '\[.*')
echo 'Current version:  '
echo $current_version

# build new version
version=$branch


# extract version suffix
suffix=$(echo $version | cut -d \- -f 2)

# check  branch
if [ $branch == 'master' ]; then
	echo "Update version in master branch is not allowed. Please use corresponding RELEASE branch"  
	exit 999;
fi

if [ $branch == 'development' ]; then
	if [ -z $1 ]; then
		echo "Please provide new project version. Usage: ./update-version <new version>"        
		exit 999;
	fi
        
        version=$1
fi
if [ $prefix_branch == 'RELEASE' ]; then
	version=$suffix.$prefix_branch

fi

if [ -z $version ]; then
	if [ -z $1 ]; then
		echo "Please provide new project version. Usage: ./update-version <new version>"        
		exit 999;
	fi
   	version=$1
fi

echo 'New version:  '
echo $version



# run maven versions plugin to set new version
mvn -X versions:set -DgenerateBackupPoms=false -DnewVersion=$version -DupdateDependencies=true

#pattern1=$(printf "<openiam.esb.version>%s</openiam.esb.version>" "$current_version") 
#pattern2=$(printf "<openiam.esb.version>%s</openiam.esb.version>" "$version")

#find . -name pom.xml -exec sed -e "s/$pattern1/$pattern2/" {} \;

pattern1="<openiam.esb.version>$current_version</openiam.esb.version>"
pattern2="<openiam.esb.version>$version</openiam.esb.version>"

find . -name pom.xml -exec sed -i "s|$pattern1|$pattern2|g"  {} \;

echo 'Changed version in pom.xml files'
