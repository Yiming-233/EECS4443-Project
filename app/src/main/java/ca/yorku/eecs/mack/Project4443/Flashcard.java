package ca.yorku.eecs.mack.Project4443;
/* A simple class to hold the information for a flash card.  The information needed is
 *
 *    - the word
 *    - the definition
 */
public class Flashcard {
    String word, definition;

    Flashcard(String wordArg, String definitionArg)
    {
        word= wordArg;
        definition = definitionArg;
    }
}
