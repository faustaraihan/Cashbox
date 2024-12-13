const admin = require("../config/firebase");

const verifyToken = async (req, res, next) => {
  const idToken = req.headers.authorization?.split(" ")[1]; 

  if (!idToken) {
    return res.status(401).json({ message: "Unauthorized, token missing" });
  }

  try {
    const decodedToken = await admin.auth().verifyIdToken(idToken);
    req.uid = decodedToken.uid;
    next(); 
  } catch (error) {
    console.error("Error verifying token:", error);
    switch (error.code) {
      case "auth/argument-error":
        return res.status(400).json({ message: "Invalid token format" });
      case "auth/id-token-expired":
        return res.status(401).json({ message: "Token expired" });
      default:
        return res.status(401).json({ message: "Invalid or expired token" });
    }
  }
};

module.exports = verifyToken;
