import { useNavigate } from 'react-router-dom'
import './NotFound.css'

const NotFound = ()=>{
  const navigate = useNavigate()
  return (
    <div className="not-found-container">
      <div className="not-found-content">
        <h1 className="not-found-title">404</h1>
        <h2 className="not-found-subtitle">Page Not Found</h2>
        <p className="not-found-message">
          The page you're looking for doesn't exist or has been moved.
        </p>
        <button className="not-found-button" onClick={()=>navigate('/')}>
          Go To homepage
        </button>
      </div>
    </div>
  )
}

export default NotFound