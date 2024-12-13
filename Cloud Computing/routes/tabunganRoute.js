const express = require("express");
const router = express.Router();
const tabunganController = require("../controllers/tabunganController");

router.post("/", tabunganController.addTabungan);
router.get("/goal/:goal_id", tabunganController.getTabunganByGoal);
router.get("/", tabunganController.getAllTabungan);
router.put("/:id", tabunganController.updateTabungan);
router.delete("/:id", tabunganController.deleteTabungan);

module.exports = router;
