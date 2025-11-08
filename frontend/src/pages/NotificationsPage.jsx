import React from 'react';
import { Button, Card, CardContent, List, ListItem, ListItemText, Typography } from '@mui/material';
import { useNotifications } from '../context/NotificationContext.jsx';

const NotificationsPage = () => {
  const { notifications, markAsRead } = useNotifications();

  return (
    <Card>
      <CardContent>
        <Typography variant="h6">Notificações</Typography>
        <List>
          {notifications.map((notification) => (
            <ListItem key={notification.id} secondaryAction={!notification.lida && (
                <Button size="small" onClick={() => markAsRead(notification.id)}>
                  Marcar como lida
                </Button>
              )}>
              <ListItemText
                primary={notification.mensagem}
                secondary={`${notification.tipo} - ${new Date(notification.dataHoraCriacao).toLocaleString()}`}
              />
            </ListItem>
          ))}
          {notifications.length === 0 && <Typography>Sem notificações.</Typography>}
        </List>
      </CardContent>
    </Card>
  );
};

export default NotificationsPage;
