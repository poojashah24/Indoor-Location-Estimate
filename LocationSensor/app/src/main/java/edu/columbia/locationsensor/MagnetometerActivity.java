package edu.columbia.locationsensor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;


public class MagnetometerActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor magnetometerSensor;
    TextView textView;
    TextView xReadingView;
    TextView yReadingView;
    TextView zReadingView;

    private long lastUpdate;
    private boolean firstLoad = true;

    LineChartView chartView;
    RollingList<PointValue> xValues;
    RollingList<PointValue> yValues;
    RollingList<PointValue> zValues;
    Axis xaxis;
    int i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnetometer);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(magnetometerSensor == null)
            Toast.makeText(this, R.string.no_magnetic_sensor, Toast.LENGTH_SHORT).show();

        textView = (TextView) findViewById(R.id.magnetometer_text_view);
        textView.setTextSize(48);

        xReadingView = (TextView)findViewById(R.id.text_magnetometer_x);
        xReadingView.setTextSize(36);
        xReadingView.setTextColor(Color.BLUE);

        yReadingView = (TextView)findViewById(R.id.text_magnetometer_y);
        yReadingView.setTextSize(36);
        yReadingView.setTextColor(Color.RED);

        zReadingView = (TextView)findViewById(R.id.text_magnetometer_z);
        zReadingView.setTextSize(36);
        zReadingView.setTextColor(Color.GREEN);

        chartView = (LineChartView)findViewById(R.id.magnetometer_chart);
        xValues = new RollingList<PointValue>(10);
        yValues = new RollingList<PointValue>(10);
        zValues = new RollingList<PointValue>(10);
        lastUpdate = System.currentTimeMillis();
        i = 0;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(magnetometerSensor != null)
            sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(magnetometerSensor != null)
            sensorManager.unregisterListener(this, magnetometerSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Repaint here

        if(firstLoad || System.currentTimeMillis() - lastUpdate >= 1000) {
            float xVal = event.values[0];
            double yVal = event.values[1];
            double zVal = event.values[2];
            double timeInMillis = System.currentTimeMillis();
            //double pressure = xVal;

            String yValReading = null;
            String zValReading = null;

            if(yReadingView != null) {
                yValReading = String.format("%.2f", yVal);
                String msg = "y:" + yValReading + "rad/s";
                yReadingView.setText(msg);
            }
            if(zReadingView != null) {
                zValReading = String.format("%.2f", zVal);
                String msg = "z:" + zValReading + "rad/s";
                zReadingView.setText(msg);
            }
            if(xReadingView != null) {
                String xValReading = String.format("%.2f", xVal);
                String msg = "x:" + xValReading + "rad/s";
                xReadingView.setText(msg);
                lastUpdate = System.currentTimeMillis();

                if(chartView != null) {
                    PointValue xPointVal = new PointValue(i++, (float)xVal);
                    xPointVal.setLabel(xValReading);
                    xValues.add(xPointVal);

                    PointValue yPointVal = new PointValue(i, (float)yVal);
                    yPointVal.setLabel(yValReading);
                    yValues.add(yPointVal);

                    PointValue zPointVal = new PointValue(i, (float)zVal);
                    zPointVal.setLabel(zValReading);
                    zValues.add(zPointVal);

                    if(firstLoad) {
                        //xValues.add(xPointVal);
                        Line xline = new Line(xValues.getList()).setColor(Color.BLUE);//.setCubic(true);
                        xline.setHasLabels(true);

                        Line yline = new Line(yValues.getList()).setColor(Color.RED);//.setCubic(true);
                        yline.setHasLabels(true);

                        Line zline = new Line(zValues.getList()).setColor(Color.GREEN);//.setCubic(true);
                        zline.setHasLabels(true);

                        List<Line> lines = new ArrayList<Line>();
                        lines.add(xline);
                        lines.add(yline);
                        lines.add(zline);

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
                        yaxis.setName("Rotation(in rad/s)");
                        data.setAxisYLeft(yaxis);

                        chartView.setLineChartData(data);
                    } else {
                        LineChartData data = (LineChartData)chartView.getChartData();
                        Line xline = data.getLines().get(0);
                        xline.setValues(xValues.getList());

                        Line yline = data.getLines().get(1);
                        yline.setValues(yValues.getList());

                        Line zline = data.getLines().get(2);
                        zline.setValues(zValues.getList());

                        data.getAxisXBottom().getValues().add(new AxisValue(i).setLabel(String.valueOf(i)));
                        //data.setAxisXBottom(xaxis);
                        chartView.setLineChartData(data);
                    }
                }
            }
            firstLoad = false;
        }

        /*if(mCurrentSeries == null) {
            String seriesTitle = "Series " + (mDataset.getSeriesCount() + 1);
            // create a new series of data
            XYSeries series = new XYSeries(seriesTitle);
            mDataset.addSeries(series);
            mCurrentSeries = series;
            // create a new renderer for the new series
            XYSeriesRenderer renderer = new XYSeriesRenderer();
            mRenderer.addSeriesRenderer(renderer);
            // set some renderer properties
            renderer.setPointStyle(PointStyle.CIRCLE);
            renderer.setFillPoints(true);
            renderer.setDisplayChartValues(true);
            renderer.setDisplayChartValuesDistance(10);
            mCurrentRenderer = renderer;
            mChartView.repaint();
        }

        mCurrentSeries.add(timeInMillis, pressure);
        if(textView != null) {
            String msg = getResources().getString(R.string.text_pressure) + pressureInMilliBars;
            textView.setText(msg);
        }
        mChartView.repaint();*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //TODO : Do something here
    }
}
