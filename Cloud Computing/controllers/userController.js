const db = require("../config/db");
const { format } = require('date-fns');

exports.getUserByUid = async (req, res) => {
    const { uid } = req.params;

    try {
        const [userRows] = await db.query(
            "SELECT akun.email, akun.name FROM akun WHERE akun.uid = ?",
            [uid]
        );

        if (userRows.length === 0) {
            return res.status(404).json({ message: "User tidak ditemukan." });
        }

        // Mendapatkan data user lengkap dari tabel user
        const [userData] = await db.query(
            "SELECT * FROM user WHERE uid = ?",
            [uid]
        );

        if (userData.length === 0) {
            return res.status(404).json({ message: "User tidak ditemukan di tabel user." });
        }

        const user = userData[0];
        res.status(200).json({
            message: "Data user berhasil ditemukan.",
            data: {
                uid: user.uid,
                tgl_lahir: user.tgl_lahir,
                no_telp: user.no_telp,
                gender: user.gender,
                email: userRows[0].email,
                name: userRows[0].name,
                created_at: user.created_at,
                update_at: user.update_at,
            },
        });
    } catch (error) {
        console.error("Error fetching user by UID:", error);
        res.status(500).json({ message: "Terjadi kesalahan pada server." });
    }
};

// Mengupdate data user berdasarkan UID
exports.updateUserByUid = async (req, res) => {
    const { uid, tgl_lahir, no_telp, gender } = req.body;

    try {
        if (!uid || !tgl_lahir || !no_telp || !gender) {
            return res.status(400).json({ message: "Field uid, tgl_lahir, no_telp, dan gender wajib diisi." });
        }

        if (!['L', 'P'].includes(gender)) {
            return res.status(400).json({ message: "Gender harus berupa 'L' (Laki-laki) atau 'P' (Perempuan)." });
        }

        const [result] = await db.query(
            "UPDATE user SET tgl_lahir = ?, no_telp = ?, gender = ?, update_at = NOW() WHERE uid = ?",
            [tgl_lahir, no_telp, gender, uid]
        );

        if (result.affectedRows === 0) {
            return res.status(404).json({ message: "User tidak ditemukan atau tidak ada perubahan." });
        }

        const [updatedUserRows] = await db.query(
            "SELECT akun.email, akun.name FROM akun WHERE akun.uid = ?",
            [uid]
        );
        const updatedUser = updatedUserRows[0];

        res.status(200).json({
            message: "Data user berhasil diperbarui.",
            data: {
                uid: uid,
                tgl_lahir: tgl_lahir,
                no_telp: no_telp,
                gender: gender,
                update_at: format(new Date(), 'yyyy-MM-dd HH:mm:ss'), 
                email: updatedUser ? updatedUser.email : null, 
                name: updatedUser ? updatedUser.name : null,
            },
        });
    } catch (error) {
        console.error("Error updating user by UID:", error);
        res.status(500).json({ message: "Terjadi kesalahan pada server." });
    }
};

// Menambahkan data pengguna ke tabel user
exports.addUserDetails = async (req, res) => {
    const { uid, tgl_lahir, no_telp, gender } = req.body;

    try {
        if (!uid || !tgl_lahir || !no_telp || !gender) {
            return res.status(400).json({ message: "Field uid, tgl_lahir, no_telp, dan gender wajib diisi." });
        }

        if (!['L', 'P'].includes(gender)) {
            return res.status(400).json({ message: "Gender harus berupa 'L' (Laki-laki) atau 'P' (Perempuan)." });
        }

        const [result] = await db.query(
            "INSERT INTO user (uid, tgl_lahir, no_telp, gender, created_at, update_at) VALUES (?, ?, ?, ?, NOW(), NOW())",
            [uid, tgl_lahir, no_telp, gender]
        );

        if (result.affectedRows === 0) {
            return res.status(404).json({ message: "User tidak ditemukan atau tidak ada perubahan." });
        }

        const [newUserRows] = await db.query(
            "SELECT akun.email, akun.name FROM akun WHERE akun.uid = ?",
            [uid]
        );
        const newUser = newUserRows[0];

        res.status(201).json({
            message: "Data user berhasil ditambahkan.",
            data: {
                uid: uid,
                tgl_lahir: tgl_lahir,
                no_telp: no_telp,
                gender: gender,
                created_at: format(new Date(), 'yyyy-MM-dd HH:mm:ss'),
                update_at: format(new Date(), 'yyyy-MM-dd HH:mm:ss'),
                email: newUser.email,
                name: newUser.name
            },
        });
    } catch (error) {
        console.error("Error adding user details:", error);
        res.status(500).json({ message: "Terjadi kesalahan pada server." });
    }
};
