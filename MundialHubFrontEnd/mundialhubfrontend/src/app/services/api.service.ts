import axios from 'axios';
import { environment } from '../../environments/environment';

const api = axios.create({
  baseURL: environment.apiBaseUrl,
  headers: {
    'Content-Type': 'application/json'
  }
});

api.interceptors.request.use((config) => {
  if (typeof window !== 'undefined') {
    const token = localStorage.getItem('mundialhub_token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  }
  return config;
});

export const ApiService = {
  login: (username: string, password: string) => api.post('/auth/login', { username, password }),
  register: (payload: Record<string, unknown>) => api.post('/auth/register', payload),
  getTeams: () => api.get('/team/getall'),
  getMatches: () => api.get('/match/getall'),
  getAlbum: () => api.post('/album/view', { section: null }),
  getCommunities: () => api.get('/communities/mine'),
  getPolls: () => api.get('/polls/mine'),
  
  // Admin endpoints
  getAllUsers: () => api.get('/user/getall'),
  createUser: (user: Record<string, unknown>) => api.post('/user/create', user),
  deleteUser: (id: number) => api.delete(`/user/deletebyid/${id}`),
  updateUser: (user: Record<string, unknown>) => api.put('/user/updatebyemailbyjson', {
    oldOne: { email: user.email },
    newOne: user
  }),
  getUserByEmail: (email: string) => api.post('/user/getaccountbyemail', { email })
};
