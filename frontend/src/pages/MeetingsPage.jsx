import React, { useEffect, useState } from 'react';
import {
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Grid,
  Stack,
  TextField,
  Typography
} from '@mui/material';
import api from '../services/api.js';

const MeetingsPage = () => {
  const [meetings, setMeetings] = useState([]);
  const [form, setForm] = useState({
    titulo: '',
    descricao: '',
    dataHoraInicio: '',
    dataHoraFim: '',
    organizadorId: '',
    participantesIds: ''
  });
  const [suggestionForm, setSuggestionForm] = useState({ participantesIds: '', inicioJanela: '', fimJanela: '', duracaoMinutos: 60 });
  const [suggestions, setSuggestions] = useState([]);

  const loadMeetings = () => {
    api.get('/meetings').then((response) => setMeetings(response.data));
  };

  useEffect(() => {
    loadMeetings();
  }, []);

  const parseIds = (value) =>
    value
      .split(',')
      .map((id) => id.trim())
      .filter(Boolean)
      .map(Number);

  const handleCreate = async (event) => {
    event.preventDefault();
    await api.post('/meetings', {
      ...form,
      dataHoraInicio: new Date(form.dataHoraInicio).toISOString(),
      dataHoraFim: new Date(form.dataHoraFim).toISOString(),
      organizadorId: Number(form.organizadorId),
      participantesIds: parseIds(form.participantesIds)
    });
    setForm({ titulo: '', descricao: '', dataHoraInicio: '', dataHoraFim: '', organizadorId: '', participantesIds: '' });
    loadMeetings();
  };

  const requestSuggestions = async () => {
    if (!suggestionForm.participantesIds || !suggestionForm.inicioJanela || !suggestionForm.fimJanela) {
      return;
    }
    const payload = {
      participantesIds: parseIds(suggestionForm.participantesIds),
      inicioJanela: new Date(suggestionForm.inicioJanela).toISOString(),
      fimJanela: new Date(suggestionForm.fimJanela).toISOString(),
      duracaoMinutos: Number(suggestionForm.duracaoMinutos)
    };
    const { data } = await api.post('/meetings/suggestions', payload);
    setSuggestions(data);
  };

  const handleCancel = async (id) => {
    await api.post(`/meetings/${id}/cancel`);
    loadMeetings();
  };

  return (
    <Grid container spacing={3}>
      <Grid item xs={12} md={5}>
        <Card>
          <CardContent>
            <Typography variant="h6">Nova reunião</Typography>
            <Box component="form" onSubmit={handleCreate} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              <TextField
                label="Título"
                required
                value={form.titulo}
                onChange={(event) => setForm((prev) => ({ ...prev, titulo: event.target.value }))}
              />
              <TextField
                label="Descrição"
                multiline
                minRows={2}
                value={form.descricao}
                onChange={(event) => setForm((prev) => ({ ...prev, descricao: event.target.value }))}
              />
              <TextField
                label="Organizador (ID)"
                required
                value={form.organizadorId}
                onChange={(event) => setForm((prev) => ({ ...prev, organizadorId: event.target.value }))}
              />
              <TextField
                label="Participantes (IDs separados por vírgula)"
                value={form.participantesIds}
                onChange={(event) => setForm((prev) => ({ ...prev, participantesIds: event.target.value }))}
              />
              <TextField
                label="Início"
                type="datetime-local"
                required
                value={form.dataHoraInicio}
                onChange={(event) => setForm((prev) => ({ ...prev, dataHoraInicio: event.target.value }))}
              />
              <TextField
                label="Fim"
                type="datetime-local"
                required
                value={form.dataHoraFim}
                onChange={(event) => setForm((prev) => ({ ...prev, dataHoraFim: event.target.value }))}
              />
              <Button type="submit" variant="contained">
                Criar reunião
              </Button>
            </Box>
          </CardContent>
        </Card>
      </Grid>
      <Grid item xs={12} md={7}>
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Typography variant="h6">Assistente de agendamento</Typography>
            <Stack spacing={2} direction="row" sx={{ flexWrap: 'wrap' }}>
              <TextField
                label="Participantes"
                value={suggestionForm.participantesIds}
                onChange={(event) => setSuggestionForm((prev) => ({ ...prev, participantesIds: event.target.value }))}
              />
              <TextField
                label="Janela inicial"
                type="datetime-local"
                value={suggestionForm.inicioJanela}
                onChange={(event) => setSuggestionForm((prev) => ({ ...prev, inicioJanela: event.target.value }))}
              />
              <TextField
                label="Janela final"
                type="datetime-local"
                value={suggestionForm.fimJanela}
                onChange={(event) => setSuggestionForm((prev) => ({ ...prev, fimJanela: event.target.value }))}
              />
              <TextField
                label="Duração (min)"
                type="number"
                value={suggestionForm.duracaoMinutos}
                onChange={(event) => setSuggestionForm((prev) => ({ ...prev, duracaoMinutos: event.target.value }))}
              />
              <Button variant="outlined" onClick={requestSuggestions}>
                Gerar sugestões
              </Button>
            </Stack>
            <Stack direction="row" spacing={1} sx={{ mt: 2, flexWrap: 'wrap' }}>
              {suggestions.map((suggestion, index) => (
                <Chip
                  key={index}
                  label={`${new Date(suggestion.dataHoraInicio).toLocaleString()} (${suggestion.pontuacao.toFixed(2)})`}
                />
              ))}
              {suggestions.length === 0 && <Typography>Nenhuma sugestão gerada ainda.</Typography>}
            </Stack>
          </CardContent>
        </Card>
        <Card>
          <CardContent>
            <Typography variant="h6">Minhas reuniões</Typography>
            <Stack spacing={2}>
              {meetings.map((meeting) => (
                <Card key={meeting.id} variant="outlined">
                  <CardContent>
                    <Typography variant="subtitle1">{meeting.titulo}</Typography>
                    <Typography variant="body2" color="text.secondary">
                      {meeting.descricao}
                    </Typography>
                    <Typography variant="body2">Status: {meeting.status}</Typography>
                    <Typography variant="body2">Início: {meeting.dataHoraInicio}</Typography>
                    <Button sx={{ mt: 1 }} size="small" variant="outlined" onClick={() => handleCancel(meeting.id)}>
                      Cancelar
                    </Button>
                  </CardContent>
                </Card>
              ))}
              {meetings.length === 0 && <Typography>Nenhuma reunião encontrada.</Typography>}
            </Stack>
          </CardContent>
        </Card>
      </Grid>
    </Grid>
  );
};

export default MeetingsPage;
