const express = require("express");
const router = express.Router();
const transaksiController = require("../controllers/transaksiController");

router.get("/transaksi", transaksiController.getAllTransaksi);
router.get("/bulan", transaksiController.getTransaksiByBulan);
router.get("/total-pemasukan", transaksiController.getTotalPemasukan);
router.get("/total-pemasukan-bulan", transaksiController.getTotalPemasukanPerBulan);
router.get("/total-pengeluaran-bulan", transaksiController.getTotalPengeluaranPerBulan);
router.get("/total-pengeluaran", transaksiController.getTotalPengeluaran);

module.exports = router;
