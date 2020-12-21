const express = require('express');
const router = express.Router(); // initialize router
const mongoose = require('mongoose'); // import mongoose for database

// import data base models
const Assessment = require('../models/assessment');
const { restart } = require('nodemon');
const { json } = require('body-parser');

// GET all assessments
router.get('/', (req, res, next) => {
    Assessment.find()
    .exec()
    .then(docs => {
        console.log(docs);
        res.status(200).json(docs) // return all docs
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error: err
        });
    });

});


// POST: register an assessment into database
router.post('/', (req, res, next) => {

    // create assessment object 
    const assessment = new Assessment({
        _id: new mongoose.Types.ObjectId(),
        student_id : req.body.student_id,
        learning_level: req.body.learning_level,
        assessment_key: req.body.assessment_key,
        letters_correct: req.body.letters_correct,
        letters_wrong: req.body.letters_wrong,
        words_correct: req.body.words_correct,
        words_wrong: req.body.words_wrong,
        paragrahp_words_wrong: req.body.paragrahp_words_wrong,
        story_ans_q1: req.body.story_ans_q1,
        story_ans_q2: req.body.story_ans_q2

    });


    // save the assessment object into database
    assessment.save().then( result => {
        console.log("Saved to Database",result);

            // return a response 
        res.status(200).json({
            "id": assessment._id
        });
    }).catch(err => {
        console.log(err);
        res.status(500).json({
            error: err
        })
    });
});

// PATCH: update intructor info
router.patch('/', (req, res, next) =>{

    //const id = req.params.instructorId; // get the id

    const id = req.body.assessment_id; // get from body


    const updateOps = {};  // get all params that need to be updated
    for (const ops of req.body.updates){
        updateOps[ops.propName] = ops.value;  
    }

    Assessment.update({_id: id}, {$set: updateOps}) // update the params with request values
    .exec()
    .then(result => {
        console.log(result);
        res.status(200).json(result);
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            message:err
        })
    });
 
});

// GET a specific assessment info from database
router.get('/:assessmentId', (req, res, next) =>{

    // get ID from the url
    const id = req.params.assessmentId; 

    // query the database with model using the id
    Assessment.findById(id)
    .exec()
    .then(doc => {
        console.log("From Database",doc);
        if(doc){
            res.status(200).json(doc);
        }else{
            console.log(id);
            res.status(404).json({message:'Not a valid Id'})
        }
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({error: err})
    });

});

// Get all assessment of a specific student 
router.get('/student/:studentId', (req, res, next) =>{

    Assessment.find({student_id: req.params.studentId })
    .exec()
    .then(docs => {
        console.log(docs);
        res.status(200).json(docs) // return all docs
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error: err
        });
    });


});


// Get all assessment of a specific student 
router.get('/student', (req, res, next) =>{

    Assessment.find({student_id: req.body.student_id })
    .exec()
    .then(docs => {
        console.log(docs);
        res.status(200).json(docs) // return all docs
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error: err
        });
    });


});

// DELETE: remove an assessment info from database ( This might not be available to all users[Instructors])
router.delete('/:assessmentId', (req, res, next) =>{

    const id = req.params.assessmentId;
    Assessment.remove({_id: id})
    .exec()
    .then(result => {
        res.status(200).json({
            message: "Successfully deleted Assessment",
            instructor : result
        });
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error : err
        });
    });

});

module.exports = router;