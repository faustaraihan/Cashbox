const mysql = require('mysql2/promise'); // Menggunakan mysql2/promise untuk mendukung Promise
const dotenv = require('dotenv');

dotenv.config();

const pool = mysql.createPool({
    host: process.env.DB_HOST,       // Gunakan environment variables untuk konfigurasi
    user: process.env.DB_USER,       
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAME,
    waitForConnections: true,        // Untuk menunggu koneksi
    connectionLimit: 10,             // Membatasi jumlah koneksi di pool
    queueLimit: 0                    // Tidak ada batasan antrian
});

// Fungsi untuk melakukan pengecekan koneksi ke database
const checkConnection = async () => {
    try {
        const connection = await pool.getConnection();
        console.log('Connected to database.');
        connection.release(); // Melepaskan koneksi setelah digunakan
    } catch (err) {
        console.error('Database connection failed:', err.stack);
    }
};

checkConnection();

module.exports = pool;