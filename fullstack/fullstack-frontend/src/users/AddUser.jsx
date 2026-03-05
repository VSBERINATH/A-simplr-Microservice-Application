
// src/users/AddUser.jsx
import axios from "axios";
import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import api from "../api/axiosConfig";

function AddUser() {
  const navigate = useNavigate();
  const [user, setUser] = useState({ name: "", username: "", email: "",role: "USER", address: "",password: ""  });
  const onInputChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post("/user", user);
      alert("Registration Successful! Please login.");
      navigate("/");
    } catch (error) {
      if (error.response) {
        alert(`${error.response.data.error}: ${error.response.data.message}`);
      } else {
        alert("Something went wrong while creating user.");
      }
    }
  };

  return (
    <div className="card">
      <div className="card-header">Register User</div>
      <div className="card-body">
        <form onSubmit={onSubmit}>
          <div className="mb-3">
            <label className="form-label">Name</label>
            <input name="name" className="form-control" value={user.name} onChange={onInputChange} required />
          </div>
          <div className="mb-3">
            <label className="form-label">Username</label>
            <input name="username" className="form-control" value={user.username} onChange={onInputChange} required />
          </div>
          <div className="mb-3">
            <label className="form-label">E-mail</label>
            <input type="email" name="email" className="form-control" value={user.email} onChange={onInputChange} required />
          </div>
          {/* Inside the form in AddUser.jsx */}
        <div className="mb-3">
          <label className="form-label">Role</label>
          <select 
            name="role" 
            className="form-control" 
            value={user.role || "USER"} 
            onChange={onInputChange}
          >
            <option value="USER">Standard User</option>
            <option value="ADMIN">Administrator</option>
          </select>
        </div>
          <div className="mb-3">
            <label className="form-label">Address</label>
            <input name="address" className="form-control" value={user.address} onChange={onInputChange} />
          </div>
          <div className="mb-3">
  <label className="form-label">Password</label>
  <input 
    type="password" 
    name="password" 
    className="form-control" 
    value={user.password} 
    onChange={onInputChange} 
    required 
  />
</div>
          <div className="d-flex gap-2">
            <button type="submit" className="btn btn-primary">Submit</button>
            <Link to="/" className="btn btn-secondary">Cancel</Link>
          </div>
        </form>
      </div>
    </div>
  );
}

export default AddUser;
