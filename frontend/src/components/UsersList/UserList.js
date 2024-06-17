import React, {useEffect, useState} from 'react';
import {Alert, Button, Form, Pagination, Table} from 'react-bootstrap';
import api from "../../api/Api";
import EditUserModal from '../Modals/EditUserModal/EditUserModal';
import EmailsModal from '../Modals/EmailsModal/EmailsModal';
import { useNavigate  } from 'react-router-dom';

const UserList = ({user}) => {
    const [users, setUsers] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [showActiveUsers, setShowActiveUsers] = useState(true);
    const [selectedUsers, setSelectedUsers] = useState([]);
    const [alert, setAlert] = useState(null);
    const [showEditModal, setShowEditModal] = useState(false);
    const [showEmailsModal, setShowEmailsModal] = useState(false);
    const [emailUserId, setEmailUserId] = useState(null);
    const [editingUser, setEditingUser] = useState(null);
    const [userDetails, setUserDetails] = useState({
        firstname: '',
        lastname: '',
        email: '',
        accountLocked: false,
        enabled: false,
        roles: []
    });
    const navigate = useNavigate();
    const isAdmin = user.roles.includes('ROLE_ADMIN');

    useEffect(() => {
        if (isAdmin) {
            fetchUsers();
        }
    }, [page, showActiveUsers, user]);

    const fetchUsers = async () => {
        try {
            const response = await api.get(`/auth/users?page=${page}&size=10&active=${showActiveUsers}`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });
            setUsers(response.data.content);
            setTotalPages(response.data.totalPages);
        } catch (error) {
            console.error('Error fetching users:', error);
        }
    };

    const handleDelete = async (userId) => {
        try {
            await api.delete(`/auth/users/${userId}`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });
            setAlert({type: 'success', message: 'User deleted successfully'});
            await fetchUsers();
        } catch (error) {
            setAlert({type: 'danger', message: 'Error deleting user'});
            console.error('Error deleting user:', error);
        }
    };

    const handleSelectUser = (userId) => {
        setSelectedUsers((prevSelected) =>
            prevSelected.includes(userId) ? prevSelected.filter(id => id !== userId) : [...prevSelected, userId]
        );
    };

    const handleDeleteMultiple = async () => {
        try {
            await api.delete(`/auth/users`, {data: selectedUsers}, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });
            setAlert({type: 'success', message: 'Users deleted successfully'});
            setSelectedUsers([]);
            await fetchUsers();
        } catch (error) {
            setAlert({type: 'danger', message: 'Error deleting users'});
            console.error('Error deleting users:', error);
        }
    };

    const handlePageChange = (newPage) => {
        setPage(newPage);
    };

    const handleFilterChange = () => {
        setShowActiveUsers(!showActiveUsers);
        setPage(0); // Reset to first page when filter changes
    };

    const handleEdit = (user) => {
        setEditingUser(user);
        setUserDetails({
            id: user.id,
            firstname: user.firstname,
            lastname: user.lastname,
            email: user.email,
            accountLocked: user.accountLocked,
            enabled: user.enabled,
            roles: user.roles
        });
        setShowEditModal(true);
    };

    const handleShowEmails = (userId) => {
        setEmailUserId(userId);
        setShowEmailsModal(true);
    };

    const handleViewProfile = () => {
        navigate(`/user/${user.id}/profile`);
    };

    if (!isAdmin) {
        return (
            <div className="container mx-auto my-4">
                <Alert variant="danger">You do not have access to this page because you are not an admin.</Alert>
                <Button variant="primary" onClick={handleViewProfile}>View Your Profile</Button>
            </div>
        );
    }
    return (
        <div className="container mx-auto my-4">
            {alert && <Alert variant={alert.type}>{alert.message}</Alert>}
            <Form>
                <Form.Check
                    type="switch"
                    id="active-users-switch"
                    label={showActiveUsers ? "Showing Active Users (Deleted false)" : "Showing Inactive Users (Deleted false)"}
                    checked={showActiveUsers}
                    onChange={handleFilterChange}
                />
            </Form>
            <Table striped bordered hover className="mt-4">
                <thead>
                <tr>
                    <th>Select</th>
                    <th>ID</th>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Email</th>
                    <th>Account Locked</th>
                    <th>Activated</th>
                    <th>Deleted</th>
                    <th>Roles</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {users.map((user) => (
                    <tr key={user.id}>
                        <td>
                            <Form.Check
                                type="checkbox"
                                checked={selectedUsers.includes(user.id)}
                                onChange={() => handleSelectUser(user.id)}
                            />
                        </td>
                        <td>{user.id}</td>
                        <td>{user.firstname}</td>
                        <td>{user.lastname}</td>
                        <td>{user.email}</td>
                        <td>{user.accountLocked ? 'Yes' : 'No'}</td>
                        <td>{user.enabled ? 'Yes' : 'No'}</td>
                        <td>{user.deleted ? 'Yes' : 'No'}</td>
                        <td>{user.roles.join(', ')}</td>
                        <td>
                            {isAdmin && (
                                <>
                                    <Button variant="warning" onClick={() => handleEdit(user)}>Edit</Button>{' '}
                                    <Button variant="danger" onClick={() => handleDelete(user.id)}
                                            disabled={user.deleted}>
                                        Delete
                                    </Button>{' '}
                                    <Button variant="info" onClick={() => handleShowEmails(user.id)}>
                                        Show Emails
                                    </Button>
                                </>
                            )}
                        </td>
                    </tr>
                ))}
                </tbody>
            </Table>
            <Button variant="danger" className="mb-4" onClick={handleDeleteMultiple}
                    disabled={selectedUsers.length === 0}>
                Delete Selected Users
            </Button>
            <Pagination>
                <Pagination.First onClick={() => handlePageChange(0)} disabled={page === 0}/>
                <Pagination.Prev onClick={() => handlePageChange(page - 1)} disabled={page === 0}/>
                {[...Array(totalPages)].map((_, index) => (
                    <Pagination.Item key={index} active={index === page} onClick={() => handlePageChange(index)}>
                        {index + 1}
                    </Pagination.Item>
                ))}
                <Pagination.Next onClick={() => handlePageChange(page + 1)} disabled={page === totalPages - 1}/>
                <Pagination.Last onClick={() => handlePageChange(totalPages - 1)} disabled={page === totalPages - 1}/>
            </Pagination>

            <EditUserModal
                show={showEditModal}
                handleClose={() => setShowEditModal(false)}
                userDetails={userDetails}
                setUserDetails={setUserDetails}
                fetchUsers={fetchUsers}
                setAlert={setAlert}
            />

            <EmailsModal
                show={showEmailsModal}
                handleClose={() => setShowEmailsModal(false)}
                userId={emailUserId}
            />
        </div>
    );
};

export default UserList;
