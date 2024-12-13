const db = require("../config/db");

// Menambahkan tabungan baru untuk goal tertentu
exports.addTabungan = async (req, res) => {
    const { deskripsi, nominal, tgl_tabung, goal_id, uid } = req.body;

    if (!deskripsi || !nominal || !tgl_tabung || !goal_id || !uid) {
        return res.status(400).json({ message: "Semua field (deskripsi, nominal, tgl_tabung, goal_id, uid) wajib diisi." });
    }

    try {
        const query = "INSERT INTO tabungan (deskripsi, nominal, tgl_tabung, goal_id, uid) VALUES (?, ?, ?, ?, ?)";
        await db.query(query, [deskripsi, nominal, tgl_tabung, goal_id, uid]);

        res.status(201).json({ message: "Tabungan berhasil ditambahkan." });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Gagal menambahkan tabungan.", error: error.message });
    }
};

// Mengambil semua tabungan untuk goal tertentu dan uid
exports.getTabunganByGoal = async (req, res) => {
    const { goal_id } = req.params;
    const { uid } = req.query;

    if (!uid) {
        return res.status(400).json({ message: "UID wajib disertakan dalam query parameter." });
    }

    try {
        const query = "SELECT * FROM tabungan WHERE goal_id = ? AND uid = ?";
        const [rows] = await db.query(query, [goal_id, uid]);

        if (rows.length === 0) {
            return res.status(404).json({ message: "Tidak ada tabungan ditemukan untuk goal ini." });
        }

        res.status(200).json({
            message: "Data tabungan berhasil diambil.",
            data: rows,
        });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Gagal mengambil data tabungan.", error: error.message });
    }
};

// Mengedit tabungan
exports.updateTabungan = async (req, res) => {
    const { id } = req.params;
    const { deskripsi, nominal, tgl_tabung, uid } = req.body;

    if (!deskripsi || !nominal || !tgl_tabung || !uid) {
        return res.status(400).json({ message: "Semua field (deskripsi, nominal, tgl_tabung, uid) wajib diisi." });
    }

    try {
        const query = "UPDATE tabungan SET deskripsi = ?, nominal = ?, tgl_tabung = ?, uid = ? WHERE id = ?";
        const [result] = await db.query(query, [deskripsi, nominal, tgl_tabung, uid, id]);

        if (result.affectedRows === 0) {
            return res.status(404).json({ message: "Tabungan tidak ditemukan." });
        }

        res.status(200).json({ message: "Tabungan berhasil diperbarui." });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Gagal memperbarui tabungan.", error: error.message });
    }
};

// Mengambil semua tabungan
exports.getAllTabungan = async (req, res) => {
    try {
        const query = "SELECT * FROM tabungan ORDER BY tgl_tabung DESC";
        const [rows] = await db.query(query);

        if (rows.length === 0) {
            return res.status(404).json({ message: "Tidak ada tabungan ditemukan." });
        }

        res.status(200).json({
            message: "Data tabungan berhasil diambil.",
            data: rows,
        });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Gagal mengambil data tabungan.", error: error.message });
    }
};

// Menghapus tabungan
exports.deleteTabungan = async (req, res) => {
    const { id } = req.params;
    const { uid } = req.query;

    if (!uid) {
        return res.status(400).json({ message: "UID wajib disertakan dalam query parameter." });
    }

    try {
        const query = "DELETE FROM tabungan WHERE id = ? AND uid = ?";
        const [result] = await db.query(query, [id, uid]);

        if (result.affectedRows === 0) {
            return res.status(404).json({ message: "Tabungan tidak ditemukan atau tidak sesuai UID." });
        }

        res.status(200).json({ message: "Tabungan berhasil dihapus." });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Gagal menghapus tabungan.", error: error.message });
    }
};
