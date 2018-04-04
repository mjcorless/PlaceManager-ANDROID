package mjcorless.bsse.asu.edu.placemanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import mjcorless.bsse.asu.edu.placemanager.models.PlaceDescription;

public class PlaceListActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        Bundle bundle = getIntent().getExtras();
        PlaceDescription placeDescription = (PlaceDescription)bundle.getSerializable("placeDescription");
    }
}
