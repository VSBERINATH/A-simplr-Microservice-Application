import "bootstrap/dist/css/bootstrap.min.css";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Navbar from "./layout/Navbar";
import Home from "./pages/Home";
import AddUser from "./users/AddUser";
import EditUser from "./users/EditUser";
import ViewUser from "./users/ViewUser";
import BulkUpload from "./pages/bulkupload";
import Login from "./pages/login";
function App() {
  return (
    <Router>
      <Navbar />
      <div className="container mt-3">
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/Home" element={<Home />} />
          <Route path="/users/add" element={<AddUser />} />
          <Route path="/users/:id/edit" element={<EditUser />} />
          <Route path="/users/:id" element={<ViewUser />} />
          <Route path="/users/bulk-upload" element={<BulkUpload />} />
          <Route path="/register" element={<AddUser />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;