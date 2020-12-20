const mongoose = require('mongoose');

const instructorShema = mongoose.Schema({
    _id: mongoose.Schema.Types.ObjectId,
    firstname: {type: String, require:true},
    lastname: {type: String, require:true},
    email: {type: String, require:true},
    password: {type: String, require:true},
    timestamp: { type: Date, default: Date.now } 
});

module.exports = mongoose.model('Instructor', instructorShema);