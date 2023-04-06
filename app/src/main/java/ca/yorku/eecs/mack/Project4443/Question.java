package ca.yorku.eecs.mack.Project4443;

import java.util.ArrayList;
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
		answerArrayCorrectIdx = r.nextInt(numberOfAnswers);
		answerArray[answerArrayCorrectIdx] = word;
	}

	// randomize the order of string.
	private void fillRandom(String word)
	{
		StringBuilder sb = new StringBuilder(word);
		String shuffledStr;
		char randomChar;
		int remain = 4 - word.length();

		if(remain >0){//if the word length < 4, add random char to make it longer
			for (int i = 0; i < remain; i++){
				randomChar = (char)(r.nextInt(26) + 'a');
				sb.append(randomChar);
			}
		}

		for (int i = 0; i < answerArray.length; ++i)
		{
			for (int j = sb.length() - 1; j > 0; j--) {
				int k = r.nextInt(j + 1);
				char temp = sb.charAt(j);
				sb.setCharAt(j, sb.charAt(k));
				sb.setCharAt(k, temp);
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