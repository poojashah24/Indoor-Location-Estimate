package edu.columbia.locationsensor;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

/**
 * Test class to check interaction with the in-memory SQLite database.
 */
public class TestDBActivity extends ActionBarActivity {

    private PressureDataSource pressureDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_db);

        pressureDataSource = new PressureDataSource(this);
        pressureDataSource.open();

        List<PressureReading> readings = pressureDataSource.getAllPressureReadings();
    }

    @Override
    protected void onStart() {
        super.onStart();
        pressureDataSource.insertPressureReading(1000.12, 123456789);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_test_db, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
