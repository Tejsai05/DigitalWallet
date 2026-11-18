import React, {useState} from 'react'
import { useNavigate } from 'react-router-dom'
import { register } from '../api/client'

export default function Register(){
  const [form, setForm] = useState({name:'', phoneNumber:'', upiId:'', pin:''})
  const [msg, setMsg] = useState('')
  const nav = useNavigate()

  const submit = async (e) => {
    e.preventDefault()
    setMsg('')
    try {
      console.log('Registering with:', {...form, pin: '****'})
      const res = await register(form)
      console.log('Register response:', res)
      
      if(res && res.success){
        setMsg('Registered successfully. Redirecting to login...')
        setTimeout(()=>nav('/login'), 1500)
      } else {
        const error = res?.message || 'Registration failed'
        console.error('Register error:', error)
        setMsg(error)
      }
    } catch (error) {
      console.error('Register exception:', error)
      setMsg(error.message || 'Network error. Make sure backend is running on port 8080')
    }
  }

  return (
    <div className="row justify-content-center">
      <div className="col-md-6">
        <h3>Register</h3>
        <form onSubmit={submit}>
          <div className="mb-2"><input className="form-control" placeholder="Name" value={form.name} onChange={e=>setForm({...form,name:e.target.value})} required /></div>
          <div className="mb-2"><input className="form-control" placeholder="Phone" value={form.phoneNumber} onChange={e=>setForm({...form,phoneNumber:e.target.value})} required /></div>
          <div className="mb-2"><input className="form-control" placeholder="UPI ID" value={form.upiId} onChange={e=>setForm({...form,upiId:e.target.value})} required /></div>
          <div className="mb-3"><input className="form-control" type="password" placeholder="PIN" value={form.pin} onChange={e=>setForm({...form,pin:e.target.value})} required /></div>
          {msg && <div className="alert alert-info">{msg}</div>}
          <button className="btn btn-primary">Register</button>
        </form>
      </div>
    </div>
  )
}
