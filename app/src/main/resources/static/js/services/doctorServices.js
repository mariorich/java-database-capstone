// doctorServices.js

import { BASE_API_URL } from './config.js';

// Full API endpoint for doctor-related actions
const DOCTOR_API = `${BASE_API_URL}/doctor`;

/**
 * Fetch all doctors from the API
 * @returns {Promise<Array>} List of doctors or empty array if failed
 */
export async function getDoctors() {
  try {
    const response = await fetch(`${DOCTOR_API}/getAll`);
    if (!response.ok) throw new Error('Failed to fetch doctors');
    const data = await response.json();
    return data.doctors || [];
  } catch (error) {
    console.error('Error fetching doctors:', error);
    return [];
  }
}

/**
 * Delete a doctor by ID using the provided token
 * @param {string} doctorId - Doctor's unique ID
 * @param {string} token - Admin authentication token
 * @returns {Promise<Object>} Result with success status and message
 */
export async function deleteDoctor(doctorId, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/delete/${doctorId}/${token}`, {
      method: 'DELETE',
    });

    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || (response.ok ? 'Doctor deleted' : 'Deletion failed'),
    };
  } catch (error) {
    console.error('Error deleting doctor:', error);
    return { success: false, message: 'An error occurred while deleting the doctor.' };
  }
}

/**
 * Save (create) a new doctor
 * @param {Object} doctor - Doctor object with details
 * @param {string} token - Admin authentication token
 * @returns {Promise<Object>} Result with success status and message
 */
export async function saveDoctor(doctor, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/save/${token}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(doctor),
    });

    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || (response.ok ? 'Doctor saved successfully' : 'Failed to save doctor'),
    };
  } catch (error) {
    console.error('Error saving doctor:', error);
    return { success: false, message: 'An error occurred while saving the doctor.' };
  }
}

/**
 * Filter doctors based on criteria (name, time, and specialty)
 * @param {string} name - Doctor's name or partial name
 * @param {string} time - Appointment time (AM/PM)
 * @param {string} specialty - Doctor's specialty
 * @returns {Promise<Object>} Filtered doctors data
 */
export async function filterDoctors(name, time, specialty) {
  try {
    const response = await fetch(`${DOCTOR_API}/filter/${name}/${time}/${specialty}`);
    if (response.ok) {
      const data = await response.json();
      return data;
    } else {
      console.error('Failed to filter doctors');
      return { doctors: [] };
    }
  } catch (error) {
    console.error('Error filtering doctors:', error);
    alert('Unable to fetch filtered doctors at the moment.');
    return { doctors: [] };
  }
}
