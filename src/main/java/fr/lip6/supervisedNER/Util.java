package fr.lip6.supervisedNER;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public static String tei2OpenNLP(String teifileOrFolder, String neType) {    	
    	try {
    		
    		File teifileOrFolderF = new File(teifileOrFolder);
    		List<File> files = new ArrayList<File>();    		
			if (teifileOrFolderF.isFile()) {
				files.add(teifileOrFolderF);
			} else if (teifileOrFolderF.isDirectory()) {
				File[] f = teifileOrFolderF.listFiles();
				for (int j = 0; j < f.length; j++) {
					files.add(f[j]);
				}
			}
			
			BufferedWriter out = new BufferedWriter(new FileWriter(teifileOrFolder+".opennlp"));
			for (int j = 0; j < files.size(); j++) {	    		
				BufferedReader br = new BufferedReader(new FileReader(files.get(j)));
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
			}
			out.close();
		} catch (FileNotFoundException e) {		
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}        
    	return teifileOrFolder+".opennlp";     	
    }
    
    public static void compareTEIGoldwithAnnot(String teifile1, String teifile2) {
    	//TODO
    }
}
