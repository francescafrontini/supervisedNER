package fr.lip6.supervisedNER;

/**
 * Main class to execute to launch supervised NER.
 * Parameter example: train files/thibaudet_openlp.ner or tag files/thibaudet_openlp.mod files/test.txt
 * 
 * @author Francesca Frontini & Carmen Brando -
 * 		Labex OBVIL - Université Paris-Sorbonne - UPMC-LIP6
 */
public class App 
{
    public static void main( String[] args )
    {
        if (args.length == 2 || args.length == 3) {
        	if (args[0].equals("train")) {
        		Training.trainingNER(args[1]);
        	} else if (args[0].equals("tag")) {
        		Tagging.taggingNER(args[1], args[2]);
        	}
        } else {
        	System.out.println("parameters are: [train file] [tag] file");
        }
    }
    
    public static void tei2OpenNLP() {
    	//TODO
    }
    
    public static void openNLP2TEI() {
    	//TODO
    }
}
