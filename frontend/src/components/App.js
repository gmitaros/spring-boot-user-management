import './App.css';
import Header from "./Header/Header";
import {useEffect, useState} from "react";
import api from "../api/Api";
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import UserList from "./UsersList/UserList";
import Welcome from "./WelcomePage/Welcome";
import UserDetailPage from "./UserDetails/UserDetailPage";

function App() {
    const [token, setToken] = useState(null);
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    const handleLogin = (newToken, userInfo) => {
        setToken(newToken);
        setUser(userInfo);
        if (newToken !== undefined) {
            localStorage.setItem('token', newToken);
            api.defaults.headers.common['Authorization'] = `Bearer ${newToken}`;
        } else {
            localStorage.removeItem('token');
            delete api.defaults.headers.common['Authorization'];
        }
    };

    const checkTokenValidity = async (storedToken) => {
        try {
            if (storedToken) {
                const response = await api.get('/auth/user/info', {
                    headers: {
                        'Authorization': `Bearer ${storedToken}`
                    }
                });
                if (response.data) {
                    setUser(response.data);
                }
            } else {
                console.log('User is logged out');
            }
        } catch (error) {
            console.error('Error checking token validity:', error);
        }
    };

    useEffect(() => {
        const storedToken = localStorage.getItem('token');
        if (storedToken) {
            setToken(storedToken);
            checkTokenValidity(storedToken).then(r => r);
            setLoading(false);
        }
        setLoading(false);
    }, []);

    return (
        <Router>
            <div className="App">
                {loading ? (
                    <div>Loading...</div>
                ) : (
                    <>
                        <Header onLogin={handleLogin} user={user}/>
                        <Routes>
                            <Route
                                exact
                                path="/"
                                element={user ? <UserList user={user}/> : <Welcome/>}
                            />
                            <Route
                                path="/user/:userId/profile"
                                element={<UserDetailPage user={user} />}
                            />
                        </Routes>
                    </>
                )}
            </div>
        </Router>
    );
}

export default App;
