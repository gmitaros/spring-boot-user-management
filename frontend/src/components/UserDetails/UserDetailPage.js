import React, {useEffect, useState} from 'react';
import {useParams} from 'react-router-dom';
import {Container, Table, Spinner, Alert} from 'react-bootstrap';
import api from "../../api/Api";

const UserDetailPage = () => {
    const {userId} = useParams();
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchUserDetails = async () => {
            try {
                const response = await api.get(`/auth/user/info`, {
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    }
                });
                setUser(response.data);
                setLoading(false);
            } catch (err) {
                setError('Error fetching user details');
                setLoading(false);
            }
        };

        fetchUserDetails();
    }, [userId]);

    if (loading) {
        return <Spinner animation="border" variant="primary"/>;
    }

    if (error) {
        return <Alert variant="danger">{error}</Alert>;
    }

    return (
        <Container className="my-4">
            <h2>User Details</h2>
            <Table striped bordered hover>
                <tbody>
                <tr>
                    <th>ID</th>
                    <td>{user.id}</td>
                </tr>
                <tr>
                    <th>First Name</th>
                    <td>{user.firstname}</td>
                </tr>
                <tr>
                    <th>Last Name</th>
                    <td>{user.lastname}</td>
                </tr>
                <tr>
                    <th>Email</th>
                    <td>{user.email}</td>
                </tr>
                <tr>
                    <th>Account Locked</th>
                    <td>{user.accountLocked ? 'Yes' : 'No'}</td>
                </tr>
                <tr>
                    <th>Enabled</th>
                    <td>{user.enabled ? 'Yes' : 'No'}</td>
                </tr>
                <tr>
                    <th>Deleted</th>
                    <td>{user.deleted ? 'Yes' : 'No'}</td>
                </tr>
                <tr>
                    <th>Roles</th>
                    <td>{user.roles.join(', ')}</td>
                </tr>
                </tbody>
            </Table>
        </Container>
    );
};

export default UserDetailPage;
