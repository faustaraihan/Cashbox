const db = require('../config/db');

// Menambahkan budgeting
exports.addBudgeting = async (req, res) => {
  try {
    const { nominal, kategori, urgensi } = req.body;
    if (nominal === undefined || kategori === undefined || urgensi === undefined) {
      return res.status(400).json({ message: "All fields are required" });
    }
  
    const [categories] = await db.execute(
      'SELECT id FROM kategori_pengeluaran WHERE id = ?',
      [kategori]
    );

    if (categories.length === 0) {
      return res.status(400).json({ message: "Invalid kategori ID" });
    }

    const query = `INSERT INTO budgeting (nominal, kategori, urgensi) VALUES (?, ?, ?)`;
    await db.execute(query, [nominal || null, kategori || null, urgensi || null]);

    res.status(201).json({
      message: "Budgeting created successfully",
      data: {
        nominal,
        kategori,
        urgensi
      }
    });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Mengedit budgeting
exports.updateBudgeting = async (req, res) => {
  try {
    const { nominal, kategori, urgensi } = req.body;
    const { id } = req.params;
    const [categories] = await db.execute(
      'SELECT id FROM kategori_pengeluaran WHERE id = ?',
      [kategori]
    );

    if (categories.length === 0) {
      return res.status(400).json({ message: "Invalid kategori ID" });
    }

    if (nominal === undefined || kategori === undefined || urgensi === undefined) {
      return res.status(400).json({ message: "One or more required parameters are undefined" });
    }

    const query = `UPDATE budgeting SET nominal = ?, kategori = ?, urgensi = ? WHERE id = ?`;
    await db.execute(query, [nominal ?? null, kategori ?? null, urgensi ?? null, id]);

    res.status(200).json({
      message: "Budgeting updated successfully",
      data: {
        id,
        nominal,
        kategori,
        urgensi
      }
    });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Menghapus budgeting
exports.deleteBudgeting = async (req, res) => {
  try {
    const { id } = req.params;

    const query = `DELETE FROM budgeting WHERE id = ?`;
    await db.execute(query, [id]);

    res.status(200).json({ message: "Budgeting deleted successfully" });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Mendapatkan semua budgeting
exports.getAllBudgeting = async (req, res) => {
  try {
    const [budgets] = await db.execute('SELECT * FROM budgeting');
    res.status(200).json({ data: budgets });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};
