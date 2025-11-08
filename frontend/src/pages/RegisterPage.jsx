import React, { useState } from 'react';
import { Box, Button, Card, CardContent, TextField, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';

const RegisterPage = () => {
  const { register } = useAuth();
  const [form, setForm] = useState({ nome: '', email: '', senha: '' });

  const handleSubmit = async (event) => {
    event.preventDefault();
    await register(form);
  };

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', alignItems: 'center', justifyContent: 'center' }}>
      <Card sx={{ maxWidth: 400, width: '100%' }}>
        <CardContent>
          <Typography variant="h5" gutterBottom>
            Criar conta
          </Typography>
          <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            <TextField
              label="Nome"
              required
              value={form.nome}
              onChange={(event) => setForm((prev) => ({ ...prev, nome: event.target.value }))}
            />
            <TextField
              label="Email"
              type="email"
              required
              value={form.email}
              onChange={(event) => setForm((prev) => ({ ...prev, email: event.target.value }))}
            />
            <TextField
              label="Senha"
              type="password"
              required
              value={form.senha}
              onChange={(event) => setForm((prev) => ({ ...prev, senha: event.target.value }))}
            />
            <Button type="submit" variant="contained">
              Registrar
            </Button>
            <Typography variant="body2">
              Já possui conta? <Link to="/login">Acesse</Link>
            </Typography>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
};

export default RegisterPage;
