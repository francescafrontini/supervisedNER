package fr.lip6.supervisedNER;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Tagging methods with OpenNLP.
 * 
 * @author Francesca Frontini & Carmen Brando - Labex OBVIL - Université
 *         Paris-Sorbonne - UPMC-LIP6
 */
public class Util {
	
	/**
     * Reads TEI file with NE annotations, 
     * strips out XML tags except NE annotations and
     * creates new file in compliance with OpenNLP format and .opennlp extension.
     */
    public static String tei2OpenNLP(String teifile, String neType) {    	
    	try {
    		BufferedWriter out = new BufferedWriter(new FileWriter(teifile.replace(".xml", ".opennlp")));
			BufferedReader br = new BufferedReader(new FileReader(teifile));
			String line;
			while ( (line=br.readLine()) != null) {
				if (! neType.equals("none") ) { //not to do it for tagging phase
					if (neType.equals("person") || neType.equals("all")) {
		        		line = line.replaceAll("\\<persName.*?>", "\\<START:person> ");
		        		line = line.replaceAll("\\</persName>", " \\<END>");
		        	} 
		        	if (neType.equals("place") || neType.equals("all")) {
		        		line = line.replaceAll("\\<placeName.*?>", "\\<START:place> ");
			        	line = line.replaceAll("\\</placeName>", " \\<END>");	
		        	}
		        	if (neType.equals("organization") || neType.equals("all")) {
			        	line = line.replaceAll("\\<orgName.*?>", "\\<START:organization> ");
				        line = line.replaceAll("\\</orgName>", " \\<END>");	
			        }
				}
	        	line = line.replaceAll("\\</?(?!(?:START:person|START:place|START:organization|END)\b)[a-z]+(?:[^>\\\"']|\\\"[^\\\"]*\\\"|'[^']*')*>", "");	        	
	        	line = line.replaceAll("\\.", " .");
	        	line = line.replaceAll(",", " , ");
	        	line = line.replaceAll("’", "’ ");
	        	line = line.replaceAll("'", "' ");
	        	line = line.replaceAll("\\\"", " \\\"");
	        	line = line.replaceAll("\\(", " \\( ");
	        	line = line.replaceAll("\\)", " \\ )");
	        	//at the end make sure that multiple spaces do not occur within the document
	        	line = line.replaceAll(" +", " ");
	        	if (!line.equals("") && !line.equals(" ")) {
		        	out.append(line + System.lineSeparator());
	        	}	        	
	        }	        
	        br.close();
	        out.close();
		} catch (FileNotFoundException e) {		
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}        
    	return teifile.replace(".xml", ".opennlp");     	
    }
    
    public static void compareTEIGoldwithAnnot(String teifile1, String teifile2) {
    	//TODO
    }
}
