import React from 'react';
import {Container} from 'react-bootstrap';
import {useNavigate} from 'react-router-dom';

const Welcome = () => {
    const navigate = useNavigate();

    const handleLogin = () => {
        navigate('/login');
    };

    const handleRegister = () => {
        navigate('/register');
    };

    return (
        <Container className="d-flex flex-column align-items-center justify-content-center" style={{height: '100vh'}}>
            <h2>Welcome</h2>
            <p>Please log in or register to continue.</p>
        </Container>
    );
};

export default Welcome;
