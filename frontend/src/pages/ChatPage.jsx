import React, { useEffect, useRef, useState } from 'react';
import { Box, Button, Card, CardContent, MenuItem, Select, TextField, Typography } from '@mui/material';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import api from '../services/api.js';
import { useAuth } from '../context/AuthContext.jsx';

const ChatPage = () => {
  const { user, token } = useAuth();
  const [canal, setCanal] = useState('geral');
  const [meetingId, setMeetingId] = useState('');
  const [mensagem, setMensagem] = useState('');
  const [mensagens, setMensagens] = useState([]);
  const stompClient = useRef(null);

  const canalCompleto = canal === 'personalizado' ? (meetingId ? `meeting-${meetingId}` : 'geral') : canal;

  useEffect(() => {
    if (canal !== 'personalizado') {
      setMeetingId('');
    }
  }, [canal]);

  useEffect(() => {
    api.get('/chat', { params: { canal: canalCompleto } }).then((response) => setMensagens(response.data));
  }, [canalCompleto]);

  useEffect(() => {
    if (!token) {
      return;
    }
    const baseUrl = import.meta.env.VITE_API_URL || '';
    const socket = new SockJS(`${baseUrl}/ws`);
    stompClient.current = Stomp.over(socket);
    stompClient.current.debug = () => {};
    stompClient.current.connect({ Authorization: `Bearer ${token}` }, () => {
      stompClient.current.subscribe(`/topic/chat/${canalCompleto}`, (message) => {
        const body = JSON.parse(message.body);
        setMensagens((prev) => [...prev, body]);
      });
    });
    return () => {
      stompClient.current?.disconnect();
      stompClient.current = null;
    };
  }, [token, canalCompleto]);

  const enviarMensagem = async (event) => {
    event.preventDefault();
    const payload = {
      remetenteId: user.id,
      canal: canalCompleto,
      meetingId: meetingId ? Number(meetingId) : null,
      conteudo: mensagem
    };
    await api.post('/chat', payload);
    setMensagem('');
  };

  return (
    <Card>
      <CardContent>
        <Typography variant="h6">Chat em tempo real</Typography>
        <Box component="form" onSubmit={enviarMensagem} sx={{ display: 'flex', gap: 2, my: 2 }}>
          <Select value={canal} onChange={(event) => setCanal(event.target.value)}>
            <MenuItem value="geral">Canal geral</MenuItem>
            <MenuItem value="personalizado">Canal por reunião</MenuItem>
          </Select>
          {canal === 'personalizado' && (
            <TextField
              label="ID da reunião"
              value={meetingId}
              onChange={(event) => setMeetingId(event.target.value)}
            />
          )}
          <TextField
            fullWidth
            value={mensagem}
            onChange={(event) => setMensagem(event.target.value)}
            placeholder="Digite sua mensagem"
          />
          <Button type="submit" variant="contained">
            Enviar
          </Button>
        </Box>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1, maxHeight: 400, overflow: 'auto' }}>
          {mensagens.map((m) => (
            <Box key={m.id} sx={{ p: 1, borderRadius: 1, backgroundColor: '#f0f4f8' }}>
              <Typography variant="subtitle2">{m.remetenteNome || 'Usuário'}</Typography>
              <Typography variant="body2">{m.conteudo}</Typography>
            </Box>
          ))}
        </Box>
      </CardContent>
    </Card>
  );
};

export default ChatPage;
