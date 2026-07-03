import { useEffect, useState } from 'react'
import { api } from '../api/client'

export default function FundsPage() {
  const [funds, setFunds] = useState([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.getFunds()
      .then((data) => setFunds(data.funds ?? []))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <p>상품 목록을 불러오는 중...</p>
  if (error) return <p className="error">{error}</p>

  return (
    <section>
      <h2>투자상품 목록</h2>
      <ul className="fund-list">
        {funds.map((fund) => (
          <li key={fund.id} className="fund-card">
            <h3>{fund.name}</h3>
            <p>위험등급 {fund.riskGrade} · 예상 수익률 {fund.expectedReturn}%</p>
          </li>
        ))}
      </ul>
    </section>
  )
}
