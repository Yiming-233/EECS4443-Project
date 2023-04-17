package ca.yorku.eecs.mack.Project4443;


import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest2 {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest2() {
        ViewInteraction button = onView(
                allOf(withId(R.id.StartQuizButton), withText("Start the Quiz"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        button.perform(click());

        ViewInteraction button2 = onView(
                allOf(withId(R.id.AddCardButton), withText("Add New Card"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                0),
                        isDisplayed()));
        button2.perform(click());

        ViewInteraction editText = onView(
                allOf(withId(R.id.wordText),
                        childAtPosition(
                                allOf(withId(R.id.results_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        editText.perform(click());

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.wordText),
                        childAtPosition(
                                allOf(withId(R.id.results_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        editText2.perform(replaceText("1"), closeSoftKeyboard());

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.defText),
                        childAtPosition(
                                allOf(withId(R.id.results_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        editText3.perform(click());

        ViewInteraction editText4 = onView(
                allOf(withId(R.id.defText),
                        childAtPosition(
                                allOf(withId(R.id.results_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        editText4.perform(replaceText("1"), closeSoftKeyboard());

        ViewInteraction button3 = onView(
                allOf(withId(R.id.addButton), withText("Add Card"),
                        childAtPosition(
                                allOf(withId(R.id.results_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        button3.perform(click());

        ViewInteraction editText5 = onView(
                allOf(withId(R.id.defText),
                        childAtPosition(
                                allOf(withId(R.id.results_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        editText5.perform(replaceText("2"), closeSoftKeyboard());

        ViewInteraction editText6 = onView(
                allOf(withId(R.id.defText), withText("2"),
                        childAtPosition(
                                allOf(withId(R.id.results_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        editText6.perform(click());

        ViewInteraction editText7 = onView(
                allOf(withId(R.id.wordText),
                        childAtPosition(
                                allOf(withId(R.id.results_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        editText7.perform(replaceText("2"), closeSoftKeyboard());

        ViewInteraction button4 = onView(
                allOf(withId(R.id.addButton), withText("Add Card"),
                        childAtPosition(
                                allOf(withId(R.id.results_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        button4.perform(click());

        pressBack();

        ViewInteraction button5 = onView(
                allOf(withId(R.id.StartQuizButton), withText("Start the Quiz"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        button5.perform(click());

        ViewInteraction button6 = onView(
                allOf(withId(R.id.quiz_answer_3), withText("2"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.quiz_container1),
                                        0),
                                2),
                        isDisplayed()));
        button6.perform(click());

        ViewInteraction button7 = onView(
                allOf(withId(android.R.id.button3), withText("Continue"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        button7.perform(click());

        ViewInteraction button8 = onView(
                allOf(withId(android.R.id.button3), withText("Continue"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        button8.perform(click());

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        ViewInteraction textView = onView(
                allOf(withId(android.R.id.title), withText("Settings"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        textView.perform(click());

        DataInteraction linearLayout = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list),
                        childAtPosition(
                                withId(android.R.id.list_container),
                                0)))
                .atPosition(0);
        linearLayout.perform(click());

        DataInteraction checkedTextView = onData(anything())
                .inAdapterView(allOf(withClassName(is("com.android.internal.app.AlertController$RecycleListView")),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                0)))
                .atPosition(1);
        checkedTextView.perform(click());

        pressBack();

        ViewInteraction button9 = onView(
                allOf(withId(R.id.StartQuizButton), withText("Start the Quiz"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        button9.perform(click());

        ViewInteraction button10 = onView(
                allOf(withId(R.id.quiz_answer_3), withText("2"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.quiz_container1),
                                        0),
                                2),
                        isDisplayed()));
        button10.perform(click());

        ViewInteraction button11 = onView(
                allOf(withId(android.R.id.button3), withText("Continue"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        button11.perform(click());

        ViewInteraction button12 = onView(
                allOf(withId(android.R.id.button3), withText("Continue"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        button12.perform(click());

        ViewInteraction button13 = onView(
                allOf(withId(R.id.StartQuizButton), withText("Start the Quiz"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        button13.perform(click());

        ViewInteraction button14 = onView(
                allOf(withId(R.id.quiz_answer_3), withText("2"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.quiz_container1),
                                        0),
                                2),
                        isDisplayed()));
        button14.perform(click());

        ViewInteraction button15 = onView(
                allOf(withId(android.R.id.button3), withText("Continue"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        button15.perform(click());

        ViewInteraction button16 = onView(
                allOf(withId(R.id.quiz_answer_3), withText("1"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.quiz_container1),
                                        0),
                                2),
                        isDisplayed()));
        button16.perform(click());

        ViewInteraction button17 = onView(
                allOf(withId(android.R.id.button3), withText("Continue"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        button17.perform(click());

        ViewInteraction button18 = onView(
                allOf(withId(android.R.id.button3), withText("Continue"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        button18.perform(click());

        ViewInteraction button19 = onView(
                allOf(withId(R.id.StartQuizButton), withText("Start the Quiz"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        button19.perform(click());

        ViewInteraction button20 = onView(
                allOf(withId(R.id.quiz_answer_1), withText("vxjw"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.quiz_container1),
                                        0),
                                0),
                        isDisplayed()));
        button20.perform(click());

        ViewInteraction button21 = onView(
                allOf(withId(R.id.quiz_answer_3), withText("1"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.quiz_container1),
                                        0),
                                2),
                        isDisplayed()));
        button21.perform(click());

        ViewInteraction button22 = onView(
                allOf(withId(android.R.id.button3), withText("Continue"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        button22.perform(click());

        ViewInteraction button23 = onView(
                allOf(withId(R.id.quiz_answer_1), withText("tjn"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.quiz_container1),
                                        0),
                                0),
                        isDisplayed()));
        button23.perform(click());

        ViewInteraction button24 = onView(
                allOf(withId(R.id.quiz_answer_3), withText("2"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.quiz_container1),
                                        0),
                                2),
                        isDisplayed()));
        button24.perform(click());

        ViewInteraction button25 = onView(
                allOf(withId(android.R.id.button3), withText("Continue"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        button25.perform(click());

        ViewInteraction button26 = onView(
                allOf(withId(android.R.id.button3), withText("Continue"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        button26.perform(click());
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
