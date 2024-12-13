const express = require("express");
const { register, login, googleLogin } = require("../controllers/authController");
const verifyToken = require("../middleware/verifyToken");

const router = express.Router();

router.post("/register", register);
router.post("/login", login);
router.post("/google-login", googleLogin);

router.get("/profile", verifyToken, (req, res) => {
  res.status(200).json({ 
    message: "Profile data", 
    uid: req.uid,
  });
});

module.exports = router;
