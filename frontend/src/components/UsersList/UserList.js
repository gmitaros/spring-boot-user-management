import React, {useEffect, useState} from 'react';
import {Alert, Button, Form, Modal, Pagination, Table} from 'react-bootstrap';
import api from "../../api/Api";

const UserList = ({user}) => {
    const [users, setUsers] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [showActiveUsers, setShowActiveUsers] = useState(true);
    const [selectedUsers, setSelectedUsers] = useState([]);
    const [alert, setAlert] = useState(null);
    const [showEditModal, setShowEditModal] = useState(false);
    const [editingUser, setEditingUser] = useState(null);
    const [userDetails, setUserDetails] = useState({
        firstname: '',
        lastname: '',
        email: '',
        accountLocked: false,
        enabled: false,
        roles: []
    });

    useEffect(() => {
        fetchUsers();
    }, [page, showActiveUsers, user]);

    const fetchUsers = async () => {
        try {
            const response = await api.get(`/auth/users?page=${page}&size=10&active=${showActiveUsers}`, {
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    }
                }
            );
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
            firstname: user.firstname,
            lastname: user.lastname,
            email: user.email,
            accountLocked: user.accountLocked,
            enabled: user.enabled,
            roles: user.roles
        });
        setShowEditModal(true);
    };

    const handleSaveEdit = async () => {
        try {
            await api.put(`/auth/users/${editingUser.id}`, userDetails, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });
            setAlert({type: 'success', message: 'User updated successfully'});
            setShowEditModal(false);
            await fetchUsers();
        } catch (error) {
            setAlert({type: 'danger', message: 'Error updating user'});
            console.error('Error updating user:', error);
        }
    };

    const isAdmin = user.roles.includes('ROLE_ADMIN');
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
                                    <Button variant="danger" onClick={() => handleDelete(user.id)} disabled={user.deleted}>
                                        Delete
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

            <Modal show={showEditModal} onHide={() => setShowEditModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Edit User</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group controlId="formFirstName">
                            <Form.Label>First Name</Form.Label>
                            <Form.Control
                                type="text"
                                value={userDetails.firstname}
                                onChange={(e) => setUserDetails({...userDetails, firstname: e.target.value})}
                            />
                        </Form.Group>
                        <Form.Group controlId="formLastName">
                            <Form.Label>Last Name</Form.Label>
                            <Form.Control
                                type="text"
                                value={userDetails.lastname}
                                onChange={(e) => setUserDetails({...userDetails, lastname: e.target.value})}
                            />
                        </Form.Group>
                        <Form.Group controlId="formEmail">
                            <Form.Label>Email</Form.Label>
                            <Form.Control
                                type="email"
                                value={userDetails.email}
                                onChange={(e) => setUserDetails({...userDetails, email: e.target.value})}
                            />
                        </Form.Group>
                        <Form.Group controlId="formAccountLocked">
                            <Form.Check
                                type="checkbox"
                                label="Account Locked"
                                checked={userDetails.accountLocked}
                                onChange={(e) => setUserDetails({...userDetails, accountLocked: e.target.checked})}
                            />
                        </Form.Group>
                        <Form.Group controlId="formEnabled">
                            <Form.Check
                                type="checkbox"
                                label="Enabled"
                                checked={userDetails.enabled}
                                onChange={(e) => setUserDetails({...userDetails, enabled: e.target.checked})}
                            />
                        </Form.Group>
                        <Form.Group controlId="formRoles">
                            <Form.Label>Roles</Form.Label>
                            <Form.Control
                                type="text"
                                value={userDetails.roles.join(', ')}
                                onChange={(e) => setUserDetails({
                                    ...userDetails,
                                    roles: e.target.value.split(',').map(role => role.trim())
                                })}
                            />
                        </Form.Group>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowEditModal(false)}>
                        Cancel
                    </Button>
                    <Button variant="primary" onClick={handleSaveEdit}>
                        Save Changes
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default UserList;
