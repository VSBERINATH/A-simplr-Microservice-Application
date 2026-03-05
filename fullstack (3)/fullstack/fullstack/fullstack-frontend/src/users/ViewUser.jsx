import axios from "axios";
import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import VerifyAddressModal from "./VerifyAddress";
import api from "../api/axiosConfig";

export default function ViewUser() {
  const [user, setUser] = useState({ name: "", username: "", email: "", address: "" });
  const [showVerify, setShowVerify] = useState(false);
  const { id } = useParams();
  
  const loggedInUser = JSON.parse(localStorage.getItem("user"));
  const config = {
    headers: { Authorization: `Basic ${loggedInUser?.authHeader}` }
  };

  useEffect(() => {
    loadUser();
  }, []);

  const loadUser = async () => {
    try {
      const result = await api.get(`/user/${id}`); 
    setUser(result.data.content || result.data);
    } catch (error) {
      alert("Error fetching user data.");
    }
  };

  return (
    <div className="container-form">
      <div className="form-card">
        <h2 className="form-title text-center">User Profile</h2>
        <hr />
        <div className="card-shell p-4 mb-3">
          <ul className="list-group list-group-flush">
            <li className="list-group-item"><b>Name:</b> {user.name}</li>
            <li className="list-group-item"><b>Username:</b> {user.username}</li>
            <li className="list-group-item"><b>Email:</b> {user.email}</li>
            <li className="list-group-item"><b>Address:</b> {user.address || "No address provided"}</li>
          </ul>
        </div>

        <div className="d-flex gap-2 justify-content-center">
          <Link className="btn btn-secondary" to={"/HOME"}>Back to Home</Link>
          
          {/* Only show Verify button to Admin or the Profile Owner */}
          {(loggedInUser.role === "ADMIN" || loggedInUser.username === user.username) && (
            <button className="btn btn-info" onClick={() => setShowVerify(true)}>
              Verify Address
            </button>
          )}
        </div>
      </div>

      <VerifyAddressModal
        show={showVerify}
        onClose={() => setShowVerify(false)}
        user={user}
      />
    </div>
  );
}