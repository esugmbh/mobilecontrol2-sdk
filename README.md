# Mobile Control II SDK

The **Mobile Control II SDK** provides access to the throttle, keys and LEDs.

Current Version: 1.1.1

## Installation

1. Open the `build.gradle` file of your application. 
2. Add the SDK to the `dependencies` section:

```groovy
dependencies {
    ...
    compile 'eu.esu.mobilecontrol2:mobilecontrol2-sdk:1.1.1'
}
```

## Usage

You can check if your app is running on the Mobile Control II by calling

```java
boolean runsOnMc2 = MobileControl2.isMobileControl2();
```

The `ThrottleFragment`, `StopButtonFragment` as well as the `MobileControl2.setLedState()` methods are designed to do nothing when not running on the Mobile Control II, so you will not need to check this very often.
  
### Throttle

To access the throttle just add the `ThrottleFragment` to your activity:

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    ...
        
    mThrottleFragment = ThrottleFragment.newInstance(1);
    mThrottleFragment.setOnThrottleListener(mOnThrottleListener);
    getSupportFragmentManager().beginTransaction()
            .add(mThrottleFragment, "mc2:throttle")
            .commit();
    ...
}
```

Make sure you ignore the `ThrottleFragment.KEYCODE_THROTTLE_WAKEUP` KeyCode to prevent user input:

```java
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == ThrottleFragment.KEYCODE_THROTTLE_WAKEUP) {
        return true;
    }

    ...
}
```

To change the throttle position call `moveThrottle()` with a value between 0 (start) and 255 (end):

```java
// Move the throttle to the middle
int position = 127; 
mThrottleFragment.moveThrottle(position);
```
 
Use the `ThrottleFragment.OnThrottleListener` to receive callbacks:
```java
private ThrottleFragment.OnThrottleListener mOnThrottleListener = new OnThrottleListener() {
    @Override
    public void onButtonDown() {
        // The throttle button is in pressed state.
    }

    @Override
    public void onButtonUp() {
        // The throttle button is in released state.
    }

    @Override
    public void onPositionChanged(int position) {
        // The new throttle position from 0 to 255.
    }
}; 
```  
### ThrottleScale
The `ThrottleScale` class provides a convenient way to convert the throttle position to speed steps and vice versa. The example code shows how to use ThrotleScale for values from 0 - 28.
 
```java          
   ThrottleScale mThrottleScale = new ThrottleScale(10, 29);
   
   // Convert throttle position to speed step
   int step = mThrottleScale.positionToStep(position);
   // ... or step to position
   int position = mThrottleScale.stepToPosition(step);
```
The constructor takes two parameters:  
The first parameter defines the zero Range: If the throttle position is between 0 - 10 `positionToStep()` will return 0. The second parameter defines the actual number of steps including zero. `0 - 28` <=> `29` steps.

### Stop Button

You will also need to add the `StopButtonFragment` to your activity if you want to listen to Stop button events.

```java
@override
protected void onCreate(Bundle savedInstanceState) {
    ...
    StopButtonFragment stopButtonFragment = StopButtonFragment.newInstance();
    stopButtonFragment.setOnStopButtonListener(mStopButtonListener);
    
    getSupportFragmentManager().beginTransaction()
        .add(stopButtonFragment, "mc2:stopButton")
        .commit();
}
```

Use the `StopButtonFragment.OnStopButtonListener` to receive events.

```java
private StopButtonFragment.OnStopButtonListener mOnStopButtonListener  = new StopButtonFragment.OnStopButtonListener() {
        @Override
        public void onStopButtonDown() {
            // The stop button is pressed.
        }

        @Override
        public void onStopButtonUp() {
            // The stop button is released.
        }
    };
```

### LEDs

Turn a LED on:
```java
MobileControl2.setLedState(MobileControl2.LED_RED, true);
```

Turn a LED off:
```java   
MobileControl2.setLedState(MobileControl2.LED_GREEN, false);
```
LED flashing is also supported:
```java
MobileControl2.setLedState(MobileControl2.LED_RED, 250, 250);
```
Available LEDs are `MobileControl2.LED_GREEN` and `MobileControl2.LED_RED`.

### Side buttons

The side buttons of the Mobile Control II are using existing Android key codes. The `MobileControl2` class provides constants: 

`MobileControl2.KEYCODE_TOP_RIGHT`: Top right.<br>
`MobileControl2.KEYCODE_BOTTOM_RIGHT`: Bottom right.<br>
`MobileControl2.KEYCODE_TOP_LEFT`: Top left.<br>
`MobileControl2.KEYCODE_BOTTOM_LEFT`: Bottom left.

Make sure you check you are running on a Mobile Control II before using the key codes:
```java
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (MobileControl2.isMobileControl2()) {
        // Handle Mobile Control II keys
    }
    else {
       // Handle default keys.
    }
}  
```

## Sample App

Checkout the [Mobile Control II SDK sample app](https//github.com/esugmbh/mobilecontrol2-sdk-sample). 

## Javadoc

Javadoc is available at [http://esugmbh.github.io/mobilecontrol2-sdk]()

## License

Licensed under the MIT License
