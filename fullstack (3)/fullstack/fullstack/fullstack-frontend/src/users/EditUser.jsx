import axios from "axios";
import React, { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import api from "../api/axiosConfig";

export default function EditUser() {
  let navigate = useNavigate();
  const { id } = useParams();
  const loggedInUser = JSON.parse(localStorage.getItem("user"));
  
  const [user, setUser] = useState({ name: "", username: "", email: "", address: "" });
  // const config = { headers: { Authorization: `Basic ${loggedInUser?.authHeader}` } };

  useEffect(() => {
    loadUser();
  }, []);

  const onInputChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
  };

  const loadUser = async () => {
    const result = await api.get(`/user/${id}`); 
    setUser(result.data.content || result.data);
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.put(`http://localhost:8088/user/${id}`, user);
      navigate("/Home");
    } catch (err) {
      alert("Access Denied: You cannot edit this user.");
    }
  };

  return (
    <div className="container">
      <div className="row mt-5">
        <div className="col-md-6 offset-md-3 border rounded p-4 shadow">
          <h2 className="text-center m-4">Edit User</h2>
          <form onSubmit={onSubmit}>
            <div className="mb-3">
              <label className="form-label">Name</label>
              <input type="text" className="form-control" name="name" value={user.name} onChange={onInputChange} />
            </div>
            <div className="mb-3">
              <label className="form-label">Username</label>
              <input type="text" className="form-control" name="username" value={user.username} onChange={onInputChange} />
            </div>
            <div className="mb-3">
              <label className="form-label">Email</label>
              <input type="email" className="form-control" name="email" value={user.email} onChange={onInputChange} />
            </div>
            <div className="mb-3">
              <label className="form-label">Address</label>
              <input type="text" className="form-control" name="address" value={user.address} onChange={onInputChange} />
            </div>
            <button type="submit" className="btn btn-primary">Update</button>
            <Link className="btn btn-outline-danger mx-2" to="/Home">Cancel</Link>
          </form>
        </div>
      </div>
    </div>
  );
}