const db = require("../config/db"); 

// Menambahkan pengeluaran baru dengan wallet_id
exports.createPengeluaran = async (req, res) => {
    const { deskripsi, nominal, sumber_uang, kategori_keluar, tanggal, uid, nama_sumber_uang } = req.body;

    try {
        // Validasi input
        if (!deskripsi || !nominal || !sumber_uang || !kategori_keluar || !tanggal || !uid || !nama_sumber_uang) {
            return res.status(400).json({ message: "Semua field wajib diisi." });
        }

        // Validasi wallet
        const [walletRows] = await db.query("SELECT * FROM wallet WHERE id = ?", [sumber_uang]);
        if (walletRows.length === 0) {
            return res.status(404).json({ message: "Wallet tidak ditemukan." });
        }
        const wallet = walletRows[0];

        // Validasi kategori
        const [kategoriRows] = await db.query("SELECT * FROM kategori_pengeluaran WHERE id = ?", [kategori_keluar]);
        if (kategoriRows.length === 0) {
            return res.status(404).json({ message: "Kategori tidak ditemukan." });
        }

        // Validasi saldo wallet mencukupi
        if (wallet.nominal < nominal) {
            return res.status(400).json({ message: "Saldo wallet tidak mencukupi." });
        }

        // Menambahkan pengeluaran
        const [pengeluaranResult] = await db.query(
            "INSERT INTO pengeluaran (deskripsi, nominal, kategori_keluar, tanggal, sumber_uang, uid, nama_sumber_uang) VALUES (?, ?, ?, ?, ?, ?, ?)",
            [deskripsi, nominal, kategori_keluar, tanggal, sumber_uang, uid, nama_sumber_uang]
        );

        // Update nominal wallet
        const updatedNominal = parseFloat(wallet.nominal) - parseFloat(nominal);
        await db.query("UPDATE wallet SET nominal = ? WHERE id = ?", [updatedNominal, sumber_uang]);

        res.status(201).json({
            message: "Pengeluaran berhasil ditambahkan.",
            data: {
                id: pengeluaranResult.insertId,
                deskripsi,
                nominal,
                kategori_keluar,
                tanggal,
                sumber_uang,
                uid,
                nama_sumber_uang
            },
        });
    } catch (error) {
        console.error("Error creating pengeluaran:", error);
        res.status(500).json({ message: "Terjadi kesalahan pada server." });
    }
};

// Mengambil semua data pengeluaran beserta informasi wallet
exports.getAllPengeluaran = async (req, res) => {
    try {
        const query = `
            SELECT p.*, w.nama AS wallet_name, w.nominal AS wallet_nominal
            FROM pengeluaran p
            LEFT JOIN wallet w ON p.sumber_uang = w.id
        `;
        const [rows] = await db.query(query);
        res.status(200).json(rows);
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Terjadi kesalahan pada server" });
    }
};

// Mengambil data pengeluaran berdasarkan ID
exports.getPengeluaranById = async (req, res) => {
    const { id_pengeluaran } = req.params;
    try {
        const [rows] = await db.query("SELECT * FROM pengeluaran WHERE id_pengeluaran = ?", [id_pengeluaran]);
        if (rows.length === 0) {
            return res.status(404).json({ message: "Pengeluaran tidak ditemukan" });
        }
        res.status(200).json(rows[0]);
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Terjadi kesalahan pada server" });
    }
};

// Mengupdate data pengeluaran
exports.updatePengeluaran = async (req, res) => {
    const { id_pengeluaran } = req.params;
    const { deskripsi, nominal, sumber_uang, kategori_keluar, tanggal, uid } = req.body;

    try {
        // Ambil pengeluaran lama
        const [oldPengeluaranRows] = await db.query("SELECT nominal, sumber_uang, uid FROM pengeluaran WHERE id_pengeluaran = ?", [id_pengeluaran]);
        if (oldPengeluaranRows.length === 0) {
            return res.status(404).json({ message: "Pengeluaran tidak ditemukan" });
        }
        const oldPengeluaran = oldPengeluaranRows[0];

        // Hitung selisih nominal pengeluaran (nominal baru - nominal lama)
        const nominalDiff = nominal - oldPengeluaran.nominal;

        // Ambil data wallet untuk sumber_uang yang terkait
        const [walletRows] = await db.query("SELECT nominal FROM wallet WHERE id = ?", [sumber_uang]);
        if (walletRows.length === 0) {
            return res.status(404).json({ message: "Wallet tidak ditemukan" });
        }
        const wallet = walletRows[0];

        let updatedNominal;

        // Jika nominal yang baru lebih kecil dari yang lama, wallet akan bertambah
        if (nominal < oldPengeluaran.nominal) {
            updatedNominal = parseFloat(wallet.nominal) + (oldPengeluaran.nominal - nominal);
        } else {
            // Jika nominal yang baru lebih besar dari yang lama, wallet akan berkurang
            updatedNominal = parseFloat(wallet.nominal) - (nominal - oldPengeluaran.nominal);
        }

        // Periksa apakah saldo wallet mencukupi setelah perubahan nominal
        if (updatedNominal < 0) {
            return res.status(400).json({ message: "Saldo wallet tidak mencukupi." });
        }

        // Update data pengeluaran
        const [updateResult] = await db.query(
            "UPDATE pengeluaran SET deskripsi = ?, nominal = ?, sumber_uang = ?, kategori_keluar = ?, tanggal = ?, uid = ? WHERE id_pengeluaran = ?",
            [deskripsi, nominal, sumber_uang, kategori_keluar, tanggal, uid, id_pengeluaran]
        );

        if (updateResult.affectedRows === 0) {
            return res.status(404).json({ message: "Pengeluaran gagal diperbarui" });
        }

        // Update nominal wallet
        await db.query("UPDATE wallet SET nominal = ? WHERE id = ?", [updatedNominal, sumber_uang]);

        return res.status(200).json({ message: "Pengeluaran berhasil diperbarui" });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Terjadi kesalahan pada server" });
    }
};

// Menghapus data pengeluaran
exports.deletePengeluaran = async (req, res) => {
    const { id_pengeluaran } = req.params;

    try {
        // Ambil data pengeluaran yang akan dihapus
        const [pengeluaranRows] = await db.query("SELECT nominal, sumber_uang FROM pengeluaran WHERE id_pengeluaran = ?", [id_pengeluaran]);
        if (pengeluaranRows.length === 0) {
            return res.status(404).json({ message: "Pengeluaran tidak ditemukan" });
        }
        const pengeluaran = pengeluaranRows[0];

        // Hapus pengeluaran
        const [result] = await db.query("DELETE FROM pengeluaran WHERE id_pengeluaran = ?", [id_pengeluaran]);

        if (result.affectedRows === 0) {
            return res.status(404).json({ message: "Pengeluaran tidak ditemukan" });
        }

        // Update nominal wallet
        const [walletRows] = await db.query("SELECT nominal FROM wallet WHERE id = ?", [pengeluaran.sumber_uang]);
        if (walletRows.length === 0) {
            return res.status(404).json({ message: "Wallet tidak ditemukan" });
        }
        const wallet = walletRows[0];

        const updatedNominal = parseFloat(wallet.nominal) + parseFloat(pengeluaran.nominal);
        await db.query("UPDATE wallet SET nominal = ? WHERE id = ?", [updatedNominal, pengeluaran.sumber_uang]);

        return res.status(200).json({ message: "Pengeluaran berhasil dihapus" });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Terjadi kesalahan pada server" });
    }
};
