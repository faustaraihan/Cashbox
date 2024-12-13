const express = require("express");
const { addWallet, getAllWallets, updateWalletName, getWalletById, deleteWallet } = require("../controllers/walletController");
const validateId = require("../middleware/validateId");
const router = express.Router();

router.post("/", async (req, res, next) => {
    const { uid } = req.body;
    if (!uid) {
        return res.status(400).json({ message: "UID harus disertakan dalam permintaan" });
    }
    next();
}, addWallet);

router.get("/", getAllWallets);
router.get("/:id", validateId, getWalletById);
router.put("/:id", validateId, updateWalletName);
router.delete("/:id", validateId, deleteWallet);

module.exports = router;
