import React, { useEffect, useState } from 'react';
import {
  Box,
  Button,
  Card,
  CardContent,
  FormControl,
  Grid,
  InputLabel,
  MenuItem,
  Select,
  TextField,
  Typography
} from '@mui/material';
import api from '../services/api.js';

const defaultTask = {
  titulo: '',
  descricao: '',
  prioridade: 'MEDIA',
  status: 'PENDENTE'
};

const TasksPage = () => {
  const [tasks, setTasks] = useState([]);
  const [filters, setFilters] = useState({ status: '', prioridade: '' });
  const [taskForm, setTaskForm] = useState(defaultTask);

  const loadTasks = () => {
    api
      .get('/tasks', {
        params: {
          status: filters.status || undefined,
          prioridade: filters.prioridade || undefined
        }
      })
      .then((response) => setTasks(response.data));
  };

  useEffect(() => {
    loadTasks();
  }, [filters]);

  const handleCreate = async (event) => {
    event.preventDefault();
    await api.post('/tasks', taskForm);
    setTaskForm(defaultTask);
    loadTasks();
  };

  const handleStatusChange = async (task, status) => {
    await api.put(`/tasks/${task.id}`, { ...task, status });
    loadTasks();
  };

  return (
    <Grid container spacing={3}>
      <Grid item xs={12} md={4}>
        <Card>
          <CardContent>
            <Typography variant="h6">Nova Tarefa</Typography>
            <Box component="form" onSubmit={handleCreate} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              <TextField
                label="Título"
                required
                value={taskForm.titulo}
                onChange={(event) => setTaskForm((prev) => ({ ...prev, titulo: event.target.value }))}
              />
              <TextField
                label="Descrição"
                multiline
                minRows={3}
                value={taskForm.descricao}
                onChange={(event) => setTaskForm((prev) => ({ ...prev, descricao: event.target.value }))}
              />
              <FormControl>
                <InputLabel>Prioridade</InputLabel>
                <Select
                  label="Prioridade"
                  value={taskForm.prioridade}
                  onChange={(event) => setTaskForm((prev) => ({ ...prev, prioridade: event.target.value }))}
                >
                  <MenuItem value="BAIXA">Baixa</MenuItem>
                  <MenuItem value="MEDIA">Média</MenuItem>
                  <MenuItem value="ALTA">Alta</MenuItem>
                </Select>
              </FormControl>
              <Button type="submit" variant="contained">
                Salvar
              </Button>
            </Box>
          </CardContent>
        </Card>
      </Grid>
      <Grid item xs={12} md={8}>
        <Card>
          <CardContent>
            <Typography variant="h6">Minhas tarefas</Typography>
            <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
              <FormControl sx={{ minWidth: 150 }}>
                <InputLabel>Status</InputLabel>
                <Select
                  value={filters.status}
                  label="Status"
                  onChange={(event) => setFilters((prev) => ({ ...prev, status: event.target.value }))}
                >
                  <MenuItem value="">Todos</MenuItem>
                  <MenuItem value="PENDENTE">Pendente</MenuItem>
                  <MenuItem value="EM_ANDAMENTO">Em andamento</MenuItem>
                  <MenuItem value="CONCLUIDA">Concluída</MenuItem>
                  <MenuItem value="ATRASADA">Atrasada</MenuItem>
                </Select>
              </FormControl>
              <FormControl sx={{ minWidth: 150 }}>
                <InputLabel>Prioridade</InputLabel>
                <Select
                  value={filters.prioridade}
                  label="Prioridade"
                  onChange={(event) => setFilters((prev) => ({ ...prev, prioridade: event.target.value }))}
                >
                  <MenuItem value="">Todas</MenuItem>
                  <MenuItem value="BAIXA">Baixa</MenuItem>
                  <MenuItem value="MEDIA">Média</MenuItem>
                  <MenuItem value="ALTA">Alta</MenuItem>
                </Select>
              </FormControl>
            </Box>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              {tasks.map((task) => (
                <Card key={task.id} variant="outlined">
                  <CardContent>
                    <Typography variant="subtitle1">{task.titulo}</Typography>
                    <Typography variant="body2" color="text.secondary">
                      {task.descricao}
                    </Typography>
                    <Box sx={{ display: 'flex', gap: 1, mt: 2 }}>
                      {['PENDENTE', 'EM_ANDAMENTO', 'CONCLUIDA', 'ATRASADA'].map((status) => (
                        <Button
                          key={status}
                          size="small"
                          variant={task.status === status ? 'contained' : 'outlined'}
                          onClick={() => handleStatusChange(task, status)}
                        >
                          {status}
                        </Button>
                      ))}
                    </Box>
                  </CardContent>
                </Card>
              ))}
              {tasks.length === 0 && <Typography>Nenhuma tarefa encontrada.</Typography>}
            </Box>
          </CardContent>
        </Card>
      </Grid>
    </Grid>
  );
};

export default TasksPage;
