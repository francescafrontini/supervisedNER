Main class to launch supervised NER is App.java or launch the runnable jar file supervisedNED.jar. If you choose to used runnable jar, you just need to have Java installed in your local machine and launch the runnable jar file via the command line using the command java -jar supervisedNED.jar

In both cases, arguments are explained below:

1) train dictionary.xml TEIfile-NEannot.xml|folderWithTEIs place|person|organization|all
(it outputs a .mod file).

For instance:

train dictionary/dictionary-sample.xml files/bergson_evolutionV2-gold.xml all

train dictionary/dictionary-latin-place-ammianus.xml files/historiale_l.2_c.62-85_place-gold.xml place

train dictionary/dictionary-latin-place-ammianus.xml files/testFolder place

2) tag dictionary.xml .mod newTEIfile-NoNEannot.xml place|person|organization
(it outputs a new tei file with NE annotations).

For instance:

tag dictionary/dictionary-sample.xml files/bergson_evolutionV2-gold.mod files/thibaudet_reflexions.xml person

tag dictionary/dictionary-latin-place-ammianus.xml files/lat-ner-2.mod files\historiale_l.2_c.62-85_noAnnot.xml place

tag dictionary/dictionary-latin-place-ammianus.xml files\historiale_l.2_c.62-85_place-gold.mod files\historiale_l.2_c.62-85_noAnnot.xml place (only for testing)