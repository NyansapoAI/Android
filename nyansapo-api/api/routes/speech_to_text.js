const sdk = require("microsoft-cognitiveservices-speech-sdk");
const fs = require("fs");
const express = require('express');
const router = express.Router(); // initialize router
const mongoose = require('mongoose'); // import mongoose for database
const multer = require('multer');
const { RawWebsocketMessage } = require("microsoft-cognitiveservices-speech-sdk/distrib/lib/src/common/Exports");
const { AudioInputStream } = require("microsoft-cognitiveservices-speech-sdk");


var storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, 'uploads/')
  },
  filename: function (req, file, cb) {
    cb(null, file.originalname) //Appending .jpg
  }
})

const upload = multer({storage : storage});

   // GET all students in database 
router.get('/',(req, res, next) => {

});

// POST: register an student
router.post('/', upload.single('audio'),(req, res, next) => {

/*  console.log(req.file);
  res.status(200).json({
    message: "saved"
  }); */
  
  try{
      const subscriptionKey = "1c58abdab5d74d5fa41ec8b0b4a62367";
      const serviceRegion = "eastus"; 
      const endpoint = "275310be-2c21-4131-9609-22733b4e0c04";
      var filename = "uploads/"+req.file.originalname; // 16000 Hz, Mono
      //var filename = "uploads/Paragraph_013_3.wav";
      // create the push stream we need for the speech sdk.
      var pushStream = sdk.AudioInputStream.createPushStream();
        
      // open the file and push it to the push stream.
      try {
          fs.createReadStream(filename).on('data', function(arrayBuffer) {
              pushStream.write(arrayBuffer.slice());
            }).on('end', function() {
              pushStream.close();
            });
      } catch (error) {
          console.log(error);
      }


      var speechConfig = sdk.SpeechConfig.fromSubscription(subscriptionKey, serviceRegion);
      
      // setting the recognition language to English.
      speechConfig.speechRecognitionLanguage = "en-US";
      // set config to nyansapo endpoint
      speechConfig.endpointId = endpoint;

      // create audio config from audio
      var audioConfig = sdk.AudioConfig.fromStreamInput(pushStream);

      // create the speech recognizer.
      var recognizer = new sdk.SpeechRecognizer(speechConfig, audioConfig);

      console.log("Now recognizing from: " + filename);
        
      // start the recognizer and wait for a result.
      /*
      recognizer.recognizeOnceAsync(
        function (result) {
          console.log(result);
          recognizer.close();
          recognizer = undefined;

          res.status(200).json(result.privText); // return response
        },
        function (err) {
          console.log("cant")
          console.trace("err - " + err);
      
          recognizer.close();
          recognizer = undefined;
        }); */



      recognizer.recognizing = (s, e) => {
          console.log(`RECOGNIZING: Text=${e.result.text}`);
      };
      

      recognizer.recognized = (s, e) => {
          if (e.result.reason == ResultReason.RecognizedSpeech) {
              console.log(`RECOGNIZED: Text=${e.result.text}`);
              res.status(200).json(e.result.text); 
          }
          else if (e.result.reason == ResultReason.NoMatch) {
              console.log("NOMATCH: Speech could not be recognized.");
          }
      };
      
      recognizer.canceled = (s, e) => {
          console.log(`CANCELED: Reason=${e.reason}`);
      
          if (e.reason == CancellationReason.Error) {
              console.log(`"CANCELED: ErrorCode=${e.errorCode}`);
              console.log(`"CANCELED: ErrorDetails=${e.errorDetails}`);
              console.log("CANCELED: Did you update the subscription info?");
          }
          res.status(200).json(error);
          recognizer.stopContinuousRecognitionAsync();
      };
      
      recognizer.sessionStopped = (s, e) => {
          console.log("\n    Session stopped event.");
          recognizer.stopContinuousRecognitionAsync();
          res.status(500).json(e);
      };

    } catch (error) {
        res.status(500).json(error);
    }

    recognizer.startContinuousRecognitionAsync();

   
});

// GET a specific student by student ID
router.get('/:groupId', (req, res, next) =>{

});

// PATCH: update student info
router.patch('/:groupId', (req, res, next) =>{

});

// DELETE: remove a student 
router.delete('/:groupId', (req, res, next) =>{

});

module.exports = router;