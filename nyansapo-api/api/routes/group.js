const express = require('express');
const router = express.Router(); // initialize router
const mongoose = require('mongoose'); // import mongoose for database

// import data base models
const Group = require('../models/group');
const { restart } = require('nodemon');
const { json } = require('body-parser');

// GET all students in database 
router.get('/', (req, res, next) => {
    Group.find()
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

        // create student group object 
        var group = new Group({
            _id: new mongoose.Types.ObjectId(),
            name: req.body.name,
            instructor_id: req.body.instructor_id,
        });

        // push student ids into group object 
        var students_id = req.body.students_id; /// "id1,id2,id3"
        students_id = students_id.split(","); // ["id1","id2","id3"]
        for(i=0; i< students_id.length; i++){
            group.students_id.push(students_id[i]);
        }
    
        // save the group object into database
        group.save().then( result => {
            console.log("Saved to Database",result);
    
                // return a response 
            res.status(200).json({
                message: ' Assessment successfully saved',
                createdProduct : result
            });
        }).catch(err => {
            console.log(err);
            res.status(500).json({
                error: err
            })
        });
   
});

// GET a specific student by student ID
router.get('/:groupId', (req, res, next) =>{

        // get id from request 
        const id = req.params.groupId; 

        // query the database with model using the id
        Group.findById(id)
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

// PATCH: add student into group 
router.patch('/addstudent/:groupId', (req, res, next) =>{

});

// PATCH: remove student into group 
router.patch('/removestudent/:groupId', (req, res, next) =>{

});

// DELETE: remove a group 
router.delete('/:groupId', (req, res, next) =>{

    const id = req.params.groupId;
    Group.remove({_id: id})
    .exec()
    .then(result => {
        res.status(200).json({
            message: "Successfully deleted Group",
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