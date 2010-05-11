@echo off

rem call mvn install:install-file -DgroupId=play.gae.appengine.api -DartifactId=play-gae-appengine-api -Dversion=1.2.6 -Dfile=appengine-api-1.0-sdk-1.2.6.jar -Dpackaging=jar -DgeneratePom=true
rem call mvn install:install-file -DgroupId=play.gae.asm -DartifactId=play-gae-asm -Dversion=3.1 -Dfile=asm-3.1.jar -Dpackaging=jar -DgeneratePom=true
rem call mvn install:install-file -DgroupId=play.gae.datanucleus.appengine -DartifactId=play-gae-datanucleus-appengine -Dversion=1.0.3 -Dfile=datanucleus-appengine-1.0.3.jar -Dpackaging=jar -DgeneratePom=true
rem call mvn install:install-file -DgroupId=play.gae.datanucleus.core -DartifactId=play-gae-datanucleus-core -Dversion=1.1.5 -Dfile=datanucleus-core-1.1.5.jar -Dpackaging=jar -DgeneratePom=true
rem call mvn install:install-file -DgroupId=play.gae.datanucleus.enhancer -DartifactId=play-gae-datanucleus-enhancer -Dversion=1.1.4 -Dfile=datanucleus-enhancer-1.1.4.jar -Dpackaging=jar -DgeneratePom=true
rem call mvn install:install-file -DgroupId=play.gae.datanucleus.jpa -DartifactId=play-gae-datanucleus-jpa -Dversion=1.1.5 -Dfile=datanucleus-jpa-1.1.5.jar -Dpackaging=jar -DgeneratePom=true
rem call mvn install:install-file -DgroupId=play.gae.jdo2 -DartifactId=play-gae-jdo2 -Dversion=2.3 -Dfile=jdo2-api-2.3-eb.jar -Dpackaging=jar -DgeneratePom=true
rem call mvn install:install-file -DgroupId=play.gae.play.gae -DartifactId=play-gae-play-gae -Dversion=1.0.1 -Dfile=play-gae.jar -Dpackaging=jar -DgeneratePom=true
rem call mvn install:install-file -DgroupId=play.gae.provided.appengine -DartifactId=play-gae-provided-appengine -Dversion=1.0.1 -Dfile=provided-appengine-api.jar -Dpackaging=jar -DgeneratePom=true
rem call mvn install:install-file -DgroupId=play.gae.provided.appengine.api -DartifactId=play-gae-provided-appengine-api -Dversion=1.0.1 -Dfile=provided-appengine-api-stubs.jar -Dpackaging=jar -DgeneratePom=true
rem call mvn install:install-file -DgroupId=play.gae.provided.appengine.local -DartifactId=play-gae-provided-appengine-local -Dversion=1.0.1 -Dfile=provided-appengine-local-runtime.jar -Dpackaging=jar -DgeneratePom=true
rem call mvn install:install-file -DgroupId=play.siena.play.siena -DartifactId=play-siena-play-siena -Dversion=1.0.1 -Dfile=play-siena.jar -Dpackaging=jar -DgeneratePom=true
rem call mvn install:install-file -DgroupId=play.siena.siena -DartifactId=play-siena-siena -Dversion=r142 -Dfile=siena-r142.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.activation -DartifactId=play-activation -Dversion=r142 -Dfile=activation.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.antlr -DartifactId=play-antlr -Dversion=2.7.6 -Dfile=antlr-2.7.6.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.asyncweb.common -DartifactId=play-asyncweb-common -Dversion=0.9.0 -Dfile=asyncweb-common-0.9.0-SNAPSHOT.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.backport.util.concurrent -DartifactId=play-backport-util-concurrent -Dversion=3.0 -Dfile=backport-util-concurrent-3.0.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.bcprov -DartifactId=play-bcprov -Dversion=142 -Dfile=bcprov-jdk15-142.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.c3p0 -DartifactId=play-c3p0 -Dversion=0.9.1 -Dfile=c3p0-0.9.1.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.cglib -DartifactId=play-cglib -Dversion=2.2 -Dfile=cglib-nodep-2.2.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.commons.beanutils -DartifactId=play-commons-beanutils -Dversion=1.0.1 -Dfile=commons-beanutils.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.commons.codec -DartifactId=play-commons-codec -Dversion=1.0.1 -Dfile=commons-codec.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.commons.collections -DartifactId=play-commons-collections -Dversion=3.1 -Dfile=commons-collections-3.1.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.commons.fileupload -DartifactId=play-commons-fileupload -Dversion=1.2 -Dfile=commons-fileupload-1.2.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.commons.httpclient -DartifactId=play-commons-httpclient -Dversion=1.0.1 -Dfile=commons-httpclient.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.commons.io -DartifactId=play-commons-io -Dversion=1.0.1 -Dfile=commons-io.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.commons.lang -DartifactId=play-commons-lang -Dversion=1.0.1 -Dfile=commons-lang.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.commons.logging -DartifactId=play-commons-logging -Dversion=1.1.1 -Dfile=commons-logging-1.1.1.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.compiler.jdt -DartifactId=play-compiler-jdt -Dversion=1.0.1 -Dfile=compiler-jdt.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.dom4j -DartifactId=play-dom4j -Dversion=1.6.1 -Dfile=dom4j-1.6.1.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.ehcache -DartifactId=play-ehcache -Dversion=1.5.0 -Dfile=ehcache-1.5.0.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.ejb3.persistence -DartifactId=play-ejb3-persistence -Dversion=1.0.1 -Dfile=ejb3-persistence.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.ezmorph -DartifactId=play-ezmorph -Dversion=1.0.3 -Dfile=ezmorph-1.0.3.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.groovy -DartifactId=play-groovy -Dversion=1.6.5 -Dfile=groovy-all-1.6.5.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.gson -DartifactId=play-gson -Dversion=1.3 -Dfile=gson-1.3.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.hibernate3.ast -DartifactId=play-hibernate3-ast -Dversion=3 -Dfile=hibernate3-ast.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.hibernate3 -DartifactId=play-hibernate3 -Dversion=3 -Dfile=hibernate3.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.hibernate.annotations -DartifactId=play-hibernate-annotations -Dversion=3 -Dfile=hibernate-annotations.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.hibernate.commons -DartifactId=play-hibernate-commons -Dversion=3 -Dfile=hibernate-commons-annotations.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.hibernate.entitymanager -DartifactId=play-hibernate-entitymanager -Dversion=3 -Dfile=hibernate-entitymanager.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.hsqldb -DartifactId=play-hsqldb -Dversion=3 -Dfile=hsqldb.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.jamon -DartifactId=play-jamon -Dversion=2.7 -Dfile=jamon-2.7.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.javassist -DartifactId=play-javassist -Dversion=2.7 -Dfile=javassist.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.jaxen -DartifactId=play-jaxen -Dversion=1.1 -Dfile=jaxen-1.1.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.jregex -DartifactId=play-jregex -Dversion=1.2 -Dfile=jregex1.2_01.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.jsr107cache -DartifactId=play-jsr107cache -Dversion=1.0 -Dfile=jsr107cache-1.0.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.jta -DartifactId=play-jta -Dversion=1.0 -Dfile=jta.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.junit -DartifactId=play-junit -Dversion=4.4 -Dfile=junit-4.4.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.log4j -DartifactId=play-log4j -Dversion=1.2.15 -Dfile=log4j-1.2.15.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.lucene.analyzers -DartifactId=play-lucene-analyzers -Dversion=2.3.1 -Dfile=lucene-analyzers-2.3.1.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.lucene.core -DartifactId=play-lucene-core -Dversion=2.3.1 -Dfile=lucene-core-2.3.1.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.mail -DartifactId=play-mail -Dversion=2.3.1 -Dfile=mail.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.memcached -DartifactId=play-memcached -Dversion=2.4.2 -Dfile=memcached-2.4.2.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.mina.core -DartifactId=play-mina-core -Dversion=2.0.0 -Dfile=mina-core-2.0.0-M2-SNAPSHOT.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.mysql -DartifactId=play-mysql -Dversion=5.1.8 -Dfile=mysql-connector-java-5.1.8-bin.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.oval -DartifactId=play-oval -Dversion=1.31 -Dfile=oval-1.31.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.play.imaging -DartifactId=play-play-imaging -Dversion=1.0.1 -Dfile=play-imaging.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.play.SimpleCaptcha -DartifactId=play-play-SimpleCaptcha -Dversion=1.0.1 -Dfile=play-SimpleCaptcha.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.provided.geronimo -DartifactId=play-provided-geronimo -Dversion=1.2 -Dfile=provided-geronimo-servlet_2.5_spec-1.2.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.slf4j.api -DartifactId=play-slf4j-api -Dversion=1.5.0 -Dfile=slf4j-api-1.5.0.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.slf4j.log4j12 -DartifactId=play-slf4j-log4j12 -Dversion=1.5.0 -Dfile=slf4j-log4j12-1.5.0.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.SnakeYAML -DartifactId=play-SnakeYAML -Dversion=1.2 -Dfile=SnakeYAML-1.2.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.wikitext.core -DartifactId=play-wikitext.core -Dversion=1.1.1 -Dfile=wikitext.core_1.1.1.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.WikiText -DartifactId=play-WikiText -Dversion=1.1.1 -Dfile=WikiText.jar -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -DgroupId=play.ZDB -DartifactId=play-ZDB -Dversion=1.1.1 -Dfile=ZDB.jar -Dpackaging=jar -DgeneratePom=true
