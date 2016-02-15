package club.poolpadel

import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.app.Fragment
import android.net.Uri
import android.os.Build
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.View
import com.firebase.client.AuthData
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import android.support.v4.app.Fragment as SupportFragment

/* ******************* */
/*  Application utils  */
/* ******************* */

fun relativeTime(unixtime: Int) = DateUtils.getRelativeTimeSpanString(unixtime.date.time,
        System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)

val AuthData.displayName: String  get() = providerData["displayName"] as? String ?: ""
val AuthData.email: String  get() = providerData["email"] as? String ?: ""
val AuthData.pic: String  get() = providerData["profileImageURL"] as? String ?: ""

val CONTENT_AUTHORITY = "club.poolpadel"
val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")

/* ************ */
/*  Java utils  */
/* ************ */

fun timestamp(): Int = Calendar.getInstance().let {
    it.set(Calendar.SECOND, 0)
    it.set(Calendar.MILLISECOND, 0)
    return it.timestamp
}

val Calendar.timestamp: Int get() = (timeInMillis / 1000L).toInt()

val Int.calendar: Calendar get() = Calendar.getInstance().apply { timeInMillis = this@calendar * 1000L }

val Int.date: Date get() = Date(this * 1000L)

/* *************** */
/*  Android utils  */
/* *************** */

fun FragmentManager.tx(f: FragmentTransaction.() -> Unit): Unit {
    beginTransaction().apply(f).commit()
}

// https://github.com/JakeWharton/kotterknife/blob/master/src/main/kotlin/butterknife/ButterKnife.kt
fun <V : View> View.bindView(id: Int): ReadOnlyProperty<View, V> = required(id, viewFinder)

fun <V : View> Activity.bindView(id: Int): ReadOnlyProperty<Activity, V> = required(id, viewFinder)
fun <V : View> Dialog.bindView(id: Int): ReadOnlyProperty<Dialog, V> = required(id, viewFinder)
fun <V : View> Fragment.bindView(id: Int): ReadOnlyProperty<Fragment, V> = required(id, viewFinder)
fun <V : View> android.support.v4.app.Fragment.bindView(id: Int): ReadOnlyProperty<android.support.v4.app.Fragment, V> = required(id, viewFinder)
fun <V : View> RecyclerView.ViewHolder.bindView(id: Int): ReadOnlyProperty<RecyclerView.ViewHolder, V> = required(id, viewFinder)

fun <V : View> View.bindOptionalView(id: Int): ReadOnlyProperty<View, V?> = optional(id, viewFinder)
fun <V : View> Activity.bindOptionalView(id: Int): ReadOnlyProperty<Activity, V?> = optional(id, viewFinder)
fun <V : View> Dialog.bindOptionalView(id: Int): ReadOnlyProperty<Dialog, V?> = optional(id, viewFinder)
fun <V : View> Fragment.bindOptionalView(id: Int): ReadOnlyProperty<Fragment, V?> = optional(id, viewFinder)
fun <V : View> android.support.v4.app.Fragment.bindOptionalView(id: Int): ReadOnlyProperty<android.support.v4.app.Fragment, V?> = optional(id, viewFinder)
fun <V : View> RecyclerView.ViewHolder.bindOptionalView(id: Int): ReadOnlyProperty<RecyclerView.ViewHolder, V?> = optional(id, viewFinder)

fun <V : View> View.bindViews(vararg ids: Int): ReadOnlyProperty<View, List<V>> = required(ids, viewFinder)
fun <V : View> Activity.bindViews(vararg ids: Int): ReadOnlyProperty<Activity, List<V>> = required(ids, viewFinder)
fun <V : View> Dialog.bindViews(vararg ids: Int): ReadOnlyProperty<Dialog, List<V>> = required(ids, viewFinder)
fun <V : View> Fragment.bindViews(vararg ids: Int): ReadOnlyProperty<Fragment, List<V>> = required(ids, viewFinder)
fun <V : View> android.support.v4.app.Fragment.bindViews(vararg ids: Int): ReadOnlyProperty<android.support.v4.app.Fragment, List<V>> = required(ids, viewFinder)
fun <V : View> RecyclerView.ViewHolder.bindViews(vararg ids: Int): ReadOnlyProperty<RecyclerView.ViewHolder, List<V>> = required(ids, viewFinder)

fun <V : View> View.bindOptionalViews(vararg ids: Int): ReadOnlyProperty<View, List<V>> = optional(ids, viewFinder)
fun <V : View> Activity.bindOptionalViews(vararg ids: Int): ReadOnlyProperty<Activity, List<V>> = optional(ids, viewFinder)
fun <V : View> Dialog.bindOptionalViews(vararg ids: Int): ReadOnlyProperty<Dialog, List<V>> = optional(ids, viewFinder)
fun <V : View> Fragment.bindOptionalViews(vararg ids: Int): ReadOnlyProperty<Fragment, List<V>> = optional(ids, viewFinder)
fun <V : View> android.support.v4.app.Fragment.bindOptionalViews(vararg ids: Int): ReadOnlyProperty<android.support.v4.app.Fragment, List<V>> = optional(ids, viewFinder)
fun <V : View> RecyclerView.ViewHolder.bindOptionalViews(vararg ids: Int): ReadOnlyProperty<RecyclerView.ViewHolder, List<V>> = optional(ids, viewFinder)

private val View.viewFinder: View.(Int) -> View? get() = { findViewById(it) }
private val Activity.viewFinder: Activity.(Int) -> View? get() = { findViewById(it) }
private val Dialog.viewFinder: Dialog.(Int) -> View? get() = { findViewById(it) }
private val Fragment.viewFinder: Fragment.(Int) -> View? @TargetApi(Build.VERSION_CODES.HONEYCOMB) get() = { view.findViewById(it) }
private val android.support.v4.app.Fragment.viewFinder: android.support.v4.app.Fragment.(Int) -> View? get() = { view?.findViewById(it) }
private val RecyclerView.ViewHolder.viewFinder: RecyclerView.ViewHolder.(Int) -> View? get() = { itemView.findViewById(it) }

private fun viewNotFound(id: Int, desc: KProperty<*>): Nothing = throw IllegalStateException("View ID $id for '${desc.name}' not found.")

@Suppress("UNCHECKED_CAST")
private fun <T, V : View> required(id: Int, finder: T.(Int) -> View?) = Lazy { t: T, desc -> t.finder(id) as V? ?: viewNotFound(id, desc) }

@Suppress("UNCHECKED_CAST")
private fun <T, V : View> optional(id: Int, finder: T.(Int) -> View?) = Lazy { t: T, desc -> t.finder(id) as V? }

@Suppress("UNCHECKED_CAST")
private fun <T, V : View> required(ids: IntArray, finder: T.(Int) -> View?) = Lazy { t: T, desc -> ids.map { t.finder(it) as V? ?: viewNotFound(it, desc) } }

@Suppress("UNCHECKED_CAST")
private fun <T, V : View> optional(ids: IntArray, finder: T.(Int) -> View?) = Lazy { t: T, desc -> ids.map { t.finder(it) as V? }.filterNotNull() }

// Like Kotlin's lazy delegate but the initializer gets the target and metadata passed to it
private class Lazy<T, V>(private val initializer: (T, KProperty<*>) -> V) : ReadOnlyProperty<T, V> {
    private object EMPTY

    private var value: Any? = EMPTY

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        if (value == EMPTY) {
            value = initializer(thisRef, property)
        }
        @Suppress("UNCHECKED_CAST")
        return value as V
    }
}


