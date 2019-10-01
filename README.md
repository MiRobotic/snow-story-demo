## Integrated AAR file instructions 2 ### Step 1 Import the aar file 3 ##### Add aar file to libs file
```gradle
implementation(name: 'csjsdk-beta', ext: 'aar')
```
##### Add the following code under the android{} structure of the app's build.gradle file
```gradle
repositories {
    flatDir {
        dirs 'libs'
    }
}
```
### 第二步 Introducing dependent libraries
```gradle
implementation 'io.netty:netty-all:4.1.23.Final'
```

### Step 3 Initialization 19 
##### Initializing the SDK in the Application
```java
CsjRobot.getInstance().init(this);
```
## Robot function instructions

### Registering a robot connection status event
```java
       CsjRobot.getInstance().registerConnectListener(new OnConnectListener() {
            @Override
            public void success() {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
            }

            @Override
            public void faild() {

            }

            @Override
            public void timeout() {

            }

            @Override
            public void disconnect() {

            }
        });
```

### Registering a robot wake event
```java
        CsjRobot.getInstance().registerWakeupListener(new OnWakeupListener() {
            @Override
            public void response(int i) {
                Log.d("TAG","registerWakeupListener:i:"+i);
                mCsjBot.getTts().startSpeaking("我在呢!",null);
                if (i > 0 && i < 360) {
                            if (i <= 180) {
                                CsjlogProxy.getInstance().debug("向左转:+" + i);
                                if (mCsjBot.getState().getChargeState() == State.NOT_CHARGING) {
                                    mCsjBot.getAction().moveAngle(i,null);
                                }
                            } else {
                                CsjlogProxy.getInstance().debug("向右转:-" + (360 - i));
                                if (mCsjBot.getState().getChargeState() == State.NOT_CHARGING) {
                                    mCsjBot.getAction().moveAngle(-(360 - i),null);
                                }
                            }
                }
            }
        });
```

### Registering a robot speech recognition event
```java
       // 语音识别
        CsjRobot.getInstance().registerSpeechListener(new OnSpeechListener() {
            @Override
            public void speechInfo(String s, int i) {

                // 简单解析示例
                Log.d("TAG","registerSpeechListener:s:"+s);
                if(Speech.SPEECH_RECOGNITION_RESULT == i){ // 识别到的信息
                    try {
                        String text = new JSONObject(s).getString("text");
                        Toast.makeText(MainActivity.this, "识别到的文本:"+text, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if(Speech.SPEECH_RECOGNITION_AND_ANSWER_RESULT == i){// 识别到的信息与的回答
                    try {
                        String say = new JSONObject(s).getJSONObject("result").getJSONObject("data").getString("say");
                        Toast.makeText(MainActivity.this, "获取的答案信息:"+say, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
```


### Registering robot real-time image transmission events
```java
        // Camera real-time image transmission
        CsjRobot.getInstance().registerCameraListener(new OnCameraListener() {
            @Override
            Public void response(Bitmap bitmap) {

            }
        });
```


### Registered robot integrated human detection event
```java
        // Comprehensive human detection
        CsjRobot.getInstance().registerDetectPersonListener(new OnDetectPersonListener() {
            @Override
            Public void response(int i) {
                If(i == 0){ // no one
                    Log.d("TAG","registerDetectPersonListener: no one");
                }else if(i == 1){ // Someone
                    Log.d("TAG","registerDetectPersonListener: someone");
                }
            }
        });
```

### Register face information event
```java
        // face information
        CsjRobot.getInstance().registerFaceListener(new OnFaceListener() {
            @Override
            Public void personInfo(String s) {
                // Member information recognized by the face (the face will continue to trigger)
                Log.d("TAG","registerFaceListener:s:"+s);
            }

            @Override
            Public void personNear(boolean b) {

                If(b){// people face close
                    Log.d("TAG","registerFaceListener: face close");
                }else{// face disappears
                    Log.d("TAG","registerFaceListener: face disappears");
                }
            }
        });
```

### Registered robot head sensor event (Xiaoxue)
```java
        // Robot head sensor (small snow)
        CsjRobot.getInstance().registerHeadTouchListener(new OnHeadTouchListener() {
            @Override
            Public void response() {
                Log.d("TAG","registerHeadTouchListener: Head Sensor Trigger");
            }
        });
```
### Robot action
```java
        // robot action
        Action action = CsjRobot.getInstance().getAction();

        // action reset
        Action.reset();

        // Limb movement (bodyPart: limb part, action: corresponding action)
        Action.action(2,6);// bow

        // Start swinging around (intervalTime: interval)
        action.startWaveHands(1000);

        // stop swinging left and right
        action.stopWaveHands();

        // start dancing
        action.startDance();

        // stop dancing
        action.stopDance();

        / / Get the current robot's location information
        action.getPosition(new OnPositionListener() {
            @Override
            Public void positionInfo(String s) {
                // return json
                /*{
                    "msg_id": "NAVI_GET_CURPOS_RSP",
                        "x": "0",
                    "y": "0",
                    "z": "0",
                    "rotation": "0",
                    "error_code": 0
                  }*/
            }
        });

        / / Move method (direction: direction 0: before 1: after 2: left 3: right)
        // shorter moving distance
        Action.move(0);

        // navigation method (the robot navigates to a point)
        String json = "";
        /*{
                    "x": 2,
                    "y": 1,
                    "z": 0,
                    "rotation": 30
                 }*/
        Action.navi(json, new OnNaviListener() {
            @Override
            Public void moveResult(String s) {
                // notification after arrival
            }

            @Override
            Public void messageSendResult(String s) {
                // Notification after the navigation message is successfully sent
            }

            @Override
            Public void cancelResult(String s) {

            }

            @Override
            Public void goHome() {

            }
        });

        // The robot cancels the current navigation
        action.cancelNavi(new OnNaviListener() {
            @Override
            Public void moveResult(String s) {

            }

            @Override
            Public void messageSendResult(String s) {

            }

            @Override
            Public void cancelResult(String s) {
                // Robot cancels navigation notification
            }

            @Override
            Public void goHome() {

            }
        });

        // Go to a specific angle ()
        action.goAngle(180);

        // Step angle (Rotation>0: turn left, Rotation<0: turn right)
        action.moveAngle(180, new OnGoRotationListener() {
            @Override
            Public void response(int i) {
                // arrival angle notification
            }
        });

      // Go back to charging
        action.goHome(new OnNaviListener() {
            @Override
            Public void moveResult(String s) {

            }

            @Override
            Public void messageSendResult(String s) {

            }

            @Override
            Public void cancelResult(String s) {

            }

            @Override
            Public void goHome() {
                // charging notification
            }
        });

        // save the map information of the current robot
        action.saveMap();

        / / Load the saved robot map information
        action.loadMap();

        // Robot speed setting (0.1-0.7, default 0.5)
        action.setSpeed(0.6f);

        // Navigation status query
        Action.search(new OnNaviSearchListener() {
            @Override
            Public void searchResult(String s) {
                /*{
                    "msg_id": "NAVI_GET_STATUS_RSP",
                     "state": 0,
                     "error_code": 0
                  }*/
                // state: 0 is idle, 1: is navigating
            }
        });
        
       // Nodding action
        action.nodAction();

        // Xiao Xue’s right arm swings
        action.snowRightArm();

        // Xiaoxue left arm swing
        action.snowLeftArm();

        // Small snow arms swinging
        action.snowDoubleArm();

        // Alice looks up
        action.AliceHeadUp();

        // Alice heads down
        action.AliceHeadDown();

        // Alice head reset
        action.AliceHeadHReset();

        // Alice lifts her left arm
        action.AliceLeftArmUp();

        // Alice puts her left arm down
        action.AliceLeftArmDown();

        // Alice lifts her right arm
        action.AliceRightArmUp();

        // Alice puts her right arm down
        action.AliceRightArmDown();

        // Xiaoxue left arm swing times
        action.SnowLeftArmSwing(20);

        // Xiaoxue right arm swing times
        action.SnowRightArmSwing(20);

        // Small snow arms swings
        action.SnowDoubleArmSwing(20);

        // turn left
        action.turnLeft(new OnGoRotationListener() {
            @Override
            Public void response(int i) {
                // complete the notification
            }
        });

        // turn right
        action.turnRight(new OnGoRotationListener() {
            @Override
            Public void response(int i) {
                // complete the notification
            }
        });

     // move to the left
        action.moveLeft();

        // move to the right
        action.moveRight();

        // go ahead
        action.moveForward();

        // back
        action.moveBack();
```

### TTS (speech synthesis)
```java
        // Robot TTS
        ISpeechSpeak speak = CsjRobot.getInstance().getTts();

        // start speaking
        speak.startSpeaking("Hello!", new OnSpeakListener() {
            @Override
            Public void onSpeakBegin() {
                // Before you speak
            }

            @Override
            Public void onCompleted(SpeechError speechError) {
                // speak is complete
            }
        });

        // Stop talking
        speak.stopSpeaking();

        // pause talking
        speak.pauseSpeaking();

        // Re-talk
        speak.resumeSpeaking();

        // Is it talking?
        speak.isSpeaking();

        // You can also use your own implementation of TTS (inherited from ISpeechSpeak interface)
        CsjRobot.getInstance().setTts(null);
```
### Speech Recognition
```java
        // robot voice
        Speech speech = CsjRobot.getInstance().getSpeech();

        // Turn on the SMS service (it is enabled by default, no need to operate)
        speech.startSpeechService();

        // Close the Xunfei voice service
        speech.closeSpeechService();

        // turn on multiple recognitions
        speech.startIsr();

        // turn off multiple recognition
        speech.stopIsr();

        // Turn on single recognition
        speech.startOnceIsr();

        / / Turn off single recognition
        speech.stopOnceIsr();

        // Manually wake up the robot
        speech.openMicro();

        // Manually get the answer to the question
        speech.getResult("What is your name?", new OnSpeechGetResultListener() {
            @Override
            Public void response(String s) {

            }
        });
```
### Face recognition information
```java
        // Robot face recognition
        Face face = CsjRobot.getInstance().getFace();

        // Turn on the camera (turn on video streaming by default)
        face.openVideo();

        // turn off the camera
        face.closeVideo();

        // Start face recognition service (default is enabled)
        face.startFaceService();

        // Close the face recognition service
        face.closeFaceService();


        // camera photo
        Face.snapshot(new OnSnapshotoListener() {
            @Override
            Public void response(String s) {
                /*
                * {
                    "error_code": 0,
                    "face_position": 0,
                    "msg_id":"FACE_SNAPSHOT_RESULT_RSP"
                    } */
                // erro_code : 0 means someone face, other means no face
            }
        });
        // Face registration (save the face of the current photo)
        face.saveFace("张三", new OnFaceSaveListener() {
            @Override
            Public void response(String s) {
                /*
                * {
                    "msg_id": "FACE_SAVE_RSP",
                    "person_id": "personx20170107161021mRJOVw",
                       "error_code": 0
                   }*/

                // error_cdoe : 0 success, 40002 face is registered, 40003 face name format is wrong
            }
        });

        // face information deletion
        face.faceDel("faceId");

        // Face information bulk deletion
        face.faceDelList("faceIdsJson");

        // Get all face databases
        face.getFaceDatabase(new OnGetAllFaceListener() {
            @Override
            Public void personList(String s) {
                // s
            }
        });
```
### Robot Status
```java
        // Robot status
        State state = CsjRobot.getInstance().getState();

        // The connection status of the robot
        state.isConnect();

        // The power value of the robot
        state.getElectricity();

        // The current state of charge of the robot
        state.getChargeState();

        // Robot shuts down
        State.shutdown();

        // Robot restart
        State.reboot();

        // Get robot battery information
        state.getBattery(new OnRobotStateListener() {
            @Override
            Public void getBattery(int i) {
                // returned power value
            }

            @Override
            Public void getCharge(int i) {

            }
        });

       / / Get the robot's state of charge
        state.getCharge(new OnRobotStateListener() {
            @Override
            Public void getBattery(int i) {

            }

            @Override
            Public void getCharge(int i) {
                // returned state of charge
            }
        });


        // Robot check information
        state.checkSelf(new OnWarningCheckSelfListener() {
            @Override
            Public void response(String s) {
                / / Return to the robot's inspection information
            }
        });

        / / Manually obtain comprehensive human detection information
        state.getPerson(new OnDetectPersonListener() {
            @Override
            Public void response(int i) {
                // returned status
                // i==0 no one i==1 someone
            }
        });
        
        // Get the SN information of the robot
        state.getSN(new OnSNListener() {
            @Override
            Public void response(String s) {
                // returned SN information
            }
        });
```
### Robot Emoticons
```java
        // robot expression
        Expression expression = CsjRobot.getInstance().getExpression();

        // Get the robot expression
        expression.getExpression(new OnExpressionListener() {
            @Override
            Public void response(int i) {
                // returned expression
            }
        });

        // happy
        Expression.happy();

        // sad
        Expression.sadness();

        // Surprised
        Expression.surprised();

        // smile
        Expression.smile();

        // normal
        Expression.normal();

        // angry
        Expression.angry();

        // lightning
        Expression.lightning();

        // sleepy
        Expression.sleepiness();
```
### Robot version
```java
        // Robot version information
        Version version = CsjRobot.getInstance().getVersion();

        / / Get robot version information
        version.getVersion(new OnGetVersionListener() {
            @Override
            Public void response(String s) {
                // returned version information
            }
        });

        // The underlying software check
        version.softwareCheck(new OnUpgradeListener() {
            @Override
            Public void checkRsp(int i) {
                If (i == 60002) {// is the latest version

                } else if (i == 60001) {//No version information is obtained, please check the network

                } else if (i == 0) {//Normal update

                }
            }

            @Override
            Public void upgradeRsp(int i) {

            }

            @Override
            Public void upgradeProgress(int i) {

            }
        });

       // Underlying software update
        version.softwareUpgrade(new OnUpgradeListener() {
            @Override
            Public void checkRsp(int i) {

            }

            @Override
            Public void upgradeRsp(int i) {

            }

            @Override
            Public void upgradeProgress(int i) {
                // update progress
            }
        });
```
### Other functions
```java
        // Other functions
        Extra extra = CsjRobot.getInstance().getExtra();

        // Get the list of hot words for the robot
        extra.getHotWords(new OnHotWordsListener() {
            @Override
            Public void hotWords(List<String> list) {
                // list of hot words returned
            }
        });

        / / Set the hot word function
        extra.setHotWords(null);

        // reset
        extra.resetRobot();
```
