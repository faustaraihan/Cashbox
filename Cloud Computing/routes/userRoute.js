const express = require("express");
const router = express.Router();
const userController = require("../controllers/userController");

router.get("/:uid", userController.getUserByUid);
router.put("/", userController.updateUserByUid);
router.post("/", userController.addUserDetails);

module.exports = router;
