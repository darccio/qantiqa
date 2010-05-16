#!/bin/bash

#
# Qantiqa : Decentralized microblogging platform
# Copyright (C) 2010 Dario (i@dario.im) 
#
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
#

# Auxiliary script to generate install scripts for Linux/Windows,
# Maven and Play 1.0.1.
# Generated scripts aim to install all Play! Framework jars in our local maven
# repository.
# It also outputs XML code to add to subprojects POMs.

# * $1: Jar ID
# * $2: Jar version
# * $3: Jar filename
mavenizing() {
    jar_id=$1
    version=$2
    jar=$3

    echo mvn install:install-file -DgroupId=play.${jar_id//-/.} -DartifactId=play-$jar_id -Dversion=$version -Dfile=$jar -Dpackaging=jar -DgeneratePom=true 
    echo '<dependency><groupId>play.'${jar_id//-/.}'</groupId><artifactId>'play-$jar_id'</artifactId><version>'$version'</version></dependency>' 1>&2
}

enter() {
    path="$1"
    echo cd \"$path\"
    cd "$path"
}

if [ -z "$1" ]; then
    echo "Usage: $0 <play_home_path>"
    exit 0
fi

play_home="$1"
play_framework_dir="$play_home/framework"
play_lib_dir="$play_framework_dir/lib"

enter "$play_framework_dir"
mavenizing play 1.0.1 play.jar

# We iterate over gae and siena modules directories because they are used in
# Qantiqa.
play_modules_dir="$play_home/modules"
for module in gae siena; do
    enter "$play_modules_dir/$module/lib"

    for jar in *.jar; do
        jar_name=${jar%.jar}
        jar_data=(${jar_name//-/ })

        if [ ${#jar_data[@]} -gt 1 ]; then
            version=${jar_data[@]: -1}
        fi

        jar_id=${jar_data[0]}
        case "$jar_id" in
            "play" | "datanucleus" | "appengine")
                jar_id=$jar_id"-"${jar_data[1]}
                if [ ${jar_data[1]} = "siena" -o ${jar_data[1]} = "gae" ]; then
                    unset version
                fi
                ;;
            "jdo2")
                version=${jar_data[2]}
                ;;
            "provided")
                unset version
                for word in `seq 1 $((${#jar_data[@]} - 2))`; do
                    jar_id=$jar_id"-"${jar_data[word]}
                done
                ;;
        esac

        jar_id=$module-$jar_id

        if [ -z "$version" ]; then
            version=1.0.1
        fi

        mavenizing $jar_id $version $jar
    done
done

enter "$play_lib_dir"
for jar in *.jar; do
    jar_name=${jar%.jar}
    jar_data=(${jar_name//-/ })

    if [ ${#jar_data[@]} -gt 1 ]; then
        version=${jar_data[@]: -1}
    fi

    jar_id=${jar_data[0]}
    case $jar_id in
        "asyncweb")
            jar_id=$jar_id"-"${jar_data[1]}
            version=${jar_data[2]}
            ;;
        "backport")
            jar_id=$jar_id"-"${jar_data[1]}"-"${jar_data[2]}
            ;;
        "commons")
            jar_id=$jar_id"-"${jar_data[1]}
            case ${jar_data[1]} in
                "beanutils" | "codec" | "httpclient" | "io" | "lang")
                    unset version
                    ;;
            esac
            ;;
        "compiler")
            jar_id=$jar_id"-"${jar_data[1]}
            unset version
            ;;
        "ejb3")
            jar_id=$jar_id"-"${jar_data[1]}
            unset version
            ;;
        "hibernate3" | "hibernate")
            version="3"
            if [ ${#jar_data[@]} -gt 1 ]; then
                jar_id=$jar_id"-"${jar_data[1]}
            fi
            ;;
        "jregex1.2_01")
            jar_id="jregex"
            version="1.2"
            ;;
        "lucene")
            jar_id=$jar_id"-"${jar_data[1]}
            ;;
        "mina")
            jar_id=$jar_id"-"${jar_data[1]}
            version=${jar_data[2]}
            ;;
        "mysql")
            version=${jar_data[@]: -2:1}
            ;;
        "play")
            unset version
            jar_id=$jar_id"-"${jar_data[1]}
            ;;
        "provided")
            jar_id=$jar_id"-"${jar_data[1]}
            ;;
        "slf4j")
            jar_id=$jar_id"-"${jar_data[1]}
            ;;
        "wikitext.core_1.1.1")
            jar_id="wikitext.core"
            version="1.1.1"
            ;;
    esac

    if [ -z "$version" ]; then
        version=1.0.1
    fi

    mavenizing $jar_id $version $jar
done

