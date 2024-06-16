import React, { useState } from 'react';
import { Button, Form, Modal } from 'react-bootstrap';
import api from '../../../api/Api';

const EditUserModal = ({ show, handleClose, userDetails, setUserDetails, fetchUsers, setAlert }) => {
    const handleSaveEdit = async () => {
        try {
            await api.put(`/auth/users/${userDetails.id}`, userDetails, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });
            setAlert({ type: 'success', message: 'User updated successfully' });
            handleClose();
            fetchUsers();
        } catch (error) {
            setAlert({ type: 'danger', message: 'Error updating user' });
            console.error('Error updating user:', error);
        }
    };

    return (
        <Modal show={show} onHide={handleClose}>
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
                <Button variant="secondary" onClick={handleClose}>
                    Cancel
                </Button>
                <Button variant="primary" onClick={handleSaveEdit}>
                    Save Changes
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default EditUserModal;
