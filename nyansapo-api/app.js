const express = require('express');
const app = express();
const morgan = require('morgan');
const bodyParser = require('body-parser');
const mongoose =  require('mongoose');

// registers routes
//const productRoutes = require('./api/routes/products');
//const orderRoutes = require('./api/routes/orders');

const studentRoutes = require('./api/routes/student');
const instructorRoutes = require('./api/routes/instructor');
const assessmentRoutes = require('./api/routes/assessment');
const attendace = require('./api/routes/attendance');
const group = require('./api/routes/group');
const speech_to_text = require('./api/routes/speech_to_text');
//const nlp = require('./api/nlp');

// connect to database
mongoose.connect('mongodb+srv://nyansapoai:nyansapoai@nyansapo-db.fhbbk.azure.mongodb.net/<dbname>?retryWrites=true&w=majority'),{
    useMongoClient: true
};



//mongoose.Promise = global.Promise;

// for handling logging 
app.use(morgan('dev'));

// body-parse middle 
app.use(bodyParser.urlencoded({extended:false}));
app.use(bodyParser.json());

// add header to response 
app.use((req, res, next)=>{
    res.header('Access-Control-Allow-Origin','*'); // you can use this to limit access 
    res.header('Access-Control-Allow-Headers','Origin, X-Requested-With, Content-Type, Accept, Authorization');
    if (req.method == 'OPTIONS'){
        res.header('Access-Control-Allow-Methods', 'PUT, POST, PATCH','DELETE, GET');
        return res.status(200).json({});
    }
    next();
});

// forwards request to routes 
app.use('/instructor', instructorRoutes);
app.use('/student', studentRoutes);
app.use('/assessment',assessmentRoutes);
app.use('/attendance',attendace);
app.use('/group',group);
app.use('/transcribe', speech_to_text);
// app.use('/nlp', nlp);


// handles error 
app.use((req, res, next) => {
    const error = new Error('Not found');
    error.status = 404;
    next(error);
});

// handles internal erros 
app.use((error, req, res, next) =>{
    res.status(error.status || 500);
    res.json({
        error: {
            message: error.message
        }
    });
});

// export apps to server
module.exports = app;