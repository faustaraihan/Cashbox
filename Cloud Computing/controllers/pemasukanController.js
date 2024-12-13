const db = require("../config/db");

// Menambahkan pemasukan baru
exports.createPemasukan = async (req, res) => {
    const { deskripsi, nominal, kategori_masuk, tanggal, fk_sumber_uang, uid, nama_sumber_uang } = req.body;

    try {
        // Validasi input
        if (!deskripsi || !nominal || !kategori_masuk || !tanggal || !fk_sumber_uang || !uid || !nama_sumber_uang) {
            return res.status(400).json({ message: "Semua field wajib diisi." });
        }

        // Validasi wallet
        const [walletRows] = await db.query("SELECT * FROM wallet WHERE id = ?", [fk_sumber_uang]);
        if (walletRows.length === 0) {
            return res.status(404).json({ message: "Wallet tidak ditemukan." });
        }
        const wallet = walletRows[0];

        // Validasi kategori
        const [kategoriRows] = await db.query("SELECT * FROM kategori_pemasukan WHERE id = ?", [kategori_masuk]);
        if (kategoriRows.length === 0) {
            return res.status(404).json({ message: "Kategori tidak ditemukan." });
        }

        // Tambahkan pemasukan
        const [pemasukanResult] = await db.query(
            "INSERT INTO pemasukan (deskripsi, nominal, kategori_masuk, tanggal, sumber_uang, uid, nama_sumber_uang) VALUES (?, ?, ?, ?, ?, ?, ?)",
            [deskripsi, nominal, kategori_masuk, tanggal, fk_sumber_uang, uid, nama_sumber_uang]
        );

        // Update nominal wallet
        const updatedNominal = parseFloat(wallet.nominal) + parseFloat(nominal);
        await db.query("UPDATE wallet SET nominal = ? WHERE id = ?", [updatedNominal, fk_sumber_uang]);

        res.status(201).json({
            message: "Pemasukan berhasil ditambahkan.",
            data: {
                id: pemasukanResult.insertId,
                deskripsi,
                nominal,
                kategori_masuk,
                tanggal,
                fk_sumber_uang,
                uid,
                nama_sumber_uang
            },
        });
    } catch (error) {
        console.error("Error creating pemasukan:", error);
        res.status(500).json({ message: "Terjadi kesalahan pada server." });
    }
};

// Mengambil semua data pemasukan beserta informasi wallet
exports.getAllPemasukan = async (req, res) => {
    try {
        const query = `
            SELECT p.*, w.nama AS wallet_name, w.nominal AS wallet_nominal, w.uid AS wallet_uid
            FROM pemasukan p
            LEFT JOIN wallet w ON p.sumber_uang = w.id
        `;
        const [rows] = await db.query(query);
        res.status(200).json(rows.map(pemasukan => ({
            ...pemasukan,
            uid: pemasukan.wallet_uid
        })));
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Terjadi kesalahan pada server" });
    }
};

// Mengupdate data pemasukan
exports.updatePemasukan = async (req, res) => {
    const { id_pemasukan } = req.params;
    const { deskripsi, nominal, fk_sumber_uang, tanggal, kategori_masuk, uid, nama_sumber_uang } = req.body;

    try {
        // Ambil data pemasukan lama
        const [oldPemasukanRows] = await db.query("SELECT nominal, sumber_uang FROM pemasukan WHERE id_pemasukan = ?", [id_pemasukan]);
        if (oldPemasukanRows.length === 0) {
            return res.status(404).json({ message: "Pemasukan tidak ditemukan" });
        }
        const oldPemasukan = oldPemasukanRows[0];
        const nominalDiff = nominal - oldPemasukan.nominal;

        // Update data pemasukan
        const [result] = await db.query(
            `UPDATE pemasukan 
            SET deskripsi = ?, nominal = ?, sumber_uang = ?, tanggal = ?, kategori_masuk = ?, uid = ?, nama_sumber_uang = ? 
            WHERE id_pemasukan = ?`,
            [deskripsi, nominal, fk_sumber_uang, tanggal, kategori_masuk, uid, nama_sumber_uang, id_pemasukan]
        );

        if (result.affectedRows === 0) {
            return res.status(404).json({ message: "Pemasukan tidak ditemukan" });
        }

        // Update nominal wallet if the source of funds has changed
        if (oldPemasukan.sumber_uang !== fk_sumber_uang) {
            // Ambil nominal lama dari wallet sumber_uang yang baru
            const [walletRows] = await db.query("SELECT nominal FROM wallet WHERE id = ?", [fk_sumber_uang]);
            if (walletRows.length === 0) {
                return res.status(404).json({ message: "Wallet tidak ditemukan" });
            }
            const wallet = walletRows[0];

            // Update nominal wallet
            const updatedNominal = parseFloat(wallet.nominal) + nominalDiff;
            await db.query("UPDATE wallet SET nominal = ? WHERE id = ?", [updatedNominal, fk_sumber_uang]);
        }

        return res.status(200).json({ message: "Pemasukan berhasil diperbarui" });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Terjadi kesalahan pada server" });
    }
};

// Mengambil pemasukan berdasarkan ID
exports.getPemasukanById = async (req, res) => {
    const { id_pemasukan } = req.params;

    try {
        const [rows] = await db.query("SELECT p.*, w.uid AS wallet_uid FROM pemasukan p LEFT JOIN wallet w ON p.sumber_uang = w.id WHERE id_pemasukan = ?", [id_pemasukan]);

        if (rows.length === 0) {
            return res.status(404).json({ message: "Pemasukan tidak ditemukan" });
        }

        return res.status(200).json({
            message: "Pemasukan berhasil ditemukan",
            data: {
                ...rows[0],
                uid: rows[0].wallet_uid
            }
        });
    } catch (error) {
        console.error(error);
        return res.status(500).json({ message: "Terjadi kesalahan pada server" });
    }
};

// Menghapus data pemasukan
exports.deletePemasukan = async (req, res) => {
    const { id_pemasukan } = req.params;

    try {
        // Ambil data pemasukan yang akan dihapus
        const [pemasukanRows] = await db.query("SELECT nominal, sumber_uang FROM pemasukan WHERE id_pemasukan = ?", [id_pemasukan]);
        if (pemasukanRows.length === 0) {
            return res.status(404).json({ message: "Pemasukan tidak ditemukan" });
        }
        const pemasukan = pemasukanRows[0];

        // Hapus pemasukan
        const [result] = await db.query("DELETE FROM pemasukan WHERE id_pemasukan = ?", [id_pemasukan]);
        if (result.affectedRows === 0) {
            return res.status(404).json({ message: "Pemasukan tidak ditemukan" });
        }

        // Update nominal wallet
        await db.query("UPDATE wallet SET nominal = nominal - ? WHERE id = ?", [pemasukan.nominal, pemasukan.sumber_uang]);

        res.status(200).json({ message: "Pemasukan berhasil dihapus" });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Terjadi kesalahan pada server" });
    }
};
