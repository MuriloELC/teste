import React from 'react';
import { AppBar, Badge, Box, Button, Container, Toolbar, Typography } from '@mui/material';
import { Link, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import { useNotifications } from '../context/NotificationContext.jsx';

const AppLayout = () => {
  const { user, logout } = useAuth();
  const { unread } = useNotifications();

  return (
    <Box sx={{ minHeight: '100vh', backgroundColor: '#f7f9fb' }}>
      <AppBar position="static">
        <Toolbar>
          <Typography
            variant="h6"
            sx={{ flexGrow: 1, textDecoration: 'none' }}
            component={Link}
            to="/"
            color="inherit"
          >
            Planner
          </Typography>
          {user && (
            <>
              <Button color="inherit" component={Link} to="/tasks">
                Tarefas
              </Button>
              <Button color="inherit" component={Link} to="/meetings">
                Reuniões
              </Button>
              <Button color="inherit" component={Link} to="/chat">
                Chat
              </Button>
              <Button color="inherit" component={Link} to="/notifications">
                <Badge color="secondary" badgeContent={unread} max={99}>
                  Notificações
                </Badge>
              </Button>
              <Button color="inherit" onClick={logout}>
                Sair
              </Button>
            </>
          )}
        </Toolbar>
      </AppBar>
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Outlet />
      </Container>
    </Box>
  );
};

export default AppLayout;
