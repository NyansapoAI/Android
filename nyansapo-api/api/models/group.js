const mongoose = require('mongoose');

const groupShema = mongoose.Schema({
    _id: mongoose.Schema.Types.ObjectId,
    name: {type: String, required:true}, // group can be school or date of operation
    timestamp: { type: Date, default: Date.now }, // format YYYY-MM-DD hh:mm:ss
    instructor_id: {type: mongoose.Schema.Types.ObjectId , ref: 'Instructor'},
    students_id: [mongoose.Schema.Types.ObjectId] // [id1,id2]
});

module.exports = mongoose.model('Group', groupShema);