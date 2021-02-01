package org.reduxkotlin.reselect.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.reduxkotlin.Reducer
import org.reduxkotlin.StoreSubscription
import org.reduxkotlin.Thunk
import org.reduxkotlin.applyMiddleware
import org.reduxkotlin.createStore
import org.reduxkotlin.createThunkMiddleware
import org.reduxkotlin.select
import org.reduxkotlin.selectors

class MainActivity : AppCompatActivity() {

    lateinit var loadingIndicator: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        loadingIndicator = findViewById(R.id.loading_indicator)

        fab.setOnClickListener { view ->
            store.dispatch(networkRequest())
            Snackbar.make(view, "Make a fake network request", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

//        subscription = store.select({ it.isLoading }) { isLoading ->
//            loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
//        }


        multiSubscription = store.selectors<AppState, Boolean> {
            select({ it.isLoading }) { isLoading ->
                loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    lateinit var subscription: StoreSubscription

    lateinit var multiSubscription: StoreSubscription

    override fun onDestroy() {
        super.onDestroy()
//        subscription()
        multiSubscription()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

/**
 * A fake network request thunk.  Just delays then dispatches a LoadingCompleteAction
 */
fun networkRequest(): Thunk<AppState> = { dispatch, getState, extraArg ->
    dispatch(StartLoading)
    GlobalScope.launch {
        delay(5000L)
        withContext(Dispatchers.Main) {
            dispatch(LoadingComplete)
        }
    }
}

object IncrementAction
object StartLoading
object LoadingComplete

val reducer: Reducer<AppState> = { state, action ->
    when (action) {
        StartLoading -> state.copy(isLoading = true)
        LoadingComplete -> state.copy(isLoading = false)
        IncrementAction -> state.copy(counter = state.counter + 1)
        else -> state
    }
}

val store = createStore(reducer, AppState(), applyMiddleware(createThunkMiddleware()))
