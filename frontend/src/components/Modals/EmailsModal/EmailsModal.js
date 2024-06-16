import React, { useEffect, useState } from 'react';
import { Button, Modal, Table } from 'react-bootstrap';
import api from '../../../api/Api';

const EmailsModal = ({ show, handleClose, userId }) => {
    const [emails, setEmails] = useState([]);

    useEffect(() => {
        const fetchEmails = async () => {
            try {
                const response = await api.get(`/emails/user/${userId}`, {
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    }
                });
                setEmails(response.data);
            } catch (error) {
                console.error('Error fetching emails:', error);
            }
        };

        if (show && userId) {
            fetchEmails();
        }
    }, [show, userId]);

    return (
        <Modal show={show} onHide={handleClose} size="xl">
            <Modal.Header closeButton>
                <Modal.Title>Emails Sent to User ID: {userId}</Modal.Title>
            </Modal.Header>
            <Modal.Body style={{ maxHeight: '70vh', overflowY: 'auto' }}>
                <Table striped bordered hover responsive>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Subject</th>
                        <th>Message</th>
                        <th>Recipient Email</th>
                        <th>Sent At</th>
                        <th>Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    {emails.map((email) => (
                        <tr key={email.id}>
                            <td>{email.id}</td>
                            <td>{email.subject}</td>
                            <td style={{ whiteSpace: 'pre-wrap', wordWrap: 'break-word' }}>{email.message}</td>
                            <td>{email.recipientEmail}</td>
                            <td>{email.sentAt}</td>
                            <td>{email.status}</td>
                        </tr>
                    ))}
                    </tbody>
                </Table>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={handleClose}>
                    Close
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default EmailsModal;
