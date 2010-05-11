#!/bin/bash

# For Play 1.0.1

mavenizing() {
    jar_id=$1
    version=$2

    echo mvn install:install-file -DgroupId=play.${jar_id//-/.} -DartifactId=play-$jar_id -Dversion=$version -Dfile=$jar -Dpackaging=jar -DgeneratePom=true 
    echo '<dependency><groupId>play.'${jar_id//-/.}'</groupId><artifactId>'play-$jar_id'</artifactId><version>'$version'</version></dependency>' 1>&2
}

if [ -z "$1" ]; then
    echo "Usage: $0 <play_home_path>"
    exit 0
fi

play_home="$1"
play_framework_dir="$play_home/framework"
play_lib_dir="$play_framework_dir/lib"

play_modules_dir="$play_home/modules"
for module in gae siena; do
    cd "$play_modules_dir/$module/lib"

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

        mavenizing $jar_id $version
    done
done

cd "$play_lib_dir"
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

    mavenizing $jar_id $version
done

