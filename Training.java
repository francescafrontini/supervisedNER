package fr.lip6.supervisedNER;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
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
 * Training methods with OpenNLP.
 * 
 * @author Francesca Frontini & Carmen Brando - Labex OBVIL - Université
 *         Paris-Sorbonne - UPMC-LIP6
 */
public class Training {

	public static void trainingNER(String file) {

		try {
			InputStream dictFileIn = new FileInputStream(
					"dictionary/dictionary.xml");

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

			BufferedOutputStream modelOut = null;

			ObjectStream<String> lineStream = new PlainTextByLineStream(
					new FileInputStream(file), "UTF-8");

			ObjectStream<NameSample> sampleStream = new NameSampleDataStream(
					lineStream);

			Map<String, Object> mainParam = new HashMap<String, Object>();
			// mainParam.put("Algorithm", "PERCEPTRON");

			TokenNameFinderModel model = NameFinderME.train("fr", null,
					sampleStream, featureGenerator, mainParam, 500, 5);

			try {
				modelOut = new BufferedOutputStream(new FileOutputStream(
						file.replace(".ner", ".mod")));
				model.serialize(modelOut);
			} finally {
				if (modelOut != null)
					modelOut.close();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
