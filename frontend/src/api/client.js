const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const apiClient = async (endpoint, options = {}) => {
  const url = `${API_BASE_URL}${endpoint}`;
  
  const token = localStorage.getItem('auth_token');
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {}),
  };

  if (token && !endpoint.includes('/api/auth/register')) {
    headers['Authorization'] = `Basic ${token}`;
  }

  try {
    const response = await fetch(url, {
      ...options,
      headers,
    });

    if (!response.ok) {
      // Try to parse JSON error message from backend
      let errorData;
      try {
        errorData = await response.json();
      } catch (e) {
        errorData = { message: 'An unexpected error occurred' };
      }
      const error = new Error(errorData.message || 'API error');
      error.status = response.status;
      error.data = errorData;
      throw error;
    }

    // Return empty object for 204 No Content
    if (response.status === 204) {
      return {};
    }

    return response.json();
  } catch (error) {
    if (error.name === 'TypeError' && error.message.includes('fetch')) {
      throw new Error('Unable to connect to the FraudWatch server. Please make sure the backend is running.');
    }
    throw error;
  }
};
