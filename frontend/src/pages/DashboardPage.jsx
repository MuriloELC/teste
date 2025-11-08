import React, { useEffect, useState } from 'react';
import { Card, CardContent, Grid, List, ListItem, ListItemText, Typography } from '@mui/material';
import api from '../services/api.js';
import { useNotifications } from '../context/NotificationContext.jsx';

const DashboardPage = () => {
  const [tasks, setTasks] = useState([]);
  const [meetings, setMeetings] = useState([]);
  const { notifications } = useNotifications();

  useEffect(() => {
    api.get('/tasks', { params: { status: 'PENDENTE' } }).then((response) => setTasks(response.data));
    api.get('/meetings').then((response) => setMeetings(response.data));
  }, []);

  return (
    <Grid container spacing={3}>
      <Grid item xs={12} md={6}>
        <Card>
          <CardContent>
            <Typography variant="h6">Próximas Reuniões</Typography>
            <List>
              {meetings.slice(0, 5).map((meeting) => (
                <ListItem key={meeting.id}>
                  <ListItemText primary={meeting.titulo} secondary={meeting.dataHoraInicio} />
                </ListItem>
              ))}
              {meetings.length === 0 && <Typography>Nenhuma reunião agendada.</Typography>}
            </List>
          </CardContent>
        </Card>
      </Grid>
      <Grid item xs={12} md={6}>
        <Card>
          <CardContent>
            <Typography variant="h6">Tarefas Pendentes</Typography>
            <List>
              {tasks.slice(0, 5).map((task) => (
                <ListItem key={task.id}>
                  <ListItemText primary={task.titulo} secondary={task.prioridade} />
                </ListItem>
              ))}
              {tasks.length === 0 && <Typography>Nenhuma tarefa pendente.</Typography>}
            </List>
          </CardContent>
        </Card>
      </Grid>
      <Grid item xs={12}>
        <Card>
          <CardContent>
            <Typography variant="h6">Notificações Recentes</Typography>
            <List>
              {notifications.slice(0, 5).map((notification) => (
                <ListItem key={notification.id}>
                  <ListItemText primary={notification.mensagem} secondary={notification.tipo} />
                </ListItem>
              ))}
              {notifications.length === 0 && <Typography>Sem notificações.</Typography>}
            </List>
          </CardContent>
        </Card>
      </Grid>
    </Grid>
  );
};

export default DashboardPage;
