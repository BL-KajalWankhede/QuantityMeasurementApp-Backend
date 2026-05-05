import { useEffect, useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/useAuth'
import { Eye, EyeOff } from 'lucide-react'
import '../styles/auth.scss'

export function SignupPage() {
  const { isAuthenticated, signup, startGoogleLogin } = useAuth()
  const navigate = useNavigate()
  const [name, setName] = useState('')
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

    if (password.length < 8) {
      setError('Password must be at least 8 characters long')
      setSubmitting(false)
      return
    }

    try {
      await signup(name, email, password)
      alert('Account created successfully! Please login.')
      navigate('/login')
    } catch (signupError) {
      setError(signupError instanceof Error ? signupError.message : 'Signup failed')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="auth-page-wrapper">
      <div className="auth-card">
        <header>
          <h1>Quantity Measurement</h1>
          <p>Create your account to start measuring with precision</p>
        </header>

        <div id="signup-form-container">
          <h3>Sign Up</h3>
          <form id="signup-form" onSubmit={handleSubmit}>
            <div className="input-section">
              <label htmlFor="signup-name">Full Name</label>
              <input
                type="text"
                id="signup-name"
                placeholder=""
                required
                value={name}
                onChange={(e) => setName(e.target.value)}
              />
              <label htmlFor="signup-email">Email</label>
              <input
                type="email"
                id="signup-email"
                placeholder=""
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
              <label htmlFor="signup-password">Password</label>
              <div className="password-input-wrapper">
                <input
                  type={showPassword ? 'text' : 'password'}
                  id="signup-password"
                  placeholder=""
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
              {submitting ? 'Creating account...' : 'Create Account'}
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
            Already have an account? <Link to="/login">Login</Link>
          </p>
        </div>
      </div>
    </div>
  )
}
