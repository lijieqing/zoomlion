package com.kstech.zoomlion.utils;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

/**
 * Created by lijie on 2017/3/29.
 */

public class ChartUtils {
    public static void setChartData(LineChart chart, List<Entry> values, int index) {
        LineDataSet lineDataSet;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            lineDataSet = (LineDataSet) chart.getData().getDataSetByIndex(index);
            lineDataSet.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            lineDataSet = new LineDataSet(values, "");
            // 设置曲线颜色
            lineDataSet.setColor(Color.RED);
            // 设置平滑曲线
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            // 不显示坐标点的小圆点
            lineDataSet.setDrawCircles(false);
            // 不显示坐标点的数据
            lineDataSet.setDrawValues(false);
            // 不显示定位线
            lineDataSet.setHighlightEnabled(false);

            LineData data = new LineData(lineDataSet);
            chart.setData(data);
            chart.invalidate();
        }
    }
    /**
     * 更新图表
     *
     * @param chart   图表
     * @param values  数据
     * @param valueType 数据类型
     */
    public static void notifyDataSetChanged(LineChart chart, List<Entry> values , int index) {
        chart.invalidate();
        setChartData(chart, values,index);
    }
}
