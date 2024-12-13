const db = require("../config/db");

// Menambahkan goal baru
exports.addGoal = async (req, res) => {
    const { nama, nominal, tgl_tercapai, uid } = req.body;

    if (!nama || !nominal || !tgl_tercapai || !uid) {
        return res.status(400).json({ message: "Semua field (nama, nominal, tgl_tercapai, uid) wajib diisi." });
    }

    try {
        const query = "INSERT INTO goals (nama, nominal, tgl_tercapai, uid) VALUES (?, ?, ?, ?)";
        await db.query(query, [nama, nominal, tgl_tercapai, uid]);

        res.status(201).json({ message: "Goal berhasil ditambahkan." });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Gagal menambahkan goal.", error: error.message });
    }
};

// Mengedit goal
exports.updateGoal = async (req, res) => {
    const { id } = req.params;
    const { nama, nominal, tgl_tercapai } = req.body;

    if (!nama || !nominal || !tgl_tercapai) {
        return res.status(400).json({ message: "Semua field (nama, nominal, tgl_tercapai) wajib diisi." });
    }

    try {
        const query = "UPDATE goals SET nama = ?, nominal = ?, tgl_tercapai = ? WHERE id = ?";
        const [result] = await db.query(query, [nama, nominal, tgl_tercapai, id]);

        if (result.affectedRows === 0) {
            return res.status(404).json({ message: "Goal tidak ditemukan." });
        }

        res.status(200).json({ message: "Goal berhasil diperbarui." });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Gagal memperbarui goal.", error: error.message });
    }
};

// Mengambil semua goals
exports.getAllGoals = async (req, res) => {
    try {
        const query = "SELECT * FROM goals ORDER BY tgl_tercapai DESC";
        const [rows] = await db.query(query);

        if (rows.length === 0) {
            return res.status(404).json({ message: "Tidak ada goals ditemukan." });
        }

        res.status(200).json({
            message: "Data goals berhasil diambil.",
            data: rows,
        });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Gagal mengambil data goals.", error: error.message });
    }
};

// Mengambil goal berdasarkan ID
exports.getGoalById = async (req, res) => {
    const { id } = req.params;

    try {
        const query = "SELECT * FROM goals WHERE id = ?";
        const [rows] = await db.query(query, [id]);

        if (rows.length === 0) {
            return res.status(404).json({ message: "Goal tidak ditemukan." });
        }

        res.status(200).json({
            message: "Data goal berhasil diambil.",
            data: rows[0],
        });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Gagal mengambil data goal.", error: error.message });
    }
};


// Menghapus goal
exports.deleteGoal = async (req, res) => {
    const { id } = req.params;

    try {
        const query = "DELETE FROM goals WHERE id = ?";
        const [result] = await db.query(query, [id]);

        if (result.affectedRows === 0) {
            return res.status(404).json({ message: "Goal tidak ditemukan." });
        }

        res.status(200).json({ message: "Goal berhasil dihapus." });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Gagal menghapus goal.", error: error.message });
    }
};