
import React, { useState } from "react";
import axios from "axios";
import api from "../api/axiosConfig";

/**
 * Simple modal to verify a user's address against a pincode.
 *
 * Props:
 * - show: boolean
 * - onClose: () => void
 * - user: { id, name, address }  // minimal fields used in the modal
 * - onVerified: (result) => void // optional callback to bubble up the result (matched / apiAddress)
 */
export default function VerifyAddressModal({ show, onClose, user, onVerified }) {
  const [pincode, setPincode] = useState("");
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null); // { pincode, apiAddress, userAddress, matched }
  const [error, setError] = useState(null);

  if (!show) return null;

// Inside VerifyAddress.jsx
const verify = async () => {
  setLoading(true);
  setError(null);    // Clear old errors
  setResult(null);   // Clear old results
  
  try {
    const res = await api.get(`/user/${user.id}/verify-pincode?pincode=${pincode}`);
    const data = res.data;

    // 1. Check if the Circuit Breaker returned the fallback message
    if (data.apiAddress && data.apiAddress.includes("Service Temporary Unavailable")) {
      setError("Postal service is currently undergoing maintenance. Please try again later.");
      // Optional: still show the result so they see the maintenance status
      setResult(data); 
    } 
    // 2. Normal case: service is up
    else {
      setResult(data);
      if (!data.matched) {
        setError("Address verification failed. The pincode does not match your address.");
      }
    }
  } catch (err) {
    // 3. Actual HTTP Errors (401, 403, 500)
    if (err.response && (err.response.status === 401 || err.response.status === 403)) {
      setError("Session expired. Please log in again.");
    } else {
      setError("An unexpected network error occurred.");
    }
  } finally {
    setLoading(false);
  }
};

  return (
    <div className="modal d-block" tabIndex="-1" style={{ backgroundColor: "rgba(0,0,0,0.35)" }}>
      <div className="modal-dialog">
        <div className="modal-content">

          <div className="modal-header">
            <h5 className="modal-title">Verify Address</h5>
            <button type="button" className="btn-close" onClick={onClose} />
          </div>

          <div className="modal-body">
            {/* User info */}
            <div className="mb-3">
              <div><strong>User:</strong> {user?.name || `#${user?.id}`}</div>
              <div><strong>Address:</strong> {user?.address || "-"}</div>
            </div>

            {/* Pincode input */}
            <div className="mb-3">
              <label className="form-label">Pincode</label>
              <input
                className="form-control"
                placeholder="Enter 6-digit pincode"
                value={pincode}
                onChange={(e) => setPincode(e.target.value)}
                maxLength={6}
              />
              <div className="form-text">We’ll verify this address against the pincode’s district/state.</div>
            </div>

            {/* Error */}
            {error && <div className="alert alert-danger">{error}</div>}

            {/* Result */}
            {result && (
              <div className="card">
                <div className="card-body">
                  <div className="d-flex align-items-center justify-content-between">
                    <h6 className="mb-0">Verification Result</h6>
                    <span className={`badge ${result.matched ? "bg-success" : "bg-danger"}`}>
                      {result.matched ? "Matched" : "Not matched"}
                    </span>
                  </div>
                  <hr className="my-2" />
                  <div><strong>Pincode:</strong> {result.pincode}</div>
                  <div><strong>Postal (API):</strong> {result.apiAddress}</div>
                  <div><strong>User Address:</strong> {result.userAddress}</div>
                </div>
              </div>
            )}
          </div>

          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={close}>Close</button>
            <button type="button" className="btn btn-primary" onClick={verify} disabled={loading}>
              {loading ? "Verifying..." : "Verify"}
            </button>
          </div>

        </div>
      </div>
    </div>
  );
}
