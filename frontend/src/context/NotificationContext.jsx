import React, { createContext, useContext, useEffect, useMemo, useRef, useState } from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import api from '../services/api.js';
import { useAuth } from './AuthContext.jsx';

const NotificationContext = createContext();

export const NotificationProvider = ({ children }) => {
  const { user, token } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const clientRef = useRef(null);

  useEffect(() => {
    if (!token) {
      setNotifications([]);
      if (clientRef.current) {
        clientRef.current.disconnect();
        clientRef.current = null;
      }
      return;
    }
    api.get('/notifications').then((response) => setNotifications(response.data));
    const baseUrl = import.meta.env.VITE_API_URL || '';
    const socket = new SockJS(`${baseUrl}/ws`);
    const stompClient = Stomp.over(socket);
    stompClient.debug = () => {};
    stompClient.connect({ Authorization: `Bearer ${token}` }, () => {
      if (user) {
        stompClient.subscribe(`/topic/notifications/${user.id}`, (message) => {
          const body = JSON.parse(message.body);
          setNotifications((prev) => [body, ...prev]);
        });
      }
    });
    clientRef.current = stompClient;
    return () => {
      stompClient.disconnect();
      clientRef.current = null;
    };
  }, [token, user?.id]);

  const markAsRead = async (id) => {
    await api.post(`/notifications/${id}/read`);
    setNotifications((prev) => prev.map((n) => (n.id === id ? { ...n, lida: true } : n)));
  };

  const value = useMemo(
    () => ({ notifications, unread: notifications.filter((n) => !n.lida).length, markAsRead }),
    [notifications]
  );

  return <NotificationContext.Provider value={value}>{children}</NotificationContext.Provider>;
};

export const useNotifications = () => useContext(NotificationContext);
