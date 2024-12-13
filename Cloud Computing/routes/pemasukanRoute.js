const express = require("express");
const router = express.Router();
const pemasukanController = require("../controllers/pemasukanController");

router.post("/", async (req, res, next) => {
    const { uid, nama_sumber_uang } = req.body;
    if (!uid || !nama_sumber_uang) {
        return res.status(400).json({ message: "UID dan nama sumber_uang harus disertakan dalam permintaan" });
    }
    next(); 
}, pemasukanController.createPemasukan);

router.get("/", pemasukanController.getAllPemasukan); 
router.get("/:id_pemasukan", pemasukanController.getPemasukanById);
router.put("/:id_pemasukan", pemasukanController.updatePemasukan);
router.delete("/:id_pemasukan", pemasukanController.deletePemasukan);

module.exports = router;
