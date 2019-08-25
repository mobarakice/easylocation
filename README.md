## smartlocation
TO integrate smartlocation library, flow this simple steps.
## Step 1. Add the JitPack repository to your build file 
Add it in your root build.gradle

allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
## Step 2. Add the dependency
dependencies {
	        implementation 'com.github.mobarakice:smartlocation:1.0.1'
	}
  
 ## Step 3. Intialize SmartLocation in your activity
 public class MainActivity extends AppCompatActivity implements ILocationUpdatedListener {
    private SmartLocation smartLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onLocationUpdated(Location location) {
       // You can get location update here
    }

    @Override
    protected void onResume() {
        super.onResume();
        smartLocation = LocationManager.getContinuousLocationWithDefaultInterval(this,
                this::onLocationUpdated);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (smartLocation != null) {
            smartLocation.stop();
        }
    }
}

That's it!
