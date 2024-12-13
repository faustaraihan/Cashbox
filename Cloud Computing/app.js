const express = require("express");
const cors = require("cors");
const bodyParser = require('body-parser');
const dotenv = require("dotenv");
const authRoute = require("./routes/authRoute"); 
const pemasukanRoute = require("./routes/pemasukanRoute");
const pengeluaranRoute = require("./routes/pengeluaranRoute");
const walletRoute = require('./routes/walletRoute');
const transaksiRoute = require('./routes/transaksiRoute');
const userRoute = require("./routes/userRoute");
const goalsRoute = require('./routes/goalsRoute');
const tabunganRoute = require('./routes/tabunganRoute');
const budgetingRoute = require('./routes/budgetingRoute');

dotenv.config();

const app = express();

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(bodyParser.json());

app.get("/", (req, res) => {
  console.log("Received request to root endpoint");
  res.status(200).json({ message: "Server is running!" });
});  

app.use("/api/auth", authRoute);
app.use("/api/user", userRoute);
app.use("/api/pemasukan", pemasukanRoute);
app.use("/api/pengeluaran", pengeluaranRoute);
app.use("/api/wallet", walletRoute);
app.use("/api/transaksi", transaksiRoute);
app.use("/api/goals", goalsRoute);
app.use("/api/tabungan", tabunganRoute);
app.use('/api/budgeting', budgetingRoute);

app.use((err, req, res, next) => {
  console.error("Error:", err.message || err);
  res.status(500).json({ message: err.message || "Something went wrong" });
});

const port = process.env.PORT || 8080;
app.listen(port, () => {
  console.log(`Server running on port ${port}`);
});
