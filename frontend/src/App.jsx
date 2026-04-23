import { useState, useEffect } from 'react';
import MapComponent from './components/MapComponent';
import './App.css';

function App() {
  const [vehicles, setVehicles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('map'); // State for mobile tabs

  useEffect(() => {
    const fetchVehicles = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/transit');
        const data = await response.json();
        setVehicles(data);
        setLoading(false);
      } catch (error) {
        console.error('Error fetching data:', error);
        setLoading(false);
      }
    };

    fetchVehicles();
    const interval = setInterval(fetchVehicles, 5000); // Poll every 5 seconds
    
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="app-container">
      <header className="app-header">
        <div className="header-title-group">
          <div className="logo-container">
            <div className="pulse-dot"></div>
            <h1>Live Flight Tracker</h1>
          </div>
          <p className="subtitle">Real-time Flight Tracking in Guadalajara</p>
        </div>
        
        {/* Mobile Tabs */}
        <div className="mobile-tabs">
          <button 
            className={`tab-btn ${activeTab === 'list' ? 'active' : ''}`}
            onClick={() => setActiveTab('list')}
          >
            List View
          </button>
          <button 
            className={`tab-btn ${activeTab === 'map' ? 'active' : ''}`}
            onClick={() => setActiveTab('map')}
          >
            Map View
          </button>
        </div>
      </header>

      <main className="main-content">
        <div className={`dashboard-panel ${activeTab === 'list' ? 'active' : ''}`}>
          <div className="stats-card">
            <h3>Active Vehicles</h3>
            <div className="stat-number">{vehicles.length}</div>
            <p className="stat-label">Currently tracked</p>
          </div>
          
          <div className="vehicle-list">
            <h3>Recent Updates</h3>
            {loading ? (
              <p>Loading transit data...</p>
            ) : (
              <ul>
                {vehicles.map(v => {
                  const isActive = Date.now() - new Date(v.lastUpdate).getTime() < 30000;
                  return (
                  <li key={v.id} className={`vehicle-item ${!isActive ? 'inactive-item' : ''}`}>
                    <span className="route-badge">{v.route}</span>
                    <span className="vehicle-id">{v.vehicleId}</span>
                    <span className="update-time">{new Date(v.lastUpdate).toLocaleTimeString()}</span>
                    <span className={`inactive-badge ${isActive ? 'hidden' : ''}`}>INACTIVE</span>
                  </li>
                  );
                })}
              </ul>
            )}
          </div>
        </div>

        <div className={`map-panel ${activeTab === 'map' ? 'active' : ''}`}>
          <MapComponent vehicles={vehicles} />
        </div>
      </main>
    </div>
  );
}

export default App;
