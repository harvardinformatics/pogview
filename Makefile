CLASSROOT= /Users/mclamp/git/harvardinformatics/pogview/classfiles2
JARFILES = /Users/mclamp/git/harvardinformatics/pogview/jars
JAVA = /usr/

CLASSPATH=.:$(CLASSROOT):$(JARFILES)/patbinfree153.jar:$(JARFILES)/looks-1.3.1.jar:$(JARFILES)/forms-1.1.0.jar:$(JARFILES)/epsgraphics.jar:$(JARFILES)/colt.jar:$(JARFILES)/concurrent.jar:$(JARFILES)/swing-worker-1.2.jar:$(JARFILES)/picard.jar

install: all

clean:
	find . -name '*.class' -exec rm {} \;

#CC      = $(JAVA)/bin/javac -target 1.6 -sourcepath ./:./src2/ -classpath $(CLASSPATH) -d $(CLASSROOT)
CC      = $(JAVA)/bin/javac -sourcepath ./:./src2/ -classpath $(CLASSPATH) -d $(CLASSROOT)

#all:	datamodel io gui guihub guievent guimenus guischemes blast analysis applet renderer expression motif tree util math feature jar
all:	datamodel io gui guihub guievent guimenus guischemes analysis renderer expression motif tree util math feature jar

jar:
	#cd classfiles2 ; ./makejar ; cd .. ; echo poggywog | jarsigner jars/pogvue.jar pogview
	cd classfiles2 ; ./makejar ; cd .. ; echo 98isag76 |jarsigner -keystore KeyStore.jks jars/pogvue.jar mydomain


io:	$(IOCLASS)
	$(CC) $(IO)

gui:	$(GUICLASS)
	$(CC) $(GUI)

guihub:	$(GUIHUBCLASS)
	$(CC) $(GUIHUB)

guievent: $(GUIEVENTCLASS)
	$(CC) $(GUIEVENT)

analysis: $(ANALYSISCLASS)
	$(CC) $(ANALYSIS)

blast: $(BLASTCLASS)
	$(CC) $(BLAST)

applet:	$(APPLETCLASS)
	$(CC) $(APPLET)

tree: $(TREECLASS)
	$(CC) $(TREE)

expression: $(EXPRESSIONCLASS)
	$(CC) $(EXPRESSION)

motif: $(MOTIFCLASS)
	$(CC) $(MOTIF)

comparer: $(COMPARERCLASS)
	$(CC) $(COMPARER)

renderer: $(RENDERERCLASS)
	$(CC) $(RENDERER)

datamodel: $(DATAMODELCLASS)
	$(CC) $(DATAMODEL)

guimenus: $(GUIMENUSCLASS)
	$(CC) $(GUIMENUS)

guischemes: $(GUISCHEMESCLASS)
	$(CC) $(GUISCHEMES)

math: $(MATHCLASS)
	$(CC) $(MATH)

util: $(UTILCLASS)
	$(CC) $(UTIL)

feature: $(FEATURECLASS)
	$(CC) $(FEATURE)

IO = src2/pogvue/io/*.java

GUI = src2/pogvue/gui/*.java

GUIHUB = src2/pogvue/gui/hub/*.java

GUIEVENT = src2/pogvue/gui/event/*.java

GUIMENUS = src2/pogvue/gui/menus/*.java

GUISCHEMES = src2/pogvue/gui/schemes/*.java

MATH = src2/pogvue/math/*.java

UTIL = src2/pogvue/util/*.java

DATAMODEL = src2/pogvue/datamodel/*.java

MOTIF = src2/pogvue/datamodel/motif/*.java

EXPRESSION = src2/pogvue/datamodel/expression/*.java

TREE = src2/pogvue/datamodel/tree/*.java

RENDERER = src2/pogvue/gui/renderer/*.java

APPLET = src2/pogvue/applet/*.java

ANALYSIS =  src2/pogvue/analysis/*.java

BLAST =  src2/pogvue/analysis/blast/*.java

FEATURE =  src2/pogvue/feature/*.java

