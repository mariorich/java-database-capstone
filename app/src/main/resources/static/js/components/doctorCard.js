import { overlayBooking } from './loggedPatient.js';
import { deleteDoctor } from './doctorServices.js';
import { getPatientDetails } from './patientServices.js';


export function createDoctorCard(doctor) {
    const card = document.createElement('div');
    card.className = 'doctor-card';

    // Get current user role from localStorage
    const role = localStorage.getItem('userRole');
    const token = localStorage.getItem('token');

    // Doctor info container
    const infoDiv = document.createElement('div');
    infoDiv.className = 'doctor-info';

    const nameEl = document.createElement('h3');
    nameEl.textContent = doctor.name;

    const specializationEl = document.createElement('p');
    specializationEl.textContent = `Specialization: ${doctor.specialization}`;

    const emailEl = document.createElement('p');
    emailEl.textContent = `Email: ${doctor.email}`;

    const timesEl = document.createElement('p');
    timesEl.textContent = `Available Times: ${doctor.availableTimes.join(', ')}`;

    // Append info elements
    infoDiv.append(nameEl, specializationEl, emailEl, timesEl);

    // Action buttons container
    const actionsDiv = document.createElement('div');
    actionsDiv.className = 'doctor-actions';

    // === ADMIN ROLE ACTIONS ===
    if (role === 'admin') {
        const deleteBtn = document.createElement('button');
        deleteBtn.textContent = 'Delete';
        deleteBtn.className = 'adminBtn';

        deleteBtn.addEventListener('click', async () => {
            const adminToken = localStorage.getItem('token');
            if (!adminToken) {
                alert('Admin not logged in.');
                return;
            }

            const confirmDelete = confirm(`Are you sure you want to delete Dr. ${doctor.name}?`);
            if (!confirmDelete) return;

            try {
                const result = await deleteDoctor(doctor.id, adminToken);
                if (result.success) {
                    alert(`Dr. ${doctor.name} deleted successfully.`);
                    card.remove();
                } else {
                    alert(`Failed to delete Dr. ${doctor.name}: ${result.message}`);
                }
            } catch (err) {
                console.error(err);
                alert('Error deleting doctor.');
            }
        });

        actionsDiv.appendChild(deleteBtn);
    }

    // === PATIENT (NOT LOGGED-IN) ROLE ACTIONS ===
    if (role === 'patient' || !role) {
        const bookBtn = document.createElement('button');
        bookBtn.textContent = 'Book Now';
        bookBtn.className = 'primaryBtn';

        bookBtn.addEventListener('click', () => {
            alert('Please log in as a patient to book an appointment.');
        });

        actionsDiv.appendChild(bookBtn);
    }

    // === LOGGED-IN PATIENT ROLE ACTIONS ===
    if (role === 'loggedPatient') {
        const bookBtn = document.createElement('button');
        bookBtn.textContent = 'Book Now';
        bookBtn.className = 'primaryBtn';

        bookBtn.addEventListener('click', async () => {
            if (!token) {
                alert('Session expired. Please log in again.');
                window.location.href = '/';
                return;
            }

            try {
                const patient = await getPatientDetails(token);
                overlayBooking(doctor, patient);
            } catch (err) {
                console.error(err);
                alert('Error fetching patient details.');
            }
        });

        actionsDiv.appendChild(bookBtn);
    }

    // Append info and actions to card
    card.append(infoDiv, actionsDiv);

    return card;
}
