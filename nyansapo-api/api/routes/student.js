const express = require('express');
const router = express.Router(); // initialize router
const mongoose = require('mongoose'); // import mongoose for database

// import data base models
const Student = require('../models/student');
const { restart } = require('nodemon');
const { json } = require('body-parser');
const checkAuth = require('../middleware/check-auth');
const { update } = require('../models/student');

// GET all students in database 
router.get('/', (req, res, next) => {
    Student.find()
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
router.post('/register',  (req, res, next) => {

    // create an student object with the instructor model and request info
    const student = new Student({
        _id: new mongoose.Types.ObjectId(),
        instructor_id: req.body.instructor_id, 
        firstname: req.body.firstname,
        lastname: req.body.lastname,
        age: req.body.age,
        gender: req.body.gender,
        notes: req.body.notes,
        learning_level: req.body.learning_level,
        std_class : req.body.std_class
    }); 

    // save the instructor object into database
    student.save().then( result => {
        console.log("Saved to Database",result);

            // return a response 
        res.status(200).json({
            "id": student._id
        });
    }).catch(err => {
        console.log(err);
        res.status(500).json({
            error: err
        })
    });
   
});

// GET all students of an instructor 
router.post('/ofInstructor', (req, res, next) =>{
    Student.find({instructor_id: req.body.instructor_id })
    .exec()
    .then(docs => {
        console.log(docs);
        res.status(200).json({
            data : docs
        }); // return all docs
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error: err
        });
    });

});

// GET all students of an instructor 
router.get('/ofInstructor', (req, res, next) =>{
    Student.find({instructor_id: req.body.instructor_id })
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

// GET a specific student by student ID
router.get('/:studentId', checkAuth, (req, res, next) =>{
    
    // get id from request 
    const id = req.params.studentId; 

    // query the database with model using the id
    Student.findById(id)
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


// PATCH: update intructor info
//router.patch('/update', checkAuth, (req, res, next) =>{
router.post('/update', (req, res, next) =>{

    const id = req.body.student_id; // get the id

    const updateOps = {};  // get all params that need to be updated
    updateOps["firstname"] = req.body.firstname;
    updateOps["lastname"] = req.body.lastname;
    updateOps["age"] = req.body.age;
    updateOps["gender"] = req.body.gender;
    updateOps["notes"] = req.body.notes;
    updateOps["std_class"] = req.body.std_class;

    console.log(updateOps);

    /*
    for (const ops of req.body.updates){
        if (ops.propName === "password"){ // if password needs update hash it
            hashpassword = passwordHash.generate(ops.value); // hash password
            updateOps[ops.propName] = hashpassword;
        }else{
            updateOps[ops.propName] = ops.value; 
        }
    }*/

    Student.update({_id: id}, {$set: updateOps}) // update the params with request values
    .exec()
    .then(result => {
        console.log(result);
        res.status(200).json(result);
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error:err
        })
    });

});


// PATCH: update student info
router.patch('/learning_level', (req, res, next) =>{

    const id = req.body.student_id; // get from body
    const learning_level = req.body.learning_level;

    Student.update({_id: id}, {learning_level: learning_level}) // update the params with request values
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

router.patch('/firstname', (req, res, next) =>{

    const id = req.body.student_id; // get from body
    const learning_level = req.body.firstname;

    Student.update({_id: id}, {firstname: firstname}) // update the params with request values
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

router.patch('/lastname', (req, res, next) =>{

    const id = req.body.student_id; // get from body
    const lastname = req.body.lastname;

    Student.update({_id: id}, {lastname: lastname}) // update the params with request values
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

router.patch('/age', (req, res, next) =>{

    const id = req.body.student_id; // get from body
    const age = req.body.age;

    Student.update({_id: id}, {age: age}) // update the params with request values
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

router.patch('/gender', (req, res, next) =>{

    const id = req.body.student_id; // get from body
    const gender = req.body.gender;

    Student.update({_id: id}, {gender: gender}) // update the params with request values
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

router.patch('/notes', (req, res, next) =>{

    const id = req.body.student_id; // get from body
    const notes = req.body.notes;

    Student.update({_id: id}, {notes: notes}) // update the params with request values
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

router.patch('/std_class', (req, res, next) =>{

    const id = req.body.student_id; // get from body
    const std_class = req.body.std_class;

    Student.update({_id: id}, {std_class: std_class}) // update the params with request values
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


// DELETE: remove a student 
/*
router.delete('/', checkAuth, (req, res, next) =>{

    const id = req.body.student_id; // get id from params

    Student.remove({_id: id})
    .exec()
    .then(result => {
        res.status(200).json({
            message: "Successfully deleted Student",
            student : result
        });
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error : err
        });
    });
});
*/

router.delete('/', (req, res, next) =>{

    const id = req.body.student_id; // get id from params

    Student.remove({_id: id})
    .exec()
    .then(result => {
        res.status(200).json({
            message: "Successfully deleted Student",
            student : result
        });
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error : err
        });
    });
});

router.post('/delete', (req, res, next) =>{

    const id = req.body.student_id; // get id from params

    Student.remove({_id: id})
    .exec()
    .then(result => {
        res.status(200).json({
            message: "Successfully deleted Student",
            student : result
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


/*
const express = require('express');
const router = express.Router(); // initialize router
const mongoose = require('mongoose'); // import mongoose for database

// import data base models
const Student = require('../models/student');
const { restart } = require('nodemon');
const { json } = require('body-parser');
const checkAuth = require('../middleware/check-auth');

// GET all students in database 
router.get('/', checkAuth, (req, res, next) => {
    Student.find()
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
router.post('/signup', checkAuth, (req, res, next) => {

    // check to see if email is already in database
    Student.find({email: req.body.email })
    .exec()
    .then(user => {
        if(user.length >=1 ){ // if there is a user with email
            return res.status(422).json({
                message: "Email exist"
            });
        }
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error: err
        });
    });
    
    
    // hash password
    hashpassword = passwordHash.generate(req.body.password); 

    // create an student object with the student model and request info
    const student = new Student({
        _id: new mongoose.Types.ObjectId(),
        firstname: req.body.firstname,
        lastname: req.body.lastname,
        email: req.body.email,
        password: hashpassword
    }); 

    // save the student object into database
    student.save().then( result => {
        console.log("Saved to Database",result);

         // return a response 
        res.status(200).json({
            message: ' Student successfully saved',
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
router.get('instructorid/:studentId', checkAuth, (req, res, next) =>{
    Student.find({instructor_id: req.params.instructorId })
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

// GET a specific student by student ID
router.get('/:studentId', checkAuth, (req, res, next) =>{
    
    // get id from request 
    const id = req.params.studentId; 

    // query the database with model using the id
    Student.findById(id)
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

// PATCH: update student info
router.patch('/:studentId', checkAuth, (req, res, next) =>{
    const id = req.params.StudentId; // get the id

    const updateOps = {};  // get all params that need to be updated
    for (const ops of req.body.updates){
        if (ops.propName === "password"){ // if password needs update hash it
            hashpassword = passwordHash.generate(ops.value); // hash password
            updateOps[ops.propName] = hashpassword;
        }else{
            updateOps[ops.propName] = ops.value; 
        }
    }

    Student.update({_id: id}, {$set: updateOps}) // update the params with request values
    .exec()
    .then(result => {
        console.log(result);
        res.status(200).json(result);
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error:err
        })
    });
});

// DELETE: remove a student 
router.delete('/:studentId', checkAuth, (req, res, next) =>{
    const id = req.params.studentId;
    Student.remove({_id: id})
    .exec()
    .then(result => {
        res.status(200).json({
            message: "Successfully deleted Student",
            student : result
        });
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error : err
        });
    });
});
*/
module.exports = router;