const express = require('express');
const router = express.Router(); // initialize router
const mongoose = require('mongoose'); // import mongoose for database

// import data base models
const Attendance = require('../models/attendance');
const { restart } = require('nodemon');
const { json } = require('body-parser');

// GET all attendance in database 
router.get('/', (req, res, next) => {
    Attendance.find()
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

// POST: register an student
router.post('/', (req, res, next) => {
   /*
    receives a json of all students whose attendace is being recorded 
    eg of a body
    {      id      : Present 
        students_id: "id1,id2,id3",
        attendance : "T,T,F"
    }
   */

    // get all id and attendance from request
    var students_id = req.body.students_id;
    students_id = students_id.split(",");
    var attendace = req.body.attendace;
    attendace = attendace.split(",");

    // create and save Attendance object for each student id
    for(i=0; i < students_id.length; i++){
        // create Attendace entry for each student id
        var st_attendace = new Attendance({
            _id: new mongoose.Types.ObjectId(),
            student_id: students_id[i],
            attendace: attendace[i] 
        });

        // save each Attendance entry
        st_attendace.save().then( result => {
            console.log(result);
        }).catch(err => { // return error response if fail
            console.log(err);
            res.status(500).json({
                error: err
            })
        });
    }

    // return a success response
    res.status(200).json({
        message: "Attendance was succesfuly recorded"
    })
});

// GET all attendace from a specific student
router.get('/:studentId', (req, res, next) =>{

});

// PATCH: update student info
router.patch('/:productId', (req, res, next) =>{

});

// DELETE: remove a student 
router.delete('/:productId', (req, res, next) =>{

});

module.exports = router;