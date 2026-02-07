/**
 * =========================================
 * HealthTech Demo - JavaScript
 * =========================================
 * 
 * Handles all frontend logic:
 * - API calls using fetch()
 * - JWT token management (localStorage)
 * - DOM manipulation
 * - Event handling
 * 
 * Frontend → Backend Communication:
 * - Base URL points to Spring Boot backend (localhost:8080)
 * - All authenticated requests include JWT in Authorization header
 * - Response is JSON parsed and displayed
 * 
 * =========================================
 */

// ============================================
// CONFIGURATION
// ============================================

/**
 * Base URL for the backend API.
 * Change this if your backend runs on a different port.
 */
const API_BASE_URL = 'http://localhost:8080/api';

// ============================================
// STATE MANAGEMENT
// ============================================

/**
 * Application state.
 * Stores current user info and selected doctor for booking.
 */
let state = {
    user: null,           // Current logged-in user { email, name, role }
    token: null,          // JWT token
    selectedDoctor: null, // Doctor selected for booking
    doctors: []           // Cached list of doctors
};

// ============================================
// DOM ELEMENTS
// ============================================

// Sections
const doctorsSection = document.getElementById('doctors-section');
const authSection = document.getElementById('auth-section');
const bookingSection = document.getElementById('booking-section');
const appointmentsSection = document.getElementById('appointments-section');

// Forms
const loginForm = document.getElementById('login-form');
const registerForm = document.getElementById('register-form');
const formLogin = document.getElementById('form-login');
const formRegister = document.getElementById('form-register');
const formBooking = document.getElementById('form-booking');

// Navigation buttons
const btnShowDoctors = document.getElementById('btn-show-doctors');
const btnShowAppointments = document.getElementById('btn-show-appointments');
const btnShowLogin = document.getElementById('btn-show-login');
const btnShowRegister = document.getElementById('btn-show-register');
const btnLogout = document.getElementById('btn-logout');
const btnCancelBooking = document.getElementById('btn-cancel-booking');

// Other elements
const alertBox = document.getElementById('alert-box');
const userGreeting = document.getElementById('user-greeting');
const doctorsGrid = document.getElementById('doctors-grid');
const appointmentsList = document.getElementById('appointments-list');
const selectedDoctorInfo = document.getElementById('selected-doctor-info');

// ============================================
// UTILITY FUNCTIONS
// ============================================

/**
 * Show alert message to user.
 * 
 * @param {string} message - Message to display
 * @param {string} type - 'success', 'error', or 'warning'
 */
function showAlert(message, type = 'success') {
    alertBox.textContent = message;
    alertBox.className = `alert ${type}`;
    alertBox.style.display = 'block';
    
    // Auto-hide after 5 seconds
    setTimeout(() => {
        alertBox.style.display = 'none';
    }, 5000);
}

/**
 * Make API request with optional authentication.
 * 
 * This function handles:
 * - Setting Content-Type header
 * - Adding Authorization header if token exists
 * - Parsing JSON response
 * - Error handling
 * 
 * @param {string} endpoint - API endpoint (without base URL)
 * @param {object} options - Fetch options (method, body, etc.)
 * @returns {Promise<object>} - Parsed JSON response
 */
async function apiRequest(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    
    // Default headers
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };
    
    // Add Authorization header if token exists
    if (state.token) {
        headers['Authorization'] = `Bearer ${state.token}`;
    }
    
    try {
        const response = await fetch(url, {
            ...options,
            headers
        });
        
        const data = await response.json();
        
        // Check if request was successful
        if (!response.ok) {
            throw new Error(data.message || 'Request failed');
        }
        
        return data;
        
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

/**
 * Format date for display.
 * 
 * @param {string} dateString - ISO date string
 * @returns {string} - Formatted date
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        weekday: 'short',
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

/**
 * Set minimum date for date picker (tomorrow).
 */
function setMinDate() {
    const dateInput = document.getElementById('appointment-date');
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    dateInput.min = tomorrow.toISOString().split('T')[0];
}

// ============================================
// AUTHENTICATION FUNCTIONS
// ============================================

/**
 * Handle user login.
 * 
 * Flow:
 * 1. Get form data
 * 2. POST to /api/auth/login
 * 3. Store JWT token and user info
 * 4. Update UI to show authenticated state
 */
async function handleLogin(event) {
    event.preventDefault();
    
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    
    try {
        const response = await apiRequest('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        });
        
        // Store token and user info
        state.token = response.data.token;
        state.user = {
            email: response.data.email,
            name: response.data.name,
            role: response.data.role
        };
        
        // Save to localStorage for persistence
        localStorage.setItem('healthtech_token', state.token);
        localStorage.setItem('healthtech_user', JSON.stringify(state.user));
        
        showAlert(`Welcome back, ${state.user.name}!`, 'success');
        updateUIForAuth();
        showSection('doctors');
        formLogin.reset();
        
    } catch (error) {
        showAlert(error.message || 'Login failed', 'error');
    }
}

/**
 * Handle user registration.
 * 
 * Flow:
 * 1. Get form data
 * 2. POST to /api/auth/register
 * 3. Store JWT token and user info (auto-login)
 * 4. Update UI
 */
async function handleRegister(event) {
    event.preventDefault();
    
    const name = document.getElementById('register-name').value;
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;
    
    try {
        const response = await apiRequest('/auth/register', {
            method: 'POST',
            body: JSON.stringify({ name, email, password })
        });
        
        // Store token and user info
        state.token = response.data.token;
        state.user = {
            email: response.data.email,
            name: response.data.name,
            role: response.data.role
        };
        
        // Save to localStorage
        localStorage.setItem('healthtech_token', state.token);
        localStorage.setItem('healthtech_user', JSON.stringify(state.user));
        
        showAlert(`Welcome, ${state.user.name}! Your account has been created.`, 'success');
        updateUIForAuth();
        showSection('doctors');
        formRegister.reset();
        
    } catch (error) {
        showAlert(error.message || 'Registration failed', 'error');
    }
}

/**
 * Handle user logout.
 * Clear token and user info, update UI.
 */
function handleLogout() {
    state.token = null;
    state.user = null;
    
    // Clear localStorage
    localStorage.removeItem('healthtech_token');
    localStorage.removeItem('healthtech_user');
    
    showAlert('You have been logged out.', 'success');
    updateUIForAuth();
    showSection('doctors');
}

/**
 * Check for existing session on page load.
 */
function checkExistingSession() {
    const token = localStorage.getItem('healthtech_token');
    const user = localStorage.getItem('healthtech_user');
    
    if (token && user) {
        state.token = token;
        state.user = JSON.parse(user);
        updateUIForAuth();
    }
}

// ============================================
// DOCTOR FUNCTIONS
// ============================================

/**
 * Fetch and display all doctors.
 * 
 * This is a PUBLIC endpoint - no authentication required.
 * Uses GET /api/doctors
 */
async function loadDoctors() {
    try {
        doctorsGrid.innerHTML = '<p class="loading">Loading doctors...</p>';
        
        const response = await apiRequest('/doctors', { method: 'GET' });
        
        state.doctors = response.data;
        
        if (state.doctors.length === 0) {
            doctorsGrid.innerHTML = `
                <div class="empty-state">
                    <h3>No Doctors Available</h3>
                    <p>Please check back later.</p>
                </div>
            `;
            return;
        }
        
        // Render doctor cards
        doctorsGrid.innerHTML = state.doctors.map(doctor => `
            <div class="doctor-card" data-doctor-id="${doctor.id}">
                <h3>👨‍⚕️ ${doctor.name}</h3>
                <span class="doctor-specialty">${doctor.specialty}</span>
                <div class="doctor-info">
                    <p>📧 ${doctor.email}</p>
                    <p>📞 ${doctor.phoneNumber || 'N/A'}</p>
                    <p>📅 ${doctor.yearsOfExperience || 0} years experience</p>
                </div>
                <p class="doctor-status ${doctor.isAvailable ? 'available' : 'unavailable'}">
                    ${doctor.isAvailable ? '✅ Available' : '❌ Not Available'}
                </p>
                <button class="btn btn-primary" 
                        onclick="selectDoctor(${doctor.id})"
                        ${!doctor.isAvailable ? 'disabled' : ''}>
                    Book Appointment
                </button>
            </div>
        `).join('');
        
    } catch (error) {
        doctorsGrid.innerHTML = `
            <div class="empty-state">
                <h3>Failed to Load Doctors</h3>
                <p>${error.message}</p>
                <button class="btn btn-primary" onclick="loadDoctors()">Retry</button>
            </div>
        `;
    }
}

/**
 * Select a doctor for booking.
 * 
 * @param {number} doctorId - ID of selected doctor
 */
function selectDoctor(doctorId) {
    // Check if user is logged in
    if (!state.token) {
        showAlert('Please login to book an appointment.', 'warning');
        showSection('auth');
        return;
    }
    
    // Find doctor in cached list
    const doctor = state.doctors.find(d => d.id === doctorId);
    if (!doctor) {
        showAlert('Doctor not found.', 'error');
        return;
    }
    
    state.selectedDoctor = doctor;
    
    // Display selected doctor info
    selectedDoctorInfo.innerHTML = `
        <h3>${doctor.name}</h3>
        <p>${doctor.specialty} • ${doctor.yearsOfExperience} years experience</p>
    `;
    
    // Show booking section
    showSection('booking');
    setMinDate();
}

// ============================================
// APPOINTMENT FUNCTIONS
// ============================================

/**
 * Handle appointment booking.
 * 
 * AUTHENTICATED endpoint - requires JWT token.
 * Uses POST /api/appointments
 * 
 * CONCURRENCY NOTE:
 * If two users try to book the same slot, the backend
 * will return a 409 Conflict error for the second request.
 */
async function handleBooking(event) {
    event.preventDefault();
    
    if (!state.selectedDoctor) {
        showAlert('Please select a doctor first.', 'error');
        return;
    }
    
    const date = document.getElementById('appointment-date').value;
    const time = document.getElementById('appointment-time').value;
    const notes = document.getElementById('appointment-notes').value;
    
    if (!date || !time) {
        showAlert('Please select both date and time.', 'error');
        return;
    }
    
    // Combine date and time into ISO format
    const appointmentTime = `${date}T${time}:00`;
    
    try {
        const response = await apiRequest('/appointments', {
            method: 'POST',
            body: JSON.stringify({
                doctorId: state.selectedDoctor.id,
                appointmentTime: appointmentTime,
                notes: notes
            })
        });
        
        showAlert('Appointment booked successfully!', 'success');
        formBooking.reset();
        state.selectedDoctor = null;
        showSection('appointments');
        loadMyAppointments();
        
    } catch (error) {
        // Handle double-booking error (409 Conflict)
        if (error.message.includes('already booked') || error.message.includes('conflict')) {
            showAlert('This time slot is already taken. Please choose another time.', 'error');
        } else {
            showAlert(error.message || 'Failed to book appointment.', 'error');
        }
    }
}

/**
 * Load current user's appointments.
 * 
 * AUTHENTICATED endpoint - requires JWT token.
 * Uses GET /api/appointments/my
 */
async function loadMyAppointments() {
    if (!state.token) {
        appointmentsList.innerHTML = `
            <div class="empty-state">
                <h3>Login Required</h3>
                <p>Please login to view your appointments.</p>
            </div>
        `;
        return;
    }
    
    try {
        appointmentsList.innerHTML = '<p class="loading">Loading appointments...</p>';
        
        const response = await apiRequest('/appointments/my', { method: 'GET' });
        
        const appointments = response.data;
        
        if (appointments.length === 0) {
            appointmentsList.innerHTML = `
                <div class="empty-state">
                    <h3>No Appointments</h3>
                    <p>You haven't booked any appointments yet.</p>
                    <button class="btn btn-primary" onclick="showSection('doctors')">Book Now</button>
                </div>
            `;
            return;
        }
        
        // Render appointment cards
        appointmentsList.innerHTML = appointments.map(apt => `
            <div class="appointment-card">
                <div class="appointment-info">
                    <h3>👨‍⚕️ ${apt.doctorName}</h3>
                    <p><strong>Specialty:</strong> ${apt.doctorSpecialty}</p>
                    <p><strong>Date & Time:</strong> ${formatDate(apt.appointmentTime)}</p>
                    ${apt.notes ? `<p><strong>Notes:</strong> ${apt.notes}</p>` : ''}
                    <span class="appointment-status ${apt.status.toLowerCase()}">
                        ${apt.status}
                    </span>
                </div>
                <div class="appointment-actions">
                    ${apt.status === 'SCHEDULED' ? 
                        `<button class="btn btn-danger" onclick="cancelAppointment(${apt.id})">
                            Cancel
                        </button>` : ''}
                </div>
            </div>
        `).join('');
        
    } catch (error) {
        appointmentsList.innerHTML = `
            <div class="empty-state">
                <h3>Failed to Load Appointments</h3>
                <p>${error.message}</p>
                <button class="btn btn-primary" onclick="loadMyAppointments()">Retry</button>
            </div>
        `;
    }
}

/**
 * Cancel an appointment.
 * 
 * AUTHENTICATED endpoint - requires JWT token.
 * Uses DELETE /api/appointments/{id}
 * 
 * @param {number} appointmentId - ID of appointment to cancel
 */
async function cancelAppointment(appointmentId) {
    if (!confirm('Are you sure you want to cancel this appointment?')) {
        return;
    }
    
    try {
        await apiRequest(`/appointments/${appointmentId}`, {
            method: 'DELETE'
        });
        
        showAlert('Appointment cancelled successfully.', 'success');
        loadMyAppointments();
        
    } catch (error) {
        showAlert(error.message || 'Failed to cancel appointment.', 'error');
    }
}

// ============================================
// UI FUNCTIONS
// ============================================

/**
 * Show a specific section and hide others.
 * 
 * @param {string} section - 'doctors', 'auth', 'booking', or 'appointments'
 */
function showSection(section) {
    // Hide all sections
    doctorsSection.style.display = 'none';
    authSection.style.display = 'none';
    bookingSection.style.display = 'none';
    appointmentsSection.style.display = 'none';
    
    // Remove active class from all nav buttons
    btnShowDoctors.classList.remove('active');
    btnShowAppointments.classList.remove('active');
    btnShowLogin.classList.remove('active');
    btnShowRegister.classList.remove('active');
    
    // Show selected section
    switch (section) {
        case 'doctors':
            doctorsSection.style.display = 'block';
            btnShowDoctors.classList.add('active');
            loadDoctors();
            break;
            
        case 'auth':
            authSection.style.display = 'block';
            loginForm.style.display = 'block';
            registerForm.style.display = 'none';
            btnShowLogin.classList.add('active');
            break;
            
        case 'register':
            authSection.style.display = 'block';
            loginForm.style.display = 'none';
            registerForm.style.display = 'block';
            btnShowRegister.classList.add('active');
            break;
            
        case 'booking':
            bookingSection.style.display = 'block';
            break;
            
        case 'appointments':
            appointmentsSection.style.display = 'block';
            btnShowAppointments.classList.add('active');
            loadMyAppointments();
            break;
    }
}

/**
 * Update UI based on authentication state.
 */
function updateUIForAuth() {
    if (state.user) {
        // User is logged in
        btnShowLogin.style.display = 'none';
        btnShowRegister.style.display = 'none';
        btnLogout.style.display = 'inline-block';
        btnShowAppointments.style.display = 'inline-block';
        userGreeting.textContent = `Hello, ${state.user.name}`;
    } else {
        // User is logged out
        btnShowLogin.style.display = 'inline-block';
        btnShowRegister.style.display = 'inline-block';
        btnLogout.style.display = 'none';
        btnShowAppointments.style.display = 'none';
        userGreeting.textContent = '';
    }
}

// ============================================
// EVENT LISTENERS
// ============================================

// Navigation buttons
btnShowDoctors.addEventListener('click', () => showSection('doctors'));
btnShowAppointments.addEventListener('click', () => showSection('appointments'));
btnShowLogin.addEventListener('click', () => showSection('auth'));
btnShowRegister.addEventListener('click', () => showSection('register'));
btnLogout.addEventListener('click', handleLogout);
btnCancelBooking.addEventListener('click', () => showSection('doctors'));

// Auth form links
document.getElementById('link-to-register').addEventListener('click', (e) => {
    e.preventDefault();
    showSection('register');
});

document.getElementById('link-to-login').addEventListener('click', (e) => {
    e.preventDefault();
    showSection('auth');
});

// Form submissions
formLogin.addEventListener('submit', handleLogin);
formRegister.addEventListener('submit', handleRegister);
formBooking.addEventListener('submit', handleBooking);

// ============================================
// INITIALIZATION
// ============================================

/**
 * Initialize the application on page load.
 */
function init() {
    checkExistingSession();
    updateUIForAuth();
    showSection('doctors');
}

// Run initialization when DOM is loaded
document.addEventListener('DOMContentLoaded', init);
