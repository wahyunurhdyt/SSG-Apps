package id.semisama.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter

// Custom View Pager Adapter
class ViewPagerAdapter<T>(var context: Context,
                          var layout: Int,
                          private var items: List<T>,
                          var view:(view: View, T, position: Int) -> Unit): PagerAdapter() {


    var data = this.items
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun getCount() = this.data.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(layout, container, false)
        this.view(view, data[position], position)

        container.addView(view)
        return view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }
}