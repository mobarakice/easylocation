## EasyLocation
TO integrate easylocation library, flow this simple steps.
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
	        implementation 'com.github.mobarakice:easylocation:1.1.0'
	}
  
 ## Step 3. Intialize SmartLocation in your activity
 public class MainActivity extends AppCompatActivity implements ILocationUpdatedListener {
    private EasyLocation easyLocation;

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
        easyLocation = EasyLocationManager.getContinuousLocationWithDefaultInterval(this,
                this::onLocationUpdated);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (easyLocation != null) {
            easyLocation.stop();
        }
    }
}

That's it!
