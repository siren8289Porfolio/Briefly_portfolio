import { BrowserRouter, Routes, Route, Link } from 'react-router-dom'
import HomePage from './pages/HomePage'
import FundsPage from './pages/FundsPage'
import './App.css'

function App() {
  return (
    <BrowserRouter>
      <div className="app">
        <header className="header">
          <Link to="/" className="logo">Briefly</Link>
          <nav>
            <Link to="/funds">투자상품</Link>
          </nav>
        </header>
        <main className="main">
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/funds" element={<FundsPage />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  )
}

export default App
