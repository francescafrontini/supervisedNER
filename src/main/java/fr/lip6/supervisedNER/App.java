package fr.lip6.supervisedNER;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Main class to execute to launch supervised NER.
 * 
 * @author Francesca Frontini & Carmen Brando -
 * 		Labex OBVIL - Université Paris-Sorbonne - UPMC-LIP6
 */
public class App 
{
    public static void main(String[] args)
    {
    	if (args.length == 4 || args.length == 5) {
        	if (args[0].equals("train")) {
        		Training.trainingNER(args[1], Training.tei2OpenNLP(args[2], args[4]));
        	} else if (args[0].equals("tag")) {
        		Tagging.taggingNER(args[1], args[2], args[3]);
        	}
        } else {
        	System.out.println("two forms of execution are possible: "
        	+ "1) train dictionary.xml TEIfile-NEannot.xml place|person|organization|all (it outputs a .mod file) "
        	+ "2) tag dictionary.xml .mod newTEIfile-NoNEannot.xml place|person|organization|all (it outputs a new tei file with NE annotations)");
        }
    }
    
    public static void openNLP2TEI() {
    	//TODO ça va pas marcher, plutot produire un fi
    }
    
}
