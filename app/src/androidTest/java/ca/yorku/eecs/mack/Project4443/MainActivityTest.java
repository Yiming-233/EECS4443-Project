package ca.yorku.eecs.mack.Project4443;


import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;

@LargeTest
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainActivityTest {
    final int load = 5;
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);


    @Test
    public void test1ViewEmptyCardTest(){
        ViewInteraction button = onView(
                allOf(withId(R.id.ViewCardButton), withText("View Card Deck"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        //step 1 click view card
        button.perform(click());
        //result i'm still at home page
        ViewInteraction button2 = onView(
                allOf(withId(R.id.ViewCardButton), withText("View Card Deck"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
    }

    @Test
    public void test2AddCardTest(){
        ViewInteraction button = onView(
                allOf(withId(R.id.AddCardButton), withText("Add New Card"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                0),
                        isDisplayed()));
        ViewInteraction editText = onView(
                allOf(withId(R.id.wordText),
                        childAtPosition(
                                allOf(withId(R.id.results_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        ViewInteraction editText2 = onView(
                allOf(withId(R.id.defText),
                        childAtPosition(
                                allOf(withId(R.id.results_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        ViewInteraction button2 = onView(
                allOf(withId(R.id.addButton), withText("Add Card"),
                        childAtPosition(
                                allOf(withId(R.id.results_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        String word, def;
        ArrayList<String> words = new ArrayList<String>();
        ArrayList<String> defs = new ArrayList<String>();
        //step1 click add card
        button.perform(click());
        for (int i = 1; i < (load+1); i++){
            word = "Word" + i;
            def = "Def"+i;
            words.add(word);
            defs.add(def);
            //step2 enter word
            editText.perform(replaceText(word), closeSoftKeyboard());
            //step3 enter definition
            editText2.perform(replaceText(def), closeSoftKeyboard());
            //step 4 click confirm
            button2.perform(click());
        }
        // i should have load cards, and result should match
        assertEquals(mActivityTestRule.getActivity().words.size(),load);
    }

    @Test
    public void test3ViewNoneEmptyCardTest(){
        ViewInteraction button = onView(
                allOf(withId(R.id.ViewCardButton), withText("View Card Deck"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        DataInteraction textView = onData(anything())
                .inAdapterView(allOf(withId(R.id.list_view),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                1)))
                .atPosition(0);
        ViewInteraction fragment = onView(withId(R.id.container));

        //step 1 click view card
        button.perform(click());
        //step 2 select 1st item
        textView.perform(click());
        for(int i = 0; i < load; i++){
            //step 3 swipe to see def
            fragment.perform(swipeLeft());
            //step 4 to see next card
            fragment.perform(click());
        }
        //Result i should at index 0
        assertEquals(FlashCardActivity.cardIdx, 0);
    }

    @Test
    public void test4DeleteCardTest(){
        ViewInteraction button = onView(
                allOf(withId(R.id.ViewCardButton), withText("View Card Deck"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        DataInteraction textView = onData(anything())
                .inAdapterView(allOf(withId(R.id.list_view),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                1)))
                .atPosition(load-1);
        ViewInteraction textView2 = onView(
                allOf(withId(android.R.id.title), withText("Delete"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));

        ArrayList<String> words = new ArrayList<String>();
        ArrayList<String> defs = new ArrayList<String>();
        String word, def;
        for (int i = 1; i < (load+1); i++) {
            word = "Word" + i;
            def = "Def" + i;
            words.add(word);
            defs.add(def);
        }
        String temp = words.get(load-1);
        //step 1 click view card
        button.perform(click());
        //step 2 select removeId-st item
        textView.perform(click());
        //step 3 select delete
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        textView2.perform(click());

        //Result size should now be load-1
        assertEquals(mActivityTestRule.getActivity().words.size(),load-1);
        assertFalse(mActivityTestRule.getActivity().words.contains(temp));
    }

    @Test
    public void test5Return1Test(){
        ViewInteraction button = onView(
                allOf(withId(R.id.ViewCardButton), withText("View Card Deck"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        DataInteraction textView = onData(anything())
                .inAdapterView(allOf(withId(R.id.list_view),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                1)))
                .atPosition(0);

        //step 1 click view card
        button.perform(click());
        //step 2 select 5st item
        textView.perform(click());
        //step 3 press back twice to return to home page
        pressBack();
        pressBack();
        //result i should return to home
        ViewInteraction button2 = onView(
                allOf(withId(R.id.ViewCardButton), withText("View Card Deck"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
    }

    @Test
    public void test6Return2Test(){
        ViewInteraction button = onView(
                allOf(withId(R.id.ViewCardButton), withText("View Card Deck"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        DataInteraction textView = onData(anything())
                .inAdapterView(allOf(withId(R.id.list_view),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                1)))
                .atPosition(0);
        ViewInteraction fragment = onView(withId(R.id.container));

        //step 1 click view card
        button.perform(click());
        //step 2 select 5st item
        textView.perform(click());
        //step 3 perform long press to return to home page
        fragment.perform(longClick());
        //result i should return to home
        ViewInteraction button2 = onView(
                allOf(withId(R.id.ViewCardButton), withText("View Card Deck"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
    }

    @Test
    public void test7QuizAllWrongTest() {
        ViewInteraction button = onView(
                allOf(withId(R.id.StartQuizButton), withText("Start the Quiz"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));

        //step 1 click start quiz
        button.perform(click());

        ViewInteraction button2 = onView(ViewMatchers.withId(R.id.quiz_answer_1));
        ViewInteraction button3 = onView(ViewMatchers.withId(R.id.quiz_answer_3));
        ViewInteraction button4 = onView(
                allOf(withId(android.R.id.button3), withText("Continue"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        ViewInteraction button5 = onView(
                allOf(withId(android.R.id.button3), withText("Continue"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));

        for(int i = 0; i < 2; i++){
            //step 2 select wrong answer
            button2.perform(click());
            //step 3 select correct answer
            button3.perform(click());
            //step 4 continue
            button4.perform(click());
        }
        //step 5 to check result
        button5.perform(click());
        //result i should have 0 correct and 2 incorrect
        assertEquals(QuizActivity.numberCorrect,0);
        assertEquals(QuizActivity.numberIncorrect,2);
    }

    @Test
    public void test8QuizAllCorrectTest() {
        ViewInteraction button = onView(
                allOf(withId(R.id.StartQuizButton), withText("Start the Quiz"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));

        //step 1 click start quiz
        button.perform(click());

        ViewInteraction button2 = onView(ViewMatchers.withId(R.id.quiz_answer_3));
        ViewInteraction button3 = onView(
                allOf(withId(android.R.id.button3), withText("Continue"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        ViewInteraction button5 = onView(
                allOf(withId(android.R.id.button3), withText("Continue"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));

        for(int i = 0; i < 2; i++){
            //step 2 select correct answer
            button2.perform(click());
            //step 3 continue
            button3.perform(click());
        }
        //step 5 to check result
        button5.perform(click());
        assertEquals(QuizActivity.numberIncorrect,0);
        assertEquals(QuizActivity.numberCorrect,2);
    }
    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }
            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
