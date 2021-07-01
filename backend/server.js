const express = require("express");
const app = express();
var cors = require('cors');
const mongoose = require("mongoose")
const bodyParse = require("body-parser")
const routesHandler = require("./routes/routes.js");
const bodyParser = require("body-parser");
require("dotenv/config")


app.use(cors());
app.use(bodyParser.urlencoded({extended:false}));
app.use(bodyParser.json());
app.use('/', routesHandler);


//MongoDB connection (URL inside the .env file)
mongoose.connect(process.env.DB_URI, {useNewUrlParser:true, useUnifiedTopology:true})
.then( () =>{
  console.log("DB conneted!");
})
.catch((err) =>{
 console.log(err)
});

const PORT = process.env.PORT || 4000;
app.listen(PORT, () => {
  console.log('Server is up and running on port ' + PORT +'!');
})