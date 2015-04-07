package de.predic8.meinebegruessungsanwendung;

import android.test.ActivityInstrumentationTestCase2;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.text.StringContains.*;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public void testBegruessung() {
        onView(withId(R.id.editText)).perform(typeText("Tobias"));
        onView(withId(R.id.button)).perform(click());
        onView(withText(containsString("Hallo"))).check(matches(withText(containsString("Tobias"))));
    }
}
