const admin = require("../config/firebase");
const db = require("../config/db");
const bcrypt = require("bcrypt"); 
const jwt = require("jsonwebtoken");
const { body, validationResult } = require("express-validator");

const SECRET_KEY = process.env.SECRET_KEY || "yourSuperSecretKey"; 

const AuthController = {
  // Fungsi untuk Register
  register: [
    body("email").isEmail().withMessage("Invalid email format"),
    body("password").isLength({ min: 6 }).withMessage("Password must be at least 6 characters"),
    body("name").notEmpty().withMessage("Name is required"),

    async (req, res) => {
      const { email, password, name } = req.body;

      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
      }

      try {
        const checkEmailQuery = `SELECT uid FROM akun WHERE email = ?`; 
        const [emailResults] = await db.query(checkEmailQuery, [email]);
        if (emailResults.length > 0) {
          return res.status(400).json({ message: "Email already registered" });
        }
        
        const firebaseUser = await admin.auth().createUser({ email, password });
        const uid = firebaseUser.uid;
        
        const passwordHash = await bcrypt.hash(password, 10);

        const query = `INSERT INTO akun (uid, email, password_hash, name) VALUES (?, ?, ?, ?)`;
        await db.query(query, [uid, email, passwordHash, name]);

        res.status(201).json({
          message: "User registered successfully",
          user: { uid, email, name },
        });
      } catch (error) {
        console.error("Registration error:", error);
        res.status(500).json({ message: "Registration failed", error: error.message });
      }
    },
  ],

  // Fungsi untuk Login
login: [
    body("email").isEmail().withMessage("Invalid email format"),
    body("password").notEmpty().withMessage("Password is required"),
  
    async (req, res) => {
      const { email, password } = req.body;
  
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
      }
  
      try {
        // Ambil data pengguna berdasarkan email
        const query = `SELECT uid, password_hash, name, email FROM akun WHERE email = ?`; 
        const [rows] = await db.query(query, [email]);
  
        if (rows.length === 0) {
          return res.status(404).json({ message: "User not found" });
        }
  
        const user = rows[0];
  
        const isPasswordValid = await bcrypt.compare(password, user.password_hash);
        if (!isPasswordValid) {
          return res.status(401).json({ message: "Invalid credentials" });
        }
  
        const token = jwt.sign({ uid: user.uid }, SECRET_KEY, {
          expiresIn: "1h", 
          audience: "yourAppName", 
          issuer: "yourIssuerName"
        });
  
        res.status(200).json({
          message: "Login successful",
          token,
          user: { uid: user.uid, email: user.email, name: user.name },
        });
      } catch (error) {
        console.error("Login error:", error);
        res.status(500).json({ message: "Login failed", error: error.message });
      }
    },
  ],  

  // Fungsi untuk Login dengan Google
  googleLogin: async (req, res) => {
    const { idToken } = req.body;

    try {
      const decodedToken = await admin.auth().verifyIdToken(idToken);
      const uid = decodedToken.uid;
      const email = decodedToken.email;
      const name = decodedToken.name || "No name";

      const query = `SELECT uid FROM akun WHERE uid = ?`;
      const [results] = await db.query(query, [uid]);

      if (results.length === 0) {
        const queryInsert = `INSERT INTO akun (uid, email, name) VALUES (?, ?, ?)`;
        await db.query(queryInsert, [uid, email, name]);
      }

      const token = jwt.sign({ uid }, SECRET_KEY, { expiresIn: "1h" });

      res.status(200).json({
        message: "Google login successful",
        token,
        user: { uid, email, name },
      });
    } catch (error) {
      console.error("Google login error:", error);
      res.status(500).json({ message: "Google login failed", error: error.message });
    }
  },
};

module.exports = AuthController;
