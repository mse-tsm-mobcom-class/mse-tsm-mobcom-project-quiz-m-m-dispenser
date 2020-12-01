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

