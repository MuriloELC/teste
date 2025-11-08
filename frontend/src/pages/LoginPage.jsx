import React, { useState } from 'react';
import { Box, Button, Card, CardContent, TextField, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';

const LoginPage = () => {
  const { login } = useAuth();
  const [form, setForm] = useState({ email: '', senha: '' });

  const handleSubmit = async (event) => {
    event.preventDefault();
    await login(form);
  };

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', alignItems: 'center', justifyContent: 'center' }}>
      <Card sx={{ maxWidth: 400, width: '100%' }}>
        <CardContent>
          <Typography variant="h5" gutterBottom>
            Acessar
          </Typography>
          <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
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
              Entrar
            </Button>
            <Typography variant="body2">
              Não possui conta? <Link to="/register">Cadastre-se</Link>
            </Typography>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
};

export default LoginPage;
