const db = require("../config/db");

// Menambahkan wallet dengan `uid` di body
const addWallet = async (req, res) => {
    console.log("Request Body:", req.body);
    const { nama, nominal, uid } = req.body;

    if (!nama || typeof nominal !== "number" || !uid) {
        return res.status(400).json({ message: "Nama, nominal, dan uid harus diisi dengan benar" });
    }

    try {
        const query = "INSERT INTO wallet (nama, nominal, uid) VALUES (?, ?, ?)";
        await db.query(query, [nama, nominal, uid]);
        return res.status(201).json({ message: "Wallet berhasil ditambahkan", uid });
    } catch (error) {
        console.error("Error adding wallet:", error);
        return res.status(500).json({ message: "Terjadi kesalahan pada server" });
    }
};

// Memperbarui nama wallet
const updateWalletName = async (req, res) => {
    const { id } = req.params;
    const { nama } = req.body;

    try {
        const [result] = await db.query(
            "UPDATE wallet SET nama = ? WHERE id = ?",
            [nama, id]
        );

        if (result.affectedRows === 0) {
            return res.status(404).json({ message: "Wallet tidak ditemukan" });
        }

        return res.status(200).json({ message: "Nama wallet berhasil diperbarui" });
    } catch (error) {
        console.error("Error updating wallet name:", error);
        res.status(500).json({ message: "Terjadi kesalahan pada server" });
    }
};

// Mendapatkan semua wallet
const getAllWallets = async (req, res) => {
    try {
        const [rows] = await db.query("SELECT id, nama, nominal, uid FROM wallet"); 
        res.status(200).json({
            message: "Daftar wallet berhasil diambil.",
            data: rows.map(wallet => ({
                ...wallet,
                uid: wallet.uid
            }))
        });
    } catch (error) {
        console.error("Error fetching wallets:", error);
        res.status(500).json({ message: "Terjadi kesalahan pada server." });
    }
};

// Mendapatkan wallet berdasarkan ID
const getWalletById = async (req, res) => {
    const { id } = req.params;

    try {
        const [rows] = await db.query("SELECT * FROM wallet WHERE id = ?", [id]);

        if (rows.length === 0) {
            return res.status(404).json({ message: "Wallet tidak ditemukan" });
        }

        return res.status(200).json({
            message: "Wallet berhasil ditemukan",
            data: { ...rows[0], uid: rows[0].uid }
        });
    } catch (error) {
        console.error("Error fetching wallet by ID:", error);
        return res.status(500).json({ message: "Terjadi kesalahan pada server" });
    }
};

// Menghapus wallet berdasarkan ID
const deleteWallet = async (req, res) => {
    const { id } = req.params;

    try {
        const [result] = await db.query("DELETE FROM wallet WHERE id = ?", [id]);

        if (result.affectedRows === 0) {
            return res.status(404).json({ message: "Wallet tidak ditemukan" });
        }

        return res.status(200).json({ message: "Wallet berhasil dihapus" });
    } catch (error) {
        console.error("Error deleting wallet:", error);
        return res.status(500).json({ message: "Terjadi kesalahan pada server" });
    }
};

module.exports = {
    addWallet,
    updateWalletName,
    getAllWallets,
    getWalletById,
    deleteWallet
};
