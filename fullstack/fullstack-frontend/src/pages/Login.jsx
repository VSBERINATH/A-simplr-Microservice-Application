import React, { useState } from "react";
import axios from "axios";
// import { useNavigate } from "react-router-dom";
import { useNavigate, Link } from "react-router-dom"; // Add Link here
export default function Login() {
  const [credentials, setCredentials] = useState({ username: "", password: "" });
  const navigate = useNavigate();

  // const handleLogin = async (e) => {
  //   e.preventDefault();
  //   try {
  //     // Step 1: Create the Base64 token
  //     const token = btoa(`${credentials.username}:${credentials.password}`);
      
  //     // Step 2: Call the backend to verify. 
  //     // Note: Use /user/me (we will add this to the backend next)
  //     const res = await axios.get(`http://localhost:8088/user/me`, {
  //       headers: { Authorization: `Basic ${token}` }
  //     });

  //     // Step 3: Save info to localStorage
  //     const authData = {
  //       username: res.data.username,
  //       role: res.data.role, 
  //       authHeader: token // Store this for all future API calls
  //     };
      
  //     localStorage.setItem("user", JSON.stringify(authData));
  //     navigate("/Home");
  //   } catch (err) {
  //     alert("Invalid Username or Password");
  //   }
  // };
  // inside Login.jsx handleLogin function
const handleLogin = async (e) => {
  e.preventDefault();
  try {
    // 1. Call the new JWT endpoint
    const res = await axios.post("http://localhost:8088/auth/login", credentials);

    // 2. Extract the token and user info from the response
    const { jwt:token, user } = res.data;

    // 3. Store them separately
    localStorage.setItem("token", token);
    localStorage.setItem("user", JSON.stringify(user));

    navigate("/Home");
  } catch (err) {
    alert("Invalid Username or Password");
  }
};

  return (
    <div className="container mt-5">
      <div className="col-md-4 offset-md-4 border rounded p-4 shadow bg-light">
        <h2 className="text-center mb-4">Login</h2>
        <form onSubmit={handleLogin}>
          <div className="mb-3">
            <label className="form-label">Username</label>
            <input type="text" className="form-control" 
              onChange={(e) => setCredentials({...credentials, username: e.target.value})} required />
          </div>
          <div className="mb-3">
            <label className="form-label">Password</label>
            <input type="password" className="form-control" 
              onChange={(e) => setCredentials({...credentials, password: e.target.value})} required />
          </div>
          <button className="btn btn-primary w-100">Sign In</button>
          <div className="mt-3 text-center">
  <p>Don't have an account? <Link to="/register">Register Here</Link></p>
</div>
        </form>
      </div>
    </div>
  );
}