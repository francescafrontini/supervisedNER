package fr.lip6.supervisedNER;

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
        		Training.trainingNER(args[1], Util.tei2OpenNLP(args[2], args[3]));
        	} else if (args[0].equals("tag")) {
        		Tagging.taggingNER(args[1], args[2], Util.tei2OpenNLP(args[3], "none"), args[3], args[4]);
        	}
        } else {
        	System.out.println("two forms of execution are possible: \n"
        	+ "1) train dictionary.xml TEIfile-NEannot.xml|folder place|person|organization|all (it outputs a .mod file) \n"
        	+ "2) tag dictionary.xml .mod newTEIfile-NoNEannot.xml place|person|organization (it outputs a new tei file with NE annotations)");
        }
    }
        
}
