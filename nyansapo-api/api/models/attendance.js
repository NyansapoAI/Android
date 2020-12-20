const mongoose = require('mongoose');

const attendanceShema = mongoose.Schema({
    _id: mongoose.Schema.Types.ObjectId,
    student_id: {type: mongoose.Schema.Types.ObjectId, ref: 'Student'},
    present : {type: mongoose.Schema.Types.Boolean, required: true}, // true or false
    timestamp: { type: Date, default: Date.now }, 
});

module.exports = mongoose.model('Attendance', attendanceShema);