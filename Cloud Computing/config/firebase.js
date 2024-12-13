const admin = require('firebase-admin');
const serviceAccount = require("../key.json");  // Gantilah dengan path yang sesuai

// Inisialisasi Firebase Admin SDK
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

module.exports = admin;