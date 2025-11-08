import React, { createContext, useContext, useEffect, useMemo, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api.js';

// Context API is suficiente para o escopo atual, mantendo a implementação simples
const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(() => localStorage.getItem('token'));
  const navigate = useNavigate();

  const login = async (credentials) => {
    const { data } = await api.post('/auth/login', credentials);
    setToken(data.token);
    api.setToken(data.token);
    localStorage.setItem('token', data.token);
    setUser(data.user);
    navigate('/');
  };

  const register = async (payload) => {
    await api.post('/auth/register', payload);
    await login({ email: payload.email, senha: payload.senha });
  };

  const logout = useCallback(() => {
    setToken(null);
    api.setToken(null);
    setUser(null);
    localStorage.removeItem('token');
    navigate('/login');
  }, [navigate]);

  useEffect(() => {
    api.setToken(token);
    if (token) {
      api.get('/users/me')
        .then((response) => setUser(response.data))
        .catch(() => {
          logout();
        });
    }
  }, [token, logout]);

  const value = useMemo(() => ({ user, token, login, logout, register }), [user, token, logout]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => useContext(AuthContext);
