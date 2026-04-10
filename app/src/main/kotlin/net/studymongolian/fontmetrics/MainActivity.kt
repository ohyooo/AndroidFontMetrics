package net.studymongolian.fontmetrics

import android.graphics.Typeface
import android.os.Bundle
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import net.studymongolian.fontmetrics.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val vm by viewModels<MainViewModel>()
    private val vdb by lazy(LazyThreadSafetyMode.NONE) { ActivityMainBinding.inflate(layoutInflater).apply { vm = this@MainActivity.vm } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(vdb.root)
        applyWindowInsets()
        setupMetricToggles()
        updateTextViews()

        vdb.viewWindow.onInvalidate {
            updateTextViews()
        }

        // initFontList()
    }

    private fun applyWindowInsets() {
        val initialPaddingLeft = vdb.activityMain.paddingLeft
        val initialPaddingTop = vdb.activityMain.paddingTop
        val initialPaddingRight = vdb.activityMain.paddingRight
        val initialPaddingBottom = vdb.activityMain.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(vdb.activityMain) { view, windowInsets ->
            val insets: Insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                left = initialPaddingLeft + insets.left,
                top = initialPaddingTop + insets.top,
                right = initialPaddingRight + insets.right,
                bottom = initialPaddingBottom + insets.bottom
            )
            windowInsets
        }
        ViewCompat.requestApplyInsets(vdb.activityMain)
    }

    private fun setupMetricToggles() {
        bindMetricToggle(vdb.cbTop, vdb.viewWindow::setTopVisible)
        bindMetricToggle(vdb.cbAscent, vdb.viewWindow::setAscentVisible)
        bindMetricToggle(vdb.cbBaseline, vdb.viewWindow::setBaselineVisible)
        bindMetricToggle(vdb.cbDescent, vdb.viewWindow::setDescentVisible)
        bindMetricToggle(vdb.cbBottom, vdb.viewWindow::setBottomVisible)
        bindMetricToggle(vdb.cbBounds, vdb.viewWindow::setBoundsVisible)
        bindMetricToggle(vdb.cbWidth, vdb.viewWindow::setWidthVisible)
    }

    private fun bindMetricToggle(checkBox: CompoundButton, setter: (Boolean) -> Unit) {
        setter(checkBox.isChecked)
        checkBox.setOnCheckedChangeListener { _, isChecked -> setter(isChecked) }
    }

    private fun updateTextViews() {
        vdb.tvTop.text = "${vdb.viewWindow.fontMetrics.top}"
        vdb.tvAscent.text = "${vdb.viewWindow.fontMetrics.ascent}"
        vdb.tvBaseline.text = 0F.toString()
        vdb.tvDescent.text = "${vdb.viewWindow.fontMetrics.descent}"
        vdb.tvBottom.text = "${vdb.viewWindow.fontMetrics.bottom}"
        vdb.tvBounds.text = "${vdb.viewWindow.textBounds.width()}, ${vdb.viewWindow.textBounds.height()}"
        vdb.tvWidth.text = "${vdb.viewWindow.measuredTextWidth}"
        vdb.tvLeading.text = "${vdb.viewWindow.fontMetrics.leading}"
    }

    private fun initFontList() {
        vdb.fontList.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            val list = vm.getSystemFontFiles()

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val v = TextView(parent.context).apply {
                    setPadding(0, 20, 0, 20)
                }
                return object : RecyclerView.ViewHolder(v) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                (holder.itemView as TextView).apply {
                    text = list[position].name
                    setOnClickListener {
                        vdb.viewWindow.setTypeFace(Typeface.createFromFile(list[position]))
                    }
                }
            }

            override fun getItemCount() = list.size
        }
    }

}
