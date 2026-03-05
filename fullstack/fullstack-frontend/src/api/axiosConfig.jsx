import axios from 'axios';

const API_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8088';

const api = axios.create({
    baseURL: API_URL,
});

// This interceptor automatically adds the Auth header if credentials exist
api.interceptors.request.use((config) => {
//     const authData = localStorage.getItem('authData'); // We will store 'Basic <hash>' here
//     if (authData) {
//         config.headers.Authorization = authData;
//     }
//     return config;
// }, (error) => {
//     return Promise.reject(error);
// });
const token = localStorage.getItem('token'); 
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

export default api;