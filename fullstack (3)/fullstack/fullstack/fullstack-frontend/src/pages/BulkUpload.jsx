import React, { useState } from "react";
import api from "../api/axiosConfig"; // Use our configured api
import { useNavigate } from "react-router-dom";

export default function BulkUpload() {
  const [file, setFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const navigate = useNavigate();

  const handleUpload = async () => {
    if (!file) return alert("Please select an Excel file.");

    const formData = new FormData();
    formData.append("file", file);

    setUploading(true);
    try {
      // The interceptor handles the Authorization header automatically!
      // Ensure the URL matches your backend: /user/upload-excel
      await api.post("/user/upload-excel", formData, {
        headers: { "Content-Type": "multipart/form-data" }
      });
      
      alert("Bulk upload successful!");
      navigate("/Home");
    } catch (err) {
      console.error("Upload failed", err);
      alert(err.response?.data || "Upload failed. Only Admins can perform this action.");
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="container mt-5">
      <div className="card shadow p-4 mx-auto" style={{ maxWidth: "500px" }}>
        <h4 className="mb-3">Bulk Upload via Excel</h4>
        <input 
          type="file" 
          className="form-control mb-3" 
          onChange={(e) => setFile(e.target.files[0])} 
          accept=".xlsx, .xls"
        />
        <div className="d-flex gap-2">
          <button 
            className="btn btn-success flex-grow-1" 
            onClick={handleUpload} 
            disabled={uploading}
          >
            {uploading ? "Processing..." : "Upload File"}
          </button>
          <Link className="btn btn-outline-secondary" to="/">Cancel</Link>
        </div>
      </div>
    </div>
  );
}