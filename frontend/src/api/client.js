const api = {
  post: async (path, body) => {
    try {
      const res = await fetch(`/api${path}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      })
      
      if (!res.ok) {
        console.error(`API Error (POST ${path}): Status ${res.status}`)
        return { success: false, message: `HTTP ${res.status}` }
      }
      
      const text = await res.text()
      if (!text) {
        return { success: false, message: 'Empty response from server' }
      }
      
      return JSON.parse(text)
    } catch (error) {
      console.error(`API Error (POST ${path}):`, error)
      return { success: false, message: error.message }
    }
  },
  get: async (path) => {
    try {
      const res = await fetch(`/api${path}`)
      
      if (!res.ok) {
        console.error(`API Error (GET ${path}): Status ${res.status}`)
        return { success: false, message: `HTTP ${res.status}` }
      }
      
      const text = await res.text()
      if (!text) {
        return { success: false, message: 'Empty response from server' }
      }
      
      return JSON.parse(text)
    } catch (error) {
      console.error(`API Error (GET ${path}):`, error)
      return { success: false, message: error.message }
    }
  }
}

export const register = (data) => api.post('/users/register', data)
export const login = (data) => api.post('/users/login', data)
export const getProfile = (upiId) => api.get(`/users/profile/${upiId}`)
export const getWallet = (upiId) => api.get(`/wallet/${upiId}`)
export const addMoney = (data) => api.post('/wallet/add', data)
export const sendMoney = (data) => api.post('/transaction/send', data)
export const getHistory = (upiId) => api.get(`/transaction/history/${upiId}`)

export default api
