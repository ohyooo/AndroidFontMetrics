package net.studymongolian.fontmetrics

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import net.studymongolian.fontmetrics.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val vm by viewModels<MainViewModel>()
    private val vdb by lazy(LazyThreadSafetyMode.NONE) { ActivityMainBinding.inflate(layoutInflater).apply { vm = this@MainActivity.vm } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(vdb.root)

        findViewById<View>(R.id.updateButton).setOnClickListener(this)

        updateTextViews()

        initFontList()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.updateButton -> {
                vdb.viewWindow.setText(vdb.etTextString.text.toString())
                val fontSize = try {
                    Integer.valueOf(vdb.etFontSize.text.toString())
                } catch (e: NumberFormatException) {
                    FontMetricsView.DEFAULT_FONT_SIZE_PX
                }

                vdb.viewWindow.setTextSizeInPixels(fontSize)
                updateTextViews()
                hideKeyboard(currentFocus)
            }

            R.id.cbTop -> vdb.viewWindow.setTopVisible(vdb.cbTop.isChecked)
            R.id.cbAscent -> vdb.viewWindow.setAscentVisible(vdb.cbAscent.isChecked)
            R.id.cbBaseline -> vdb.viewWindow.setBaselineVisible(vdb.cbBaseline.isChecked)
            R.id.cbDescent -> vdb.viewWindow.setDescentVisible(vdb.cbDescent.isChecked)
            R.id.cbBottom -> vdb.viewWindow.setBottomVisible(vdb.cbBottom.isChecked)
            R.id.cbBounds -> vdb.viewWindow.setBoundsVisible(vdb.cbBounds.isChecked)
            R.id.cbWidth -> vdb.viewWindow.setWidthVisible(vdb.cbWidth.isChecked)
        }
    }

    fun updateTextViews() {
        vdb.tvTop.text = "${vdb.viewWindow.fontMetrics.top}"
        vdb.tvAscent.text = "${vdb.viewWindow.fontMetrics.ascent}"
        vdb.tvBaseline.text = 0F.toString()
        vdb.tvDescent.text = "${vdb.viewWindow.fontMetrics.descent}"
        vdb.tvBottom.text = "${vdb.viewWindow.fontMetrics.bottom}"
        vdb.tvBounds.text = "w = ${vdb.viewWindow.textBounds.width()} h = ${vdb.viewWindow.textBounds.height()}"
        vdb.tvWidth.text = "${vdb.viewWindow.measuredTextWidth}"
        vdb.tvLeading.text = "${vdb.viewWindow.fontMetrics.leading}"
    }

    private fun hideKeyboard(view: View?) {
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
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