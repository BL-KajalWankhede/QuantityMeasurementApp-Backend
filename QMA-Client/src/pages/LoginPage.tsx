import { useEffect, useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/useAuth'
import { Eye, EyeOff, Ruler } from 'lucide-react'
import '../styles/auth.scss'

export function LoginPage() {
  const { isAuthenticated, login, startGoogleLogin } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    if (isAuthenticated) {
      navigate('/dashboard', { replace: true })
    }
  }, [isAuthenticated, navigate])

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />
  }

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      await login(email, password)
    } catch (loginError) {
      setError(loginError instanceof Error ? loginError.message : 'Login failed')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="auth-page-wrapper">
      <header className="external-header">
        <div className="brand-icon-box">
          <Ruler size={40} strokeWidth={1.5} />
        </div>
        <h1>Quantity <span>Measurement</span></h1>
        <div className="title-underline"></div>
        <p>Precision in every unit, excellence in every calculation.</p>
      </header>

      <div className="auth-card-container">
        <div className="auth-card">
          <div id="login-form-container">
            <h3>Login</h3>
            <p className="subtitle">Enter your email and password to access your account</p>
            <form id="login-form" onSubmit={handleSubmit}>
              <div className="input-section">
                <label htmlFor="login-email">Email</label>
                <input
                  type="email"
                  id="login-email"
                  placeholder="Email Address"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
                <label htmlFor="login-password">Password</label>
                <div className="password-input-wrapper">
                  <input
                    type={showPassword ? 'text' : 'password'}
                    id="login-password"
                    placeholder="Password"
                    required
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                  />
                  <button
                    type="button"
                    className="password-toggle-btn"
                    onClick={() => setShowPassword(!showPassword)}
                    aria-label={showPassword ? 'Hide password' : 'Show password'}
                  >
                    {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                  </button>
                </div>
              </div>

              {error && <div className="auth-error-msg">{error}</div>}

              <button type="submit" disabled={submitting} className="btn btn-primary">
                {submitting ? 'Logging in...' : 'Login'}
              </button>
            </form>

            <button type="button" onClick={startGoogleLogin} className="btn btn-google">
              <img 
                src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg" 
                alt="Google" 
                style={{ width: '16px' }}
              />
              Continue with Google
            </button>

            <p className="auth-toggle">
              Don't have an account? <Link to="/signup">Sign Up</Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
