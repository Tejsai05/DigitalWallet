import React, { useState, useEffect } from 'react'
import { Routes, Route, Link, Navigate, useNavigate } from 'react-router-dom'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'

function App(){
  const [user, setUser] = useState(null)
  const nav = useNavigate()

  useEffect(() => {
    // Load user from localStorage on mount
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      try {
        setUser(JSON.parse(storedUser))
      } catch (e) {
        console.error('Error parsing user from localStorage:', e)
        localStorage.removeItem('user')
      }
    }
    
    // Listen for storage changes (logout from another tab)
    const handleStorageChange = (e) => {
      if (e.key === 'user') {
        if (e.newValue) {
          try {
            setUser(JSON.parse(e.newValue))
          } catch (err) {
            console.error('Error parsing user:', err)
          }
        } else {
          setUser(null)
        }
      }
    }
    
    window.addEventListener('storage', handleStorageChange)
    return () => window.removeEventListener('storage', handleStorageChange)
  }, [])

  const handleLogout = () => {
    console.log('Logging out...')
    localStorage.removeItem('user')
    setUser(null)
    nav('/login', { replace: true })
  }

  return (
    <div>
      <nav className="navbar navbar-expand-lg navbar-dark bg-primary">
        <div className="container">
          <Link className="navbar-brand" to="/">Phonepe Wallet</Link>
          <div className="collapse navbar-collapse">
            <ul className="navbar-nav ms-auto">
              {user ? (
                <li className="nav-item">
                  <a className="nav-link" href="#" onClick={(e) => {e.preventDefault(); handleLogout();}}>Logout</a>
                </li>
              ) : (
                <>
                  <li className="nav-item"><Link className="nav-link" to="/login">Login</Link></li>
                  <li className="nav-item"><Link className="nav-link" to="/register">Register</Link></li>
                </>
              )}
            </ul>
          </div>
        </div>
      </nav>

      <div className="container mt-4">
        <Routes>
          <Route path="/" element={user ? <Dashboard user={user} /> : <Navigate to="/login" replace />} />
          <Route path="/login" element={<Login onLoginSuccess={(userData) => setUser(userData)} />} />
          <Route path="/register" element={<Register />} />
        </Routes>
      </div>
    </div>
  )
}

export default App
