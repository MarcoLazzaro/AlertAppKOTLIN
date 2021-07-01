const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const pointSchema = new Schema({
    type: {
      type: String,
      enum: ['Point'],
      required: true
    },
    coordinates: {
      type: [Number],
      required: true
    }
  });
  
  const alertSchema = new Schema({
    text: String,
    alertLevel:{
      type : Number,
      min: 1,
      max: 3,
      default: 1,
    },
    location: {
      type: pointSchema,
      index: '2dsphere',
      required: true,
    },
    createdAt: { type: Date, default: Date.now, index: { expires: 3600 }}
  });


  const signupTemplate = new Schema ({
    username:{
      type:String,
      required:true
    },
    email:{
      type:String,
      required:true,
      unique:true
    },
    password:{
      type:String,
      required:true
    }
  })


  
  const Alerts = mongoose.model('Alerts', alertSchema);
  const Users = mongoose.model('Users', signupTemplate);
  const Schemas = {'Alerts':Alerts, 'Users':Users};
  module.exports = Schemas;