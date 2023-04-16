package ca.yorku.eecs.mack.Project4443;

import java.util.Random;

/*
 * A class to hold the information for a quiz question. The quotation for the question (quoteIdx) is
 * selected at random from the quotation array. Of course, we only need to store the index of
 * the selected quotation. We also need an answer array for the possible answers (answerArray).
 * The entries in this array are also randomly selected indices from the quotation array. One
 * tricky detail is to ensure that the answer array does not have any repeated indices. Of
 * course, one entry in the answer array must be the index of the correct answer, and we need a
 * place-holder for that (answerArrayCorrectIdx).
 */
public class Question
{
	int cardIdx; // index in quotation array of the quote for the question.
	String[] answerArray; // the array of possible answers (also indices)
	int answerArrayCorrectIdx; // the index in the answer array of the correct answer
	Random r;
	int numberOfAnswers;

	Question(int cardIdxxArg, int numberOfAnswersArg)
	{
		String word = MainActivity.words.get(cardIdxxArg);
		cardIdx = cardIdxxArg;
		numberOfAnswers = numberOfAnswersArg;

		r = new Random();

		// create an array of answers (indices into the quotation array)
		answerArray = new String[numberOfAnswers];

		// fill the answer array with unique and wrong random indices
		do
		{
			fillRandom(word);
		} while (repeats());

		// replace one of the entries with the idx of the correct answer
		//answerArrayCorrectIdx = r.nextInt(numberOfAnswers);
		//for test purpose
		answerArrayCorrectIdx = 2;
		answerArray[answerArrayCorrectIdx] = word;
	}

	// randomize the order of character.
	private void fillRandom(String word)
	{

		char randomChar;
		//make sure word is not too short
		int randomNumber = r.nextInt(word.length()+3)+2;

		for (int i = 0; i < answerArray.length; ++i)
		{
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j <randomNumber; j++) {
				randomChar = (char)(r.nextInt(26) + 'a');
				sb.append(randomChar);
			}
			answerArray[i]=sb.toString();
		}
	}

	// returns true if there are any repeated values in the answer array
	private boolean repeats()
	{
		for (int i = 0; i < answerArray.length; ++i)
			for (int j = i + 1; j < answerArray.length; ++j)
				if (answerArray[i] == answerArray[j])
					return true;
		return false;
	}

}