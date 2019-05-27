package com.umutbey.userlist.view


import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.umutbey.userlist.view.adapter.UserListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.R
import android.R.attr.showText
import androidx.test.espresso.Espresso.onView
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.umutbey.userlist.helpers.get
import com.umutbey.userlist.view.MainActivity.Companion.GRID
import com.umutbey.userlist.view.MainActivity.Companion.LIST
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher


@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)


    @Test
    @Throws(Exception::class)
    fun ensureRecyclerViewIsPresent() {
        val activity = mActivityTestRule.getActivity()
        val rView = activity.recyclerView as RecyclerView
        assertThat(rView, notNullValue())
        assertThat(rView, instanceOf(RecyclerView::class.java))
        val adapter = rView.adapter
        assertThat(adapter, instanceOf(UserListAdapter::class.java))
        Thread.sleep(3000)
        val itemCount = (adapter as UserListAdapter).itemCount
        assertThat("List is not empty", itemCount, greaterThan(0))

    }
    @Test
    @Throws(Exception::class)
    fun checkSharedPreferenceForListType() {
        val activity = mActivityTestRule.activity
        val actionMenuItemView = onView(
            allOf(
                ViewMatchers.withId(com.umutbey.userlist.R.id.action_list),
                ViewMatchers.withContentDescription("List"),
                childAtPosition(
                    childAtPosition(
                        ViewMatchers.withId(com.umutbey.userlist.R.id.toolbar),
                        1
                    ),
                    0
                ),
                ViewMatchers.isDisplayed()
            )
        )
        actionMenuItemView.perform(ViewActions.click())
        val prefs = activity.getSharedPreferences(MainActivity.PREFS_FILENAME,0)
        if (prefs.get<Int>(MainActivity.LIST_TYPE, 0) != 0) {
            assertThat(LIST, equalTo(0))
        } else {
            assertThat(GRID, equalTo(1))

        }

    }
    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

}
