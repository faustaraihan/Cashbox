const express = require("express");
const router = express.Router();
const pengeluaranController = require("../controllers/pengeluaranController");


router.post("/", pengeluaranController.createPengeluaran);
router.get("/", pengeluaranController.getAllPengeluaran);
router.get("/:id_pengeluaran", pengeluaranController.getPengeluaranById);
router.put("/:id_pengeluaran", pengeluaranController.updatePengeluaran);
router.delete("/:id_pengeluaran", pengeluaranController.deletePengeluaran);

module.exports = router;
