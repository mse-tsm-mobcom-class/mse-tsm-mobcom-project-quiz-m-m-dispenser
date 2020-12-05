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

The Android App consists of 3 activities. The main activity where the player can select the topic and number of questions he wants to answer, the quiz activity itself and the result activity where the quiz result is displayed with the leaderboard for the selected topic.

After logging in or creating an account, the user selects the topic and questions for this quiz round. The quiz can only start, when the app has successfully connected to the Candy dispenser via BLE connection, otherwise a toast appears so the user needs a connection first. The topics are pulled from the Firebase DB.

Upon creation the Quiz Activity creates the number of questions for the chosen topic from the FirebaseDB. There is also a timelimit of 30 seconds for the user to give the correct answer.

When finishing the quiz the result activity shows the top5 players with their correct answers and the individual player result. It is possible to appear more than once in the leaderboard as it shows the top5 players overall. The player then has the option to play another round of the quiz or quit the game.


#### Feedback from candy dispenser
The candy dispenser gives feedback to the player.
- Filling level: there is a % showing the current filling level in the candy machine.
- Correct answer: when giving the correct answer the app changes the text that there is some dispensed candy in the machine.
- Theft attempt: when someone tries to open the candy storage a toast appears in the app warning the user that someone is trying to steal some sweets.

#### UI
For the User Interface (UI) we tried to apply the principles from the [material.io](https://material.io/design) guidelines and chose the colour scheme accordingly. 
The images we used are all under free license.
- [Quiz logo](https://pixabay.com/de/illustrations/quiz-fliesen-buchstaben-rot-spiel-2058883/)
- [Leaderboard logo](https://commons.wikimedia.org/wiki/File:Topeka-leaderboard_cropped.png)
- [Icon](https://pixabay.com/de/illustrations/quiz-icon-test-q-u-ich-z-rot-2058888/)

#### Screenshots
![Bild screenshot_20201204-mwk5t.jpeg auf abload.de](https://abload.de/img/screenshot_20201204-mwk5t.jpeg)
![Bild screenshot_20201204-fpkur.jpeg auf abload.de](https://abload.de/img/screenshot_20201204-fpkur.jpeg)
![Bild screenshot_20201204-qukmb.jpeg auf abload.de](https://abload.de/img/screenshot_20201204-qukmb.jpeg)

#### Video of prototype
You can see the prototype in action in our project video [here](Docs/MnMDispenserDemo.mov).

#### Testing
Testing is done with the Espresso framework.

#### UML Class Diagram
[![Class Diagram](https://abload.de/thumb/classdiagram1tpjon.png)](https://abload.de/image.php?img=classdiagram1tpjon.png)


### Firebase (Backend)

As a backend we used Firebase Real time data to syncronize the App with the necessary data and Firebase Auth to authenticate users and store there scores.

#### Firebase Auth

There is a very loose user authentication which is not thought as a security measurement but as a way to authenticate and enable a score board among the users. It is required once in the beginning and won't be necessary anymore afterwards. Except the app is reinstalled. 

#### Firebase RealTime Database

The Firebase realtime Database is used to store questions acording to different topics. A user can choose a topic in the beginning of a question round and the app will query the Realtime Database for a list of questions.

Datastructure:
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

#### Firease Testing 
Testing of the firebase is done with the @firebase/rules-unit-testing library and locally installed emulators. To install the emulator first install the [firebase cli](https://firebase.google.com/docs/cli#install_the_firebase_cli). It is necessary to login into the gmail account and connect your project to it. Furthermore, to run the test on a physical device you have to be in the same wifi with both devices(lapto & Mobile phone). Then add in the local ip of you laptop

