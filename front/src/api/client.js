const API_BASE = '/api'

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  })

  if (response.status === 204) {
    return null
  }

  const data = await response.json()
  if (!response.ok) {
    throw new Error(data.error || '요청 처리 중 오류가 발생했습니다.')
  }
  return data
}

export const api = {
  login: (email, password) =>
    request('/login', { method: 'POST', body: JSON.stringify({ email, password }) }),
  signup: (email, password, name) =>
    request('/signup', { method: 'POST', body: JSON.stringify({ email, password, name }) }),
  logout: () => request('/logout', { method: 'POST' }),
  getFunds: () => request('/funds'),
  getFund: (id) => request(`/funds?id=${id}`),
  toggleWatchlist: (fundId) =>
    request('/watchlist/toggle', { method: 'POST', body: JSON.stringify({ fundId }) }),
  getApplications: () => request('/applications'),
  applyFund: (fundId) =>
    request('/applications', { method: 'POST', body: JSON.stringify({ fundId }) }),
  getAlerts: () => request('/alerts'),
  getReports: (fundId) => request(`/alerts?fundId=${fundId}`),
}
