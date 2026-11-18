import React, {useEffect, useState} from 'react'
import { getWallet, addMoney, sendMoney, getHistory, getProfile } from '../api/client'

function WalletView({user, wallet, onAdd}){
  const [amt, setAmt] = useState('')
  return (
    <div className="card mb-3">
      <div className="card-body">
        <h5>Wallet for {user.upiId}</h5>
        <p className="fs-4">Balance: {wallet?.balance ?? '—'}</p>
        <div className="input-group">
          <input className="form-control" value={amt} onChange={e=>setAmt(e.target.value)} placeholder="Amount" />
          <button className="btn btn-success" onClick={()=>{onAdd(Number(amt)); setAmt('')}}>Add Money</button>
        </div>
      </div>
    </div>
  )
}

function SendForm({onSend}){
  const [to, setTo] = useState('')
  const [amount, setAmount] = useState('')
  return (
    <div className="card mb-3">
      <div className="card-body">
        <h5>Send Money</h5>
        <div className="mb-2"><input className="form-control" placeholder="Receiver UPI" value={to} onChange={e=>setTo(e.target.value)} /></div>
        <div className="mb-2"><input className="form-control" placeholder="Amount" value={amount} onChange={e=>setAmount(e.target.value)} /></div>
        <button className="btn btn-primary" onClick={()=>{onSend({receiverUpiId:to, amount: Number(amount)}); setTo(''); setAmount('')}}>Send</button>
      </div>
    </div>
  )
}

export default function Dashboard({user}){
  const [wallet, setWallet] = useState(null)
  const [history, setHistory] = useState([])
  const [profile, setProfile] = useState(null)
  const [msg, setMsg] = useState('')

  const load = async ()=>{
    try {
      console.log('Loading dashboard data for:', user)
      const w = await getWallet(user.upiId)
      if(w?.success) setWallet(w.data)
      else console.warn('Wallet fetch failed:', w?.message)
      
      const h = await getHistory(user.upiId)
      if(h?.success) setHistory(h.data || [])
      else console.warn('History fetch failed:', h?.message)
      
      const p = await getProfile(user.upiId)
      if(p?.success) setProfile(p.data)
      else console.warn('Profile fetch failed:', p?.message)
    } catch (error) {
      console.error('Dashboard load error:', error)
      setMsg('Error loading dashboard: ' + error.message)
    }
  }

  useEffect(()=>{ load() }, [user])

  const handleAdd = async (amount) => {
    if(!amount || amount <= 0) {
      setMsg('Please enter a valid amount')
      return
    }
    const res = await addMoney({ upiId: user.upiId, amount })
    if(res?.success){ setWallet(res.data); setMsg('Money added successfully') } else setMsg(res?.message || 'Failed to add money')
  }

  const handleSend = async (payload) => {
    if(!payload.receiverUpiId || !payload.amount || payload.amount <= 0) {
      setMsg('Please enter receiver UPI and valid amount')
      return
    }
    const body = { senderUpi: user.upiId, receiverUpi: payload.receiverUpiId, amount: payload.amount }
    console.log('Sending money with payload:', body)
    const res = await sendMoney(body)
    if(res?.success){ setMsg('Money sent successfully'); setTimeout(() => load(), 500) } else setMsg(res?.message || 'Failed to send money')
  }

  return (
    <div>
      <h4>Welcome, {profile?.name || user.name}</h4>
      {msg && <div className="alert alert-info">{msg}</div>}
      <WalletView user={user} wallet={wallet} onAdd={handleAdd} />
      <SendForm onSend={handleSend} />

      <div className="card">
        <div className="card-body">
          <h5>Transactions</h5>
          <ul className="list-group">
            {history.length===0 && <li className="list-group-item">No transactions yet</li>}
            {history.map((t, i)=> (
              <li key={i} className="list-group-item d-flex justify-content-between">
                <div><strong>{t.status}</strong> {t.senderUpi === user.upiId ? '→' : '←'} {t.senderUpi === user.upiId ? t.receiverUpi : t.senderUpi}</div>
                <div>₹{t.amount}</div>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  )
}
