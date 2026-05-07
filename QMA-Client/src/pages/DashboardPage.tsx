import { useEffect, useMemo, useState, useRef } from 'react'
import { UNITS_BY_TYPE } from '../constants/units'
import type { MeasurementType, OperationType } from '../constants/units'
import { quantityService } from '../services/quantityService'
import type { QuantityMeasurementHistory } from '../types'
import { Ruler, Scale, Thermometer, Droplets, Plus, Minus, Divide, ArrowRightLeft, Equal } from 'lucide-react'
import '../styles/dashboard.scss'

const MEASUREMENT_TYPES_UI = [
  { key: 'LENGTH' as MeasurementType, label: 'Length', icon: Ruler, color: 'text-yellow-500' },
  { key: 'WEIGHT' as MeasurementType, label: 'Weight', icon: Scale, color: 'text-amber-600' },
  { key: 'TEMPERATURE' as MeasurementType, label: 'Temperature', icon: Thermometer, color: 'text-red-500' },
  { key: 'VOLUME' as MeasurementType, label: 'Volume', icon: Droplets, color: 'text-blue-400' },
]

export function DashboardPage() {
  const isFirstRender = useRef(true)
  const [measurementType, setMeasurementType] = useState<MeasurementType>('LENGTH')
  const [actionCategory, setActionCategory] = useState<'CONVERT' | 'COMPARE' | 'ARITHMETIC'>('CONVERT')
  const [arithmeticOp, setArithmeticOp] = useState<'ADD' | 'SUBTRACT' | 'DIVIDE'>('ADD')
  
  const [leftValue, setLeftValue] = useState('')
  const [leftUnit, setLeftUnit] = useState(UNITS_BY_TYPE.LENGTH[0])
  const [rightValue, setRightValue] = useState('')
  const [rightUnit, setRightUnit] = useState(UNITS_BY_TYPE.LENGTH[1])
  
  const [result, setResult] = useState<QuantityMeasurementHistory | null>(null)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')

  const unitOptions = useMemo(() => UNITS_BY_TYPE[measurementType], [measurementType])

  useEffect(() => {
    const [firstUnit, secondUnit] = unitOptions
    setLeftUnit(firstUnit)
    setRightUnit(secondUnit ?? firstUnit)
    setResult(null)
    setError('')
  }, [measurementType, unitOptions])

  const runOperation = async () => {
    setSubmitting(true)
    setError('')
    try {
      const op = actionCategory === 'ARITHMETIC' ? arithmeticOp : actionCategory
      const response = await quantityService.performOperation(
        op as OperationType,
        { value: Number(leftValue), unit: leftUnit, measurementType },
        { value: Number(rightValue || 0), unit: rightUnit, measurementType },
      )
      setResult(response)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Operation failed')
      setResult(null)
    } finally {
      setSubmitting(false)
    }
  }

  useEffect(() => {
    const timer = setTimeout(() => {
      // For CONVERT, we only need leftValue. For others, we need both.
      const isReady = leftValue !== '' && (actionCategory === 'CONVERT' || rightValue !== '')
      if (isReady) {
        if (isFirstRender.current) {
          isFirstRender.current = false
          return
        }
        runOperation()
      }
    }, 500)
    return () => clearTimeout(timer)
  }, [leftValue, rightValue, leftUnit, rightUnit, measurementType, actionCategory, arithmeticOp])

  return (
    <div className="dashboard-viewport">
      <div className="header-banner">
        <h1>Welcome To Quantity Measurement</h1>
      </div>

      <div className="dashboard-content">
        <section className="section-group">
          <h2 className="section-title">Choose Type</h2>
          <div className="type-grid">
            {MEASUREMENT_TYPES_UI.map((item) => (
              <button
                key={item.key}
                onClick={() => setMeasurementType(item.key)}
                className={`type-btn ${measurementType === item.key ? 'active' : ''}`}
              >
                <div className="icon-box">
                  <item.icon size={22} className={item.color} />
                </div>
                <span>{item.label}</span>
              </button>
            ))}
          </div>
        </section>

        <section className="section-group">
          <h2 className="section-title">Choose Action</h2>
          <div className="action-toggle">
            {[
              { key: 'COMPARE', label: 'Comparison' },
              { key: 'CONVERT', label: 'Conversion' },
              { key: 'ARITHMETIC', label: 'Arithmetic' },
            ].map((action) => (
              <button
                key={action.key}
                onClick={() => setActionCategory(action.key as any)}
                className={actionCategory === action.key ? 'active' : ''}
              >
                {action.label}
              </button>
            ))}
          </div>
        </section>

        <section className="main-input-row">
          <div className="input-card">
            <h3>{actionCategory === 'ARITHMETIC' ? 'Value 1' : 'From'}</h3>
            <input
              type="number"
              value={leftValue}
              onChange={(e) => setLeftValue(e.target.value)}
            />
            <select value={leftUnit} onChange={(e) => setLeftUnit(e.target.value)}>
              {unitOptions.map(unit => <option key={unit} value={unit}>{unit}</option>)}
            </select>
          </div>

          <div className="operator-indicator">
            {actionCategory === 'CONVERT' && <ArrowRightLeft size={20} />}
            {actionCategory === 'COMPARE' && <Equal size={20} />}
            {actionCategory === 'ARITHMETIC' && (
              <button onClick={() => {
                setArithmeticOp(prev => {
                  if (prev === 'ADD') return 'SUBTRACT'
                  if (prev === 'SUBTRACT') return 'DIVIDE'
                  return 'ADD'
                })
              }}>
                {arithmeticOp === 'ADD' && <Plus size={20} />}
                {arithmeticOp === 'SUBTRACT' && <Minus size={20} />}
                {arithmeticOp === 'DIVIDE' && <Divide size={20} />}
              </button>
            )}
          </div>

          <div className="input-card">
            <h3>{actionCategory === 'ARITHMETIC' ? 'Value 2' : 'To'}</h3>
            <input
              type="number"
              value={rightValue}
              onChange={(e) => setRightValue(e.target.value)}
            />
            <select value={rightUnit} onChange={(e) => setRightUnit(e.target.value)}>
              {unitOptions.map(unit => <option key={unit} value={unit}>{unit}</option>)}
            </select>
          </div>
        </section>

        <section className="result-section">
          <h2 className="section-title">Result</h2>
          <div className="result-display">
            <span className="result-value">
              {submitting ? '...' : (result?.resultValue?.toLocaleString(undefined, { maximumFractionDigits: 4 }) || result?.resultString || '0')}
            </span>
            <div className="result-unit-box">
              {result?.resultUnit || 'Unit'}
            </div>
          </div>
          {error && <p className="error-text">{error}</p>}
        </section>
      </div>

      <footer className="dashboard-footer">
        <p>&copy; 2026 Quantity Measurement App. All rights reserved.</p>
      </footer>
    </div>
  )
}
