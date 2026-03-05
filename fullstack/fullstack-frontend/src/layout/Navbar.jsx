import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Navbar() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  // const user = JSON.parse(localStorage.getItem("user"));

  const onLogout = () => {
    logout();
    navigate("/");
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark px-3 shadow">
      <div className="container-fluid">
        <Link className="navbar-brand" to={user ? "/Home" : "/"}>
          User Manager
        </Link>
        <div className="collapse navbar-collapse">
          <ul className="navbar-nav me-auto">
            {user && (
              <>
                <li className="nav-item">
                  <Link className="nav-link" to="/Home">Home</Link>
                </li>
                {user.role === "ADMIN" && (
                  <li className="nav-item">
                    <Link className="nav-link" to="/users/bulk-upload">Bulk Upload</Link>
                  </li>
                )}
              </>
            )}
          </ul>
          {user && (
            <div className="d-flex align-items-center gap-3">
              <span className="text-light">
                Logged in as: <b>{user.username}</b> ({user.role})
              </span>
              <button className="btn btn-outline-danger btn-sm" onClick={onLogout}>
                Logout
              </button>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
}