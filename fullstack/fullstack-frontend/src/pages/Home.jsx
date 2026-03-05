import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";
import VerifyAddressModal from "../users/VerifyAddress"; // Ensure this path is correct
import api from "../api/axiosConfig";

export default function Home() {
  const [users, setUsers] = useState([]);
  const [verifyUser, setVerifyUser] = useState(null); // State for the modal
  const navigate = useNavigate();
  const loggedInUser = JSON.parse(localStorage.getItem("user"));

  useEffect(() => {
    if (!loggedInUser) {
      navigate("/");
    } else {
      loadUsers();
    }
  }, []);

  const loadUsers = async () => {
    try {
      const result = await api.get("/users"); 
    setUsers(result.data.content || result.data);
    } catch (err) {
      console.error("Failed to load users", err);
    }
  };

  const deleteUser = async (id) => {
    if (window.confirm("Are you sure you want to delete this user?")) {
      try {
        await api.delete(`http://localhost:8088/user/${id}`);
        loadUsers();
      } catch (err) {
        alert("Action failed: Only Admins can delete users.");
      }
    }
  };

  return (
    <div className="container-form">
      <h2 className="page-title">User Management</h2>
      <p className="page-subtitle">View, edit, or verify user details and addresses.</p>
      <hr className="hr-soft" />

      <div className="card-shell">
        <table className="table table-modern">
          <thead>
            <tr>
              <th scope="col">S.N</th>
              <th scope="col">Name</th>
              <th scope="col">Username</th>
              <th scope="col">Email</th>
              <th scope="col" className="text-center">Action</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user, index) => (
              <tr key={user.id}>
                <th scope="row">{index + 1}</th>
                <td>{user.name}</td>
                <td>{user.username}</td>
                <td>{user.email}</td>
                <td>
                  <div className="btn-set justify-content-center">
                    <Link className="btn btn-primary" to={`/users/${user.id}`}>View</Link>

                    {/* Verify Condition: Admin or Self */}
                    {(loggedInUser.role === "ADMIN" || loggedInUser.username === user.username) && (
                      <button 
                        className="btn btn-outline-info" 
                        onClick={() => setVerifyUser(user)}
                      >
                        Verify Address
                      </button>
                    )}

                    {/* Edit Condition: Admin or Self */}
                    {(loggedInUser.role === "ADMIN" || loggedInUser.username === user.username) && (
                      <Link className="btn btn-outline-primary" to={`/users/${user.id}/edit`}>Edit</Link>
                    )}

                    {/* Delete Condition: Admin Only */}
                    {loggedInUser.role === "ADMIN" && (
                      <button className="btn btn-danger" onClick={() => deleteUser(user.id)}>Delete</button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Verification Modal Popup */}
      {verifyUser && (
        <VerifyAddressModal
          show={!!verifyUser}
          onClose={() => setVerifyUser(null)}
          user={verifyUser}
        />
      )}
    </div>
  );
}