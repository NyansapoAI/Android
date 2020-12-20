const mongoose = require('mongoose');

const assessmentShema = mongoose.Schema({
    _id: mongoose.Schema.Types.ObjectId,
    student_id : {type: mongoose.Schema.Types.ObjectId , ref: 'Student'}, // foreing_id of student
    timestamp: { type: Date, default: Date.now }, 
    learning_level: {type: String}, // final level of student after assessment
    assessment_key: {type: String}, // assessment key of the assessment made
    letters_correct: {type: String}, // letters correct during assessment
    letters_wrong: {type: String}, // letters wrong during assessment
    words_correct: {type: String}, // words correct during assessment
    words_wrong: {type: String}, // words wrong during assessment
    paragrahp_words_wrong: {type: String}, // words wrong during assessment
    story_ans_q1: {type: String}, // asnwer to question one
    story_ans_q2: {type: String} // answer to question one

});

module.exports = mongoose.model('Assessment', assessmentShema);