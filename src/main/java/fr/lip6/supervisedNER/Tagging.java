package fr.lip6.supervisedNER;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;
import opennlp.tools.util.StringList;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import opennlp.tools.util.featuregen.BigramNameFeatureGenerator;
import opennlp.tools.util.featuregen.CachedFeatureGenerator;
import opennlp.tools.util.featuregen.CharacterNgramFeatureGenerator;
import opennlp.tools.util.featuregen.DictionaryFeatureGenerator;
import opennlp.tools.util.featuregen.OutcomePriorFeatureGenerator;
import opennlp.tools.util.featuregen.PreviousMapFeatureGenerator;
import opennlp.tools.util.featuregen.SentenceFeatureGenerator;
import opennlp.tools.util.featuregen.TokenClassFeatureGenerator;
import opennlp.tools.util.featuregen.TokenFeatureGenerator;
import opennlp.tools.util.featuregen.WindowFeatureGenerator;

/**
 * Tagging methods with OpenNLP.
 * 
 * @author Francesca Frontini & Carmen Brando - Labex OBVIL - Université
 *         Paris-Sorbonne - UPMC-LIP6
 */
public class Tagging {

	/**
	 * Annotates the input XML-TEI file with named-entities from dictionary and model. 
	 * @param dico, the name of the input dictionary in OpenNLP format
	 * @param model, the OpenNLP trained model
	 * @param openNLPfile, the input TEI file in OpenNLP format
	 * @param teifile, the user TEI input file
	 * @param typeNE, the type of named-entity
	 */
	public static void taggingNER(String dico, String model, String openNLPfile, String teifile, String typeNE) {

		try {
			BufferedReader br = new BufferedReader(new FileReader(openNLPfile));
			String input;
			//TODO call tokenise.pl de TreeTagger pour être sure que la tokenisation est bien faite (?)
			List<String> words = new ArrayList<String>();
			while ((input = br.readLine()) != null) {
				String[] ws = input.split(" ");
				for (String w : ws) {
					words.add(w);
				}
			}
			String[] wL = {};
			wL = words.toArray(wL);
			Span nameSpans[] = statNamedEntityFinder(dico, wL, model);
			System.out.println("Result produced by the STATISTICAL model\n");
			System.out.println("offset\tform");
			Set<String> mentions = new HashSet<String>();
			for (int i = 0; i < nameSpans.length; i++) {
				//String offset = nameSpans[i].getStart() + " "
				//		+ nameSpans[i].getEnd();
				String mention = "";
				for (int j = nameSpans[i].getStart(); j < nameSpans[i].getEnd(); j++) {
					mention += wL[j];
				}
				//offset += "\t" + mention;
				mentions.add(mention);
				//System.out.println(offset);				
			}
			br.close();
			// if some dictionary is provided, fuse model and dictionary
			Set<String> fusedMentions = fuseModelAndDictionary(dico, mentions, typeNE);
			/*for (String m : fusedMentions) {
				System.out.println(m);
			}*/
			// inject annotations into TEI
			injectAnnotationsTEI(fusedMentions, teifile, typeNE);
			
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	/**
	 * Opens TEI file using DOM, go paragraph per paragraph <p> and load content (and tail), then replace
	 * @param fusedMentions
	 * @param teifile
	 * @param typeNE
	 */
	private static void injectAnnotationsTEI(Set<String> fusedMentions, String teifile, String typeNE) {

		try {
			String neTag = "", input = "";
			if (typeNE.equals("place")) {
				neTag = "placeName";
			} else if (typeNE.equals("person")) {
				neTag = "persName";
			} else if (typeNE.equals("organization")) {
				neTag = "orgName";
			}
			BufferedWriter brWriteTEI = new BufferedWriter(new FileWriter(teifile.replace(".xml", "-out.xml")));
			
			BufferedReader brReadTEI = new BufferedReader(new FileReader(teifile));
			while((input = brReadTEI.readLine()) != null) {
				for (String m : fusedMentions) {
					/*input = input.replaceAll(m, "<"+neTag+">"+m+"</"+neTag+">");//TODO */
						
				}
				brWriteTEI.write(input + System.lineSeparator());
			}
			brReadTEI.close();
			brWriteTEI.close();
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	/**
	 * Build a mentions-to-inject list from dict and model only including:
	 * - those that do not overlap
	 * - when complete overlapping, those that are the longest in length
	 * - when partial overlapping, take the longest consecutive string.
	 * @param dico
	 * @param mentions
	 * @return fused mentions
	 */
	private static Set<String> fuseModelAndDictionary(String dico, Set<String> mentions, String typeNE) {
		
		try {
			InputStream dictFileIn = new FileInputStream(
					dico);
			Set<String> fusedModel = new HashSet<String>();
			Dictionary dictIn = new Dictionary(dictFileIn);
			Iterator<StringList> it = dictIn.iterator();
			while (it.hasNext()) {
				StringList dicEntry = it.next();
				//System.out.println(dicEntry.toString());
				Iterator<String> tokenIt = dicEntry.iterator();
				
				while (tokenIt.hasNext()) {
					String token = tokenIt.next();
					Iterator<String> mentionIt = mentions.iterator();
					String[] partString;				
					while (mentionIt.hasNext()) {						
						String mention = mentionIt.next(); 											
						if (token.contains(mention)) { //complete overlapping
							fusedModel.add(token);
						} else if (mention.contains(token)) {
							fusedModel.add(mention);
						} else if ( (partString = partiallyOverlaps(token, mention)) != null) { //partial overlapping
							fusedModel.add(rebuildString(partString));						
						} else {
							//as it is a set, we will not have duplicates
							fusedModel.add(token);
							fusedModel.add(mention);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mentions;
	}

	private static String rebuildString(String[] partString) {
		String f = "";
		for (String s : partString) {
			f += s + " ";
		}
		return f.trim();
	}

	private static String[] partiallyOverlaps(String token, String mention) {		
		if (!token.equals("") && !mention.equals("")) { //we suppose the mention does not contain any punctuation
			String[] tokenArray = token.split(" ");
			String[] mentionArray = mention.split(" ");
			//TODO https://neil.fraser.name/news/2010/11/04/ ?
			
		}
		return null;
	}

	public static Span[] statNamedEntityFinder(String dico, String[] sentence, String modelL) {

		try {
			InputStream dictFileIn = new FileInputStream(
					dico);
			Dictionary dictIn = new Dictionary(dictFileIn);
			
			AdaptiveFeatureGenerator featureGenerator = new CachedFeatureGenerator(
					new AdaptiveFeatureGenerator[] {
							new DictionaryFeatureGenerator(dictIn),
							new CharacterNgramFeatureGenerator(2, 5),
							new WindowFeatureGenerator(
									new TokenFeatureGenerator(), 2, 2),
							new WindowFeatureGenerator(
									new TokenClassFeatureGenerator(true), 2, 2),
							new OutcomePriorFeatureGenerator(),
							new PreviousMapFeatureGenerator(),
							new BigramNameFeatureGenerator(),
							new SentenceFeatureGenerator(true, false) });

			InputStream modelIn;
			modelIn = new FileInputStream(modelL);
			TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
			System.out.println("Loading the model...");
			NameFinderME nameFinder = new NameFinderME(model, featureGenerator,
					NameFinderME.DEFAULT_BEAM_SIZE);
			Span nameSpans[] = nameFinder.find(sentence);
			//nameFinder.clearAdaptiveData();
			System.out.println("Finished...");
			return nameSpans;

		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
	}
}
