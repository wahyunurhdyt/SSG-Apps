package id.semisama.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import id.semisama.app.adapter.ViewPagerAdapter
import id.semisama.app.api.data.Boarding
import id.semisama.app.base.BaseActivity
import id.semisama.app.ui.navigation.ActivityNavigation
import id.semisama.app.utilily.Constant.boardingTemps
import id.semisama.app.utilily.Constant.getBoardingPass
import id.semisama.app.utilily.getPx
import id.semisama.app.utilily.launchNewActivity
import kotlinx.android.synthetic.main.activity_boarding.*

class ActivityBoarding : BaseActivity() {

    private lateinit var sliderAdapter: ViewPagerAdapter<Boarding>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boarding)
        if (haveLocationPermissions()) {
            fetchLocation()
        }
        handlerSlider()
    }

    @SuppressLint("SetTextI18n")
    private fun handlerSlider() {
        this.sliderAdapter = ViewPagerAdapter(this,
            R.layout.item_boarding,
            listOf()
        ) { itemView, item, position ->
            val labelCountOf = itemView.findViewById<TextView>(R.id.labelCountOf)
            val labelTitle = itemView.findViewById<TextView>(R.id.labelTitle)
            val labelDesc = itemView.findViewById<TextView>(R.id.labelDesc)
            val image = itemView.findViewById<ImageView>(R.id.ivImage)
            val btnNext = itemView.findViewById<LinearLayout>(R.id.btnNext)
            labelCountOf.text = "${position + 1} of ${sliderAdapter.count}"
            labelTitle.text = item.title
            labelDesc.text = item.desc
            image.setImageResource(item.image)
            btnNext.setOnClickListener {
                if (position == 2) {
                    cache.set(boardingTemps, "true")
                    launchNewActivity(ActivityNavigation::class.java)
                    finish()
                } else {
                    vpBanner.currentItem = position + 1
                }
            }
        }
        vpBanner.adapter = this.sliderAdapter
        vpBanner.apply {
            adapter = sliderAdapter
            setPadding(
                getPx(0),
                getPx(0),
                getPx(0),
                getPx(0))
            clipToPadding = false
            pageMargin = getPx(0)
        }
        vpBanner.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
            }
        })
        this.sliderAdapter.data = getBoardingPass()
    }
}