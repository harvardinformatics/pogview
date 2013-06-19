CLASSROOT= /Users/mclamp/cvs/pogdev/classfiles
JARFILES = /Users/mclamp/cvs/pogdev/jars
JAVA = /usr/

CLASSPATH=.:$(CLASSROOT):$(JARFILES)/patbinfree153.jar:$(JARFILES)/looks-1.3.1.jar:$(JARFILES)/forms-1.1.0.jar:$(JARFILES)/epsgraphics.jar:$(JARFILES)/colt.jar:$(JARFILES)/concurrent.jar:$(JARFILES)/swing-worker-1.2.jar

install: all

clean:
	find . -name '*.class' -exec rm {} \;

#CC      = $(JAVA)/bin/javac -target 1.6 -sourcepath ./:./src/ -classpath $(CLASSPATH) -d $(CLASSROOT)
CC      = $(JAVA)/bin/javac -sourcepath ./:./src/ -classpath $(CLASSPATH) -d $(CLASSROOT)

all:	datamodel io gui guihub guievent guimenus guischemes blast analysis applet renderer expression motif tree util math feature jar

jar:
	cd classfiles ; ./makejar ; cd .. ; echo poggywog | jarsigner jars/pogvue.jar pogvue


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

IO = src/pogvue/io/*.java

GUI = src/pogvue/gui/*.java

GUIHUB = src/pogvue/gui/hub/*.java

GUIEVENT = src/pogvue/gui/event/*.java

GUIMENUS = src/pogvue/gui/menus/*.java

GUISCHEMES = src/pogvue/gui/schemes/*.java

MATH = src/pogvue/math/*.java

UTIL = src/pogvue/util/*.java

DATAMODEL = src/pogvue/datamodel/*.java

MOTIF = src/pogvue/datamodel/motif/*.java

EXPRESSION = src/pogvue/datamodel/expression/*.java

TREE = src/pogvue/datamodel/tree/*.java

RENDERER = src/pogvue/gui/renderer/*.java

APPLET = src/pogvue/applet/*.java

ANALYSIS =  src/pogvue/analysis/*.java

BLAST =  src/pogvue/analysis/blast/*.java

FEATURE =  src/pogvue/feature/*.java

