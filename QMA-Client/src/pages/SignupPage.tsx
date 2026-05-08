import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/useAuth'
import { Eye, EyeOff, Ruler } from 'lucide-react'
import '../styles/auth.scss'

export function SignupPage() {
  const { signup, logout, startGoogleLogin } = useAuth()
  const navigate = useNavigate()
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  // Removed auto-redirect to allow the user to see the success message and go to login


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
      await logout() // Clear the auto-session so they can login manually
      navigate('/login', { replace: true })
    } catch (signupError) {
      setError(signupError instanceof Error ? signupError.message : 'Signup failed')
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
        <p>Your journey to perfect precision starts here.</p>
      </header>

      <div className="auth-card-container">
        <div className="auth-card">
          <div id="signup-form-container">
            <h3>Sign Up</h3>
            <p className="subtitle">Join us today to start measuring with precision</p>
            <form id="signup-form" onSubmit={handleSubmit}>
              <div className="input-section">
                <label htmlFor="signup-name">Full Name</label>
                <input
                  type="text"
                  id="signup-name"
                  placeholder="Full Name"
                  required
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                />
                <label htmlFor="signup-email">Email</label>
                <input
                  type="email"
                  id="signup-email"
                  placeholder="Email Address"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
                <label htmlFor="signup-password">Password</label>
                <div className="password-input-wrapper">
                  <input
                    type={showPassword ? 'text' : 'password'}
                    id="signup-password"
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
    </div>
  )
}
