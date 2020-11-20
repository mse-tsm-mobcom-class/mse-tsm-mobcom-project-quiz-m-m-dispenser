# MSE TSM MobCom Project Quiz giving M&M as reward

## Agile Board
* https://trello.com/b/6AdpjjWI/mm

## Team
* [Nikolai Strässle](https://github.com/nikolaistraessle)
* [Steiner Pascal](https://github.com/SteinerPascal)
* [Martin Radinger](https://github.com/seros76)

## Docs
* [Slides](Docs/Slides.pdf) (PDF)
* [Video](Docs/Video.mp4) (MP4)

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
	"topics": {
		  "geography" : {
			"questions" : [ null, {
			  "1" : "Lucerne",
			  "2" : "Bern",
			  "3" : "Zurich",
			  "4" : "Geneva",
			  "correctAnswer" : 2,
			  "question" : "What is the capital of lucerne"
			}, {
			  "1" : "Jura",
			  "2" : "Westschweiz",
			  "3" : "Tessin",
			  "4" : "Ostschweiz",
			  "correctAnswer" : 3,
			  "question" : "What is the southern part called of Switzerland?"
			} ]
		  }
	}
}
```

