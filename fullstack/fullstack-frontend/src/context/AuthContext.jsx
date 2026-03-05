import { createContext, useState, useContext } from 'react';
import axios from 'axios';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(JSON.parse(localStorage.getItem('user')) || null);

    const login = async (username, password) => {
        try {
            // 1. Call the new JWT login endpoint on the Gateway
            const response = await axios.post("http://localhost:8088/auth/login", {
                username,
                password
            });

            // 2. Extract token and user details from backend response
            const { token, user: userData } = response.data;

            // 3. Store the JWT token for the Axios interceptor
            localStorage.setItem('token', token);
            
            // 4. Store the user object for UI (Navbar/Roles)
            localStorage.setItem('user', JSON.stringify(userData));
            
            setUser(userData);
            return { success: true };
        } catch (error) {
            console.error("Login Error:", error.response?.data || error.message);
            return { 
                success: false, 
                message: error.response?.data || "Invalid credentials" 
            };
        }
    };

    const logout = () => {
        // Clear all security items
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, setUser, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};
 
export const useAuth = () => useContext(AuthContext);