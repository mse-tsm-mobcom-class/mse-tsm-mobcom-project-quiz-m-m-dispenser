# MSE TSM MobCom Project Quiz giving M&M as reward

## Agile Board
* [Project Board](https://github.com/mse-tsm-mobcom-class/mse-tsm-mobcom-project-quiz-m-m-dispenser/projects)

## Team
* [Nikolai Strässle](https://github.com/nikolaistraessle)
* [Steiner Pascal](https://github.com/SteinerPascal)
* [Martin Radinger](https://github.com/seros76)

## Docs
* [Slides](Docs/Slides.pdf) (PDF)
* [Video](Docs/MnMDispenserDemo.mov) (MOV)

## Code
* [Android](Android)
* [Arduino](Arduino)

## DevDocs

### Android App

#### Prerequisites
* Android smartphone with BLE, API Level 26+ 
* Java Version VERSION_1_8
* Gradle 

#### Building the app
* Android Studio > Build > Make Project

#### Running the app
* Connect smartphone via USB
* Android Studio > Run > Run 'app'

#### Description
The Android App consists of 3 activities. The main activity where the player can select the topic and number of questions he wants to answer, the quiz activity itself and the result activity where the quiz result is displayed with the leaderboard for the selected topic. There is also a login activity to connect the user to the firebase DB backend to load the questions, topic and save the user score.

After logging in or creating an account, the user selects the topic and questions for this quiz round. The quiz can only start, when the app has successfully connected to the Candy dispenser via BLE connection, otherwise a toast appears so the user needs a connection first. The topics are pulled from the Firebase DB.

Upon creation the Quiz Activity creates the number of questions for the chosen topic from the FirebaseDB. There is also a timelimit of 30 seconds for the user to give the correct answer.

When finishing the quiz the result activity shows the top5 players with their correct answers and the individual player result. It is possible to appear more than once in the leaderboard as it shows the top5 players overall. The player then has the option to play another round of the quiz or quit the game.

#### Feedback from candy dispenser
The candy dispenser gives feedback to the player.
- Filling level: there is a % showing the current filling level in the candy machine.
- Correct answer: when giving the correct answer the app changes the text that there is some dispensed candy in the machine.

#### UI
For the User Interface (UI) we tried to apply the principles from the [material.io](https://material.io/design) guidelines and chose the colour scheme accordingly. 
The images we used are all under free license.
- [Login Image](https://pixabay.com/de/photos/m-m-s-s%C3%BC%C3%9Figkeiten-lustig-spa%C3%9F-2629323/)
- [Quiz logo](https://pixabay.com/de/illustrations/quiz-fliesen-buchstaben-rot-spiel-2058883/)
- [Leaderboard logo](https://commons.wikimedia.org/wiki/File:Topeka-leaderboard_cropped.png)
- [Icon](https://pixabay.com/de/illustrations/quiz-icon-test-q-u-ich-z-rot-2058888/)

#### Screenshots
![Bild screenshot_20201207-16ujzq.jpg auf abload.de](https://abload.de/img/screenshot_20201207-16ujzq.jpg)
![Bild screenshot_20201207-1fsjbv.jpg auf abload.de](https://abload.de/img/screenshot_20201207-1fsjbv.jpg)
![Bild screenshot_20201207-1jpjww.jpg auf abload.de](https://abload.de/img/screenshot_20201207-1jpjww.jpg)
![Bild screenshot_20201207-1s1kgr.jpg auf abload.de](https://abload.de/img/screenshot_20201207-1s1kgr.jpg)

#### Video of prototype
You can see the prototype in action in our project video [here](Docs/MnMDispenserDemo.mov).

#### Testing
Testing is done with
- Espresso
- powermock
- robolectric
- AndroidJUnitRunner

#### Sonarqube
* install [docker](https://docs.docker.com/get-docker/)
* install [docker-compose](https://docs.docker.com/compose/install/)
* start an AVD (and disconnect your android device if it is connected to your computer)
* start sonarqube over docker-compose with the following command in your android project root: `docker-compose up -d`
* run the command in your android project root: `./gradlew  clean sonarqube`
* go to [localhost:9000](http://localhost:9000) and check the project (username/password: admin, projectname: Quiz)


#### UML Class Diagram
[![Class Diagram](https://abload.de/thumb/classdiagram1tpjon.png)](https://abload.de/image.php?img=classdiagram1tpjon.png)


### Sensor and Actuator

The visualisation of the sensor data and acutator state is made in the question view. In the bottom on the left side the filling level of the dispenser is displayed. The filling level is shown in percentage (calculation made by distance and container size). In the bottom on the middle the state of the actuator (servo) is displayed. If the servo is waiting the text "nothing to take" is displayed otherwise the text "grab your m&m" is shown.

Another visualisation of the sensor state is our anti-theft system. If the lid of the dispenser will be opened and the filling level before is higher than 20% a toast with the text "Attention Content is stolen" is displayed. The color of the text of this toast is red.

### Firebase (Backend)

As a backend we used Firebase Real time data to syncronize the App with the necessary data and Firebase Auth to authenticate users and store there scores. In order to make this project work you have to create a firebase account and open a project. After that you will need to import the datastructure to the Firebase RealTime Database. Note: Don't forget to include the firebase json with the api key in your project structure on the applevel.

#### Firebase Auth

There is a very loose user authentication which is not thought as a security measurement but as a way to authenticate and enable a score board among the users. It is required once in the beginning and won't be necessary anymore afterwards. Except the app is reinstalled. 

#### Firebase RealTime Database

The Firebase realtime Database is used to store questions acording to different topics. A user can choose a topic in the beginning of a question round and the app will query the Realtime Database for a list of questions.

Datastructure for the questions:
```
{
  "topics" : {
    "geography" : {
      "questions" : [ null, {
        "answer1" : "Lucerne",
        "answer2" : "Bern",
        "answer3" : "Zurich",
        "answer4" : "Geneva",
        "correctAnswer" : 2,
        "question" : "What is the capital of lucerne"
      }, {
        "answer1" : "Jura",
        "answer2" : "Westschweiz",
        "answer3" : "Tessin",
        "answer4" : "Ostschweiz",
        "correctAnswer" : 3,
        "question" : "What is the southern part called of Switzerland?"
      } ]
    },
    "sports" : {
      "questions" : [ null, {
        "answer1" : "Switzerland",
        "answer2" : "Germany",
        "answer3" : "Argentina",
        "answer4" : "Brazil",
        "correctAnswer" : 4,
        "question" : "Who won the soccer World Cup in 1994?"
      } ]
    }
  }
}

```

We also save the leaderboard with the highscores of all users for a specific topic.

Datastructure for the leaderboard:
```
  "Leaders_geography" : [ {
    "userName" : "peter@outlook.com",
    "userScore" : 5
  }, {
    "userName" : "dieter@outlook.com",
    "userScore" : 3
  }, {
    "userName" : "hans@gmail.com",
    "userScore" : 2
  }, {
    "userName" : "klaus@bluewin.ch",
    "userScore" : 1
  }, {
    "userName" : "jessica@web.de",
    "userScore" : 0
  }
 ]
```


#### Firease Testing 
Testing of the firebase is done with the @firebase/rules-unit-testing library and locally installed emulators. To install the emulator first install the [firebase cli](https://firebase.google.com/docs/cli#install_the_firebase_cli). It is necessary to login into the gmail account and connect your project to it. Furthermore, to run the test on a physical device you have to be in the same wifi with both devices(lapto & Mobile phone). Then add in the local ip of you laptop.

# M&M Dispenser Arduino Program

## Prerequisites
* [Feather nRF52840 Sense](https://github.com/tamberg/mse-tsm-mobcom/wiki/Feather-nRF52840-Sense) device
* Grove - [Servo](https://www.distrelec.ch/en/grove-servo-seeed-studio-316010005/p/30069966?channel=b2c&price_gs=12.924&source=googleps&ext_cid=shgooaqchen-blcss&kw=%7Bkeyword%7D&gclid=Cj0KCQiA8dH-BRD_ARIsAC24umZxAaDpbZoTRaQGgg7EWPJz9kLlttsf8TR-gWFbqXpstKS-69iycZEaAsiBEALw_wcB) on pin 6
* Adafruit_VL53L0X on pin D2 
* Tof sensor (e.g: [VL53L0X](https://www.adafruit.com/product/3317))

## Adding libraries
* Arduino IDE > Sketch > Include Library > Manage Libraries ...
* Search for "Adafruit_VL53L0X" > Install

## Uploading the program
* Connect the Arduino board via USB
* Arduino IDE > Tools > Board: Adafruit Feather Bluefruit Sense
* Arduino IDE > Tools > Port: (Adafruit Feather Bluefruit Sense)
* Arduino IDE > Upload

### Characteristics
Our arduino device uses a custom GATT service and 3 custom GATT characteristics for connecting the candy machine via BLE to our quiz app.
- BLECharacteristic mmDispenserStateCharacteristic => notify the state of the servo (running or waiting)
properties are read and notify
- BLECharacteristic mmDispenserFillingLevelCharacteristic => notify the filling level by distance in mm
properties are read and notify
- BLECharacteristic mmDispenserDispenseCharacteristic => starts the servo from remote
properties are read and write

### Code
#### Setup void setup() {...}
- void setupVL53L0X() {...} connect the Time-of-flight VL53L0X sensor
- attach myServo
- void setupDispenserService() {...} start the service with three characteristics 
DispenserFillingLevelCharacteristic and DispenserStateCharacteristic set void cccdCallback(...) {...} for enabling notifications
DispenseCharacteristic sets void writeCallback(...) {...}
- startAdvertising to connect arduino board with android app

#### void loop() {...}
notify the measurement from the TOF sensore and current dispenserState with void notifyDispenserState() {...} and void notifyProximity() {...}

#### DispenseCharacteristic
void writeCallback(...) {...} 
set dispenser state and call void dispense() {...} to write to myServo to start turning and write again to stop rotation after a short time delay. After dispensing the candy the dispenserState is set to false again.

#### DispenserFillingLevelCharacteristic
readProximity() {...}
We use the Time-of-Flight VL53L0X Sensor to measure the distance in the candy dispenser. Range is measured in mm. To avoid measurement inaccuracies, the average of the last 5 measurements is used. If current distance is out of range for the sensor, the current distance is set to 2000mm (which is much higher than the size of the container). This indicates, that the lid is open.

# Dispenser construction
The Dispenser got constructed with some modifications from [this](https://www.buildsomething.com/plans/PA57A1F77F2D3B445/Desktop-Candy-Dispenser) site.
