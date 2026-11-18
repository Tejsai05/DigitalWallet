import React, {useState} from 'react'
import { useNavigate } from 'react-router-dom'
import { login } from '../api/client'

export default function Login({onLoginSuccess}){
  const [phone, setPhone] = useState('')
  const [pin, setPin] = useState('')
  const [err, setErr] = useState('')
  const nav = useNavigate()

  const submit = async (e) => {
    e.preventDefault()
    setErr('')
    try {
      console.log('Logging in with:', { phoneNumber: phone, pin: '****' })
      const res = await login({ phoneNumber: phone, pin })
      console.log('Login response:', res)
      
      if(res && res.success){
        console.log('Login successful, storing user data')
        localStorage.setItem('user', JSON.stringify(res.data))
        
        // Call parent callback to update App state
        if (onLoginSuccess) {
          onLoginSuccess(res.data)
        }
        
        console.log('Navigating to dashboard')
        nav('/')
      } else {
        const error = res?.message || 'Login failed'
        console.error('Login error:', error)
        setErr(error)
      }
    } catch (error) {
      console.error('Login exception:', error)
      setErr(error.message || 'Network error. Make sure backend is running on port 8080')
    }
  }

  return (
    <div className="row justify-content-center">
      <div className="col-md-6">
        <h3>Login</h3>
        <form onSubmit={submit}>
          <div className="mb-3">
            <label className="form-label">Phone</label>
            <input className="form-control" value={phone} onChange={e=>setPhone(e.target.value)} required />
          </div>
          <div className="mb-3">
            <label className="form-label">PIN</label>
            <input className="form-control" type="password" value={pin} onChange={e=>setPin(e.target.value)} required />
          </div>
          {err && <div className="alert alert-danger">{err}</div>}
          <button className="btn btn-primary">Login</button>
        </form>
      </div>
    </div>
  )
}
