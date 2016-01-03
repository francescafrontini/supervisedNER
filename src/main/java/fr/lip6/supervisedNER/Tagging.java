package fr.lip6.supervisedNER;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;
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
				String offset = nameSpans[i].getStart() + " "
						+ nameSpans[i].getEnd();
				String mention = "";
				for (int j = nameSpans[i].getStart(); j < nameSpans[i].getEnd(); j++) {
					mention += wL[j];
				}
				offset += "\t" + mention;
				mentions.add(mention);
				System.out.println(offset);				
			}
			br.close();
			// inject annotations into TEI 
			// perform substitutions per NE mention along the whole file (brute force..)
			String neTag = "";
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
				for (String m : mentions) {
					input = input.replaceAll(m, "<"+neTag+">"+m+"</"+neTag+">");
					//TODO as expected, this is not working properly, overlapping issues
				}
				brWriteTEI.write(input + System.lineSeparator());
			}
			brReadTEI.close();
			brWriteTEI.close();
		} catch (IOException io) {
			io.printStackTrace();
		}
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
