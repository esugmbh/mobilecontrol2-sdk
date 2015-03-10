# Mobile Control II SDK

The **Mobile Control II SDK** provides access to the throttle, keys and LEDs.

Current Version: 1.0

## Installation

1. Open the `build.gradle` file of your application. 
2. Add the SDK to the `dependencies` section:

```groovy
dependencies {
    ...
    compile 'eu.esu.mobilecontrol2:mobilecontrol2-sdk:1.0'
}
```

## Usage

You can check if your app is running on the Mobile Control II by calling

```java
boolean runsOnMc2 = MobileControl2.isMobileControl2();
```

The `ThrottleFragment` class and the `MobileControl2.setLedState()` methods are designed to do nothing when not running on the Mobile Control II, so you will not need to check this very often. 

### Throttle

Add the `ThrottleFragment` to your activitiy:

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

Move the throttle:
```java
mThrottleFragment.moveThrottle(position);
```
Use a `OnThrottleListener` to receive callbacks:
```java
private OnThrottleListener mOnThrottleListener = new OnThrottleListener() {
    @Override
    public void onButtonDown() {
        // The throttle button is in pressed state
    }

    @Override
    public void onButtonUp() {
        // The throttle button is in released state
    }

    @Override
    public void onPositionChanged(int position) {
        // The new throttle position
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

### Buttons

The Mobile Control II uses existing Android key codes. The `MobileControl2` class provides constants:

`MobileControl2.KEYCODE_STOP`: The stop button.<br>
`MobileControl2.KEYCODE_TOP_RIGHT`: Top right.<br>
`MobileControl2.KEYCODE_BOTTOM_RIGHT`: Bottom right.<br>
`MobileControl2.KEYCODE_TOP_LEFT`: Top left.<br>
`MobileControl2.KEYCODE_BOTTOM_LEFT`: Bottom left.

Make sure you check you are running on a Mobile Control II for using the key codes:
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

Checkout the <a href="https://github.com/esugmbh/mobilecontrol2-sdk-sample">Mobile Control II SDK Sample application</a>.

## Javadoc

Javadoc is available at <a href="http://esugmbh.github.io/mobilecontrol2-sdk/">http://esugmbh.github.io/mobilecontrol2-sdk/</a>

## License

Licensed under the MIT License
