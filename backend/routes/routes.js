const express = require("express");
const router = express.Router();
const Schemas = require('../models/schemas.js')


//change to post request with data from frontend
//LOGIN SYSTEM PROTOTYPE
router.get('/signup', async(req,res) => {
    const newUser = new Schemas.Users({
        username: "Marco",
        email: "marco@email.com",
        password: "testPassword"
    });
    try {
        await newUser.save(async(err, newUserResult) => {
            console.log('new User added to db!');
            res.end('new User added to db!');
        })
    } catch(err){
        console.log(err);
        res.end('error adding User!');
    }
});


//Submits new alert to the MongoDB collection. (Just for test and debugging)
router.get('/addAlert', async(req, res) => {
    const newAlert = new Schemas.Alerts({
            text: "Road closed",
            alertLevel: 1,
            location: {
                type: "Point",
                coordinates: [
                    40.85631,
                    14.24641
                ]
            },
    });
    try {
        await newAlert.save(async(err, newAlertResult) => {
            if(err){
                console.log(err)
            }
            console.log('new Alert added to db!');
            res.end('new Alert added to db!');
        })
    } catch(err){
        console.log('error adding alert!');
        res.end('error adding alert!');
    }
})

//Submits new alert to the MongoDB collection. Uses data recived from the frontend
router.post('/addAlertToApi', async(req, res) => {
    const Data = req.body
    
    console.log(Data)
    const newAlert = new Schemas.Alerts({
        text: Data.text,
        alertLevel: Data.alertLevel,
        location: Data.location
    });
    try {
        await newAlert.save(async(err, newAlertResult) => {
            console.log('new Alert added to db from frontend!');
            res.end('new Alert added to db from frontend!');
        })
    } catch(err){
        console.log(err);
        res.end('error adding alert from frontend!');
    }
})

//Gets the alert collection data from MongoDB (Geospatial query (based on "2dsphere" type))
router.get('/getAlert', async(req, res) => {
    //console.log(req.query.coords) //coordinated from forntend for spatial queries
    Schemas.Alerts.find({
        location:
          { $near :
             {
               $geometry: { type: "Point",  coordinates: [ req.query.coords[0], req.query.coords[1] ] },
               $maxDistance: 800
             }
          }
      }
   )
    .then(foundAlerts => res.json(foundAlerts))
})

module.exports = router;