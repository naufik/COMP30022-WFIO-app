Don't Lose Your Way

Team "We'll Figure It Out" presents... Don't Lose Your Way.

An application to help the elderly reach their destination.

## Team Information

**Team Name**: We'll Figure It Out

**Members**:

- @fdarma
- @naufik
- @syudanardi
- @staticDDQ
- @chill-X

## Known Issues

| Issue                                    | Comment                                  |
| ---------------------------------------- | ---------------------------------------- |
| After selecting a destination in Map View switching to AR View and switching back to Map View prompts you to re-select a destination in order to request for help. | This issue has to do with the way onResume() works in Android Studio. We are aware of the issue, however at the moment we assume that the elder is  requests for help immediately after selecting a destination or after the elder switches to AR View. |
| Pressing the call button on Maps View and switching to Message View does not change the call icon in Message View to the hang-up icon. | The button functionality still works, that is if you press the icon it still hangs-up the call. |
| When a Carer accepts an Elder's request for help, it might take a while for the Elder's location to show  on the Carer's side. | Tests vary, however a rough estimate concludes that the delay rarely happens. However, when it does it may take up to 30 seconds to show. |

## Guide

**Connecting to a Elder to a Carer**

1. The Elder must first navigate to settings by pressing the settings icon from the home page followed by pressing the `REGISTER NEW CARER`  button
2. Press `NEW CODE` to generate a new code
3. Once a code is generated, the Carer should then enter the code by first pressing on `CONNECT TO AN ELDER` on the home page followed by inputting the code in the text-field and pressing `ENTER`
4. Upon a successful connection, the app now switches to the My Elders Activity informing the Carer that the connection was successful

### Elder

Firstly, register for a new *Elder* account and log-in with the newly created account

**Navigating**

1. Press `NAVIGATE` in the home page
2. The *Unity* logo will appear for several seconds before the camera/map opens.
3. When the device is in a vertical orientation the camera will open prompting the user to select a destination. In order to select a destination, tilt the device into a horizontal position which in turn will open the Map Activity. Click the search icon in the toolbar and after typing in a destination, press enter on the device's in-built keyboard. This causes the map to re-orientate to accommodate for the newly generated route to the destination.
4. Auditory navigational instructions will be prompted upon the route creation and will continue to function for particular checkpoints within the route.
5. When the device is tilted to a vertical orientation, the camera will open and several assets will render, this includes:
   - An *Artificial Reality* arrow that points to the next checkpoint along the route
   - The distance (in meters) to the next checkpoint relative to the user's current position
6. When the user reaches a checkpoint, the arrow will then re-orientate itself to point to the next checkpoint along the route. 
7. Auditory navigational instructions will still function as soon as long as the device's orientation is horizontal (i.e. in the Map Activity).
8. To exit Navigation press the device's in-built back button anytime during navigation and to confirm the exit press `Yes!`

**Requesting for Help**

1. To request for help, either:
   - Press `HELP ME!` within the Map Activity, or
   - Press `SOS` within the AR Activity
2. As soon as a connected Carer accepts the request, the user will receive a notification specifying which Carer accepted his/her request and a notification with the action to accept the request.
3. Upon clicking the accept, the user is now able to call and see navigational instructions in the form of text messages from the Carer that accepted the request.

**Call**

1. Press the call icon located within the toolbar of the Maps Activity to call the particular connected Carer, an automated voice dialog will surface before the phone rings.
2. To Hang-up the Call press the call icon once more, which should now have changed to the hang-up icon once the call was initiated. 
3. Upon hang-up the hang-up icon changes once more to the call icon

**Text-Messaging**

1. To view messages press `VIEW MESSAGES` located on the bottom-left of the Map

2. When a Carer sends a text, the text appears within the activity

3. ###### Optional: Upon clicking the text, an automated voice will read the message and send it through the device's speakers for the user to listen to it.

**Favorites**

1. Before being able to save a location, enter a destination first
2. To save a location as a favorite, either:
   - Press the right-most icon on the toolbar and press `Add to Favorites`, or
   - Press the *star icon* on the AR Activity
3. To access the list of favorites press the right-most icon on the toolbar and press `Favorites`
4. A list of locations of particular destinations will now be shown, the user can now either:
   - Press the Map Icon within the favorite item, and Map Activity will open with a newly generated route from the user's current location to the favorite destination.
   - Press the Delete Icon within the favorite item, and an alert dialog re-confirming the deletion of that favorite location will appear. To delete the favorite location, choose `Yes!`, otherwise choose  `No!`

### Carer

**My Elders**

1. To view a list of connected Elders press `MY ELDERS` on the home page
2. A list of Elders will appear specifying the username and fullname of that particular Elder
3. To delete a connection with a particular Elder press the Delete Icon within the Elder item, and an alert dialog re-confirming the deletion of the connection with that particular elder will appear. To delete the connection, choose `Yes!`, otherwise choose  `No!`

**Accepting a Carer's Help**

1. Upon receiving a notification for an Elder's request for help, to accept the request press `accept` on the notification or press the notification itself. To decline the request, dismiss the notification, or press the notification and press `DELETE` on the newly generated activity.
2. Upon accepting the request, an activity will open with the Elder's location and the route to a destination shown within a map. 
3. The user is now able to call and/or text-message the elder to give custom navigational instructions 
4. To exit navigation press the device's in-built back button anytime during navigation and to confirm the exit press `Yes!`

**Text-Messaging**

1. To send a message to the connected Elder press the text-messaging icon on the toolbar
2. Type in the input field and press the arrow icon on the right to send the message, a dialog appears on a successfully sent message.

**Call**

1. Press the call icon located within the toolbar of the Maps Activity to call the particular connected Elder, or open text-messages followed by pressing the call icon on the toolbar of the text-messages activity. An automated voice dialog will surface before the phone rings.
2. To Hang-up the Call press the call icon once more, which should now have changed to the hang-up icon once the call was initiated. A Carer is able to hang-up from both the Text-Message Activity or the Map Activity
3. Upon hang-up the hang-up icon changes once more to the call icon

**[UNDER-DEVELOPMENT] Voice Clips**

1. To record a voice clip hold-press the `^` button on the bottom-left corner of the text-messaging activity.
2. To view recorded voice clips, press the top-right logo on the newly text-messaging activity toolbar
3. To play/stop the voice message click on the individual voice clip item.

## Unit Testing

Unfortunately there was not a lot of unit testing that is done for this project. Mainly due to the limitations of the Android testing framework that we are using.

`Mockito`, the framework taught in class are not able to do two crucial things: Mocking constructors (i.e. to test methods that create a new object) and the Android Studio test environment forces methods from external libraries to be mocked. Since many of our methods relies on creating and reading JSON objects, we cannot simulate them with Android's limitation on unit testing as the Android JUnit runner will break if we try to verify methods inside the JSON Object class.

The `test` package contains few Unit Tests for some utility modules.