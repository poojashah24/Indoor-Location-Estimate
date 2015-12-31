package edu.columbia.locationsensor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * User interface for the pressure sensor.
 * This displays current pressure readings in a graphical format.
 */
public class PressureActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor pressureSensor;
    TextView textView;
    TextView pressureReadingView;

    private long lastUpdate;
    private boolean firstLoad = true;

    LineChartView chartView;
    RollingList<PointValue> values;
    Axis xaxis;
    int i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressure);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if(pressureSensor == null)
            Toast.makeText(this, R.string.no_pressure_sensor, Toast.LENGTH_SHORT).show();

        textView = (TextView) findViewById(R.id.pressure_text_view);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/digital-7.ttf");
        textView.setTextSize(48);

        pressureReadingView = (TextView)findViewById(R.id.pressure_reading);
        pressureReadingView.setTextSize(72);

        chartView = (LineChartView)findViewById(R.id.chart);
        values = new RollingList<PointValue>(15);
        lastUpdate = System.currentTimeMillis();
        i = 0;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(pressureSensor != null)
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(pressureSensor != null)
            sensorManager.unregisterListener(this, pressureSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Repaint here

        if(firstLoad || System.currentTimeMillis() - lastUpdate >= 1000) {
            float pressureInMilliBars = event.values[0];
            double timeInMillis = System.currentTimeMillis();
            double pressure = pressureInMilliBars;

            if(pressureReadingView != null) {
                String pressureReading = String.format("%.2f", pressureInMilliBars);
                String msg = "" + pressureReading + " MBar";
                pressureReadingView.setText(msg);
                lastUpdate = System.currentTimeMillis();

                if(chartView != null) {
                    PointValue v = new PointValue(i++, (float)pressure);
                    v.setLabel(pressureReading);
                    values.add(v);

                    if(firstLoad) {
                        values.add(v);
                        Line line = new Line(values.getList()).setColor(Color.BLUE);//.setCubic(true);
                        line.setHasLabels(true);
                        List<Line> lines = Collections.singletonList(line);
                        LineChartData data = new LineChartData();
                        data.setLines(lines);

                        List<AxisValue> axisValues = new ArrayList<AxisValue>();
                        xaxis = new Axis(axisValues);
                        xaxis.setLineColor(Color.BLUE);
                        xaxis.setName("Time (in seconds)");
                        xaxis.getValues().add(new AxisValue(i).setLabel(String.valueOf(i)));
                        data.setAxisXBottom(xaxis);

                        Axis yaxis = new Axis();
                        yaxis.setLineColor(Color.BLUE);
                        yaxis.setMaxLabelChars(5);
                        yaxis.setName("Pressure (in mbar)");
                        data.setAxisYLeft(yaxis);

                        chartView.setLineChartData(data);
                    } else {
                        LineChartData data = (LineChartData)chartView.getChartData();
                        Line l = data.getLines().get(0);
                        l.setValues(values.getList());
                        data.getAxisXBottom().getValues().add(new AxisValue(i).setLabel(String.valueOf(i)));
                        chartView.setLineChartData(data);
                    }
                }
            }
            firstLoad = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //NO-OP
    }
}
