const jwt = require('jsonwebtoken');

module.exports = (req, res, next) => { // might need more work on token data to add more authentification clarifications 
    // how do you make sure that token given is from which user 
    try{
        const decoded = jwt.verify(req.body.token, "nyansapoai"); // GET token in body
        req.userData = decoded;
        console.log(decoded);
        next();
    } catch (error){
        return res.status(401).json({
            message: 'Authentification i failed',
            error: error
        });
    }

};