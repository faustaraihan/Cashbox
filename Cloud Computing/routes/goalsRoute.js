const express = require("express");
const router = express.Router();
const goalsController = require("../controllers/goalsController");

router.post("/", goalsController.addGoal);
router.put("/:id", goalsController.updateGoal);
router.delete("/:id", goalsController.deleteGoal);
router.get("/", goalsController.getAllGoals);
router.get("/:id", goalsController.getGoalById);

module.exports = router;
