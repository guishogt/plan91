/**
 * Simple authentication helper for Plan 91.
 *
 * For development, we use localStorage to simulate authentication.
 * TODO: Replace with proper authentication in production.
 */

// Available test users (real IDs from database)
const TEST_USERS = {
    demo: {
        id: '83a5a779-ffd0-11f0-bfe5-4aaad1f3cde9',
        email: 'demo@plan91.com',
        name: 'Demo User'
    },
    luis: {
        id: '8fbdd1fe-ffe8-11f0-bfe5-4aaad1f3cde9',
        email: 'luis@fernandezgt.com',
        name: 'Luis Fernandez'
    },
    luisShort: {
        id: 'a4dcf0f6-fff9-11f0-bfe5-4aaad1f3cde9',
        email: 'l@l.com',
        name: 'Luis'
    }
};

// Fix old invalid practitioner ID
const currentId = localStorage.getItem('practitionerId');
const validIds = [TEST_USERS.demo.id, TEST_USERS.luis.id, TEST_USERS.luisShort.id];
if (!currentId || !validIds.includes(currentId)) {
    console.log('Fixing invalid practitioner ID...');
    localStorage.setItem('practitionerId', TEST_USERS.luisShort.id);
    localStorage.setItem('userEmail', TEST_USERS.luisShort.email);
    localStorage.setItem('userName', TEST_USERS.luisShort.name);
}

// Helper function to get current practitioner ID
function getCurrentPractitionerId() {
    return localStorage.getItem('practitionerId');
}

// Helper function to check if user is logged in
function isLoggedIn() {
    return !!localStorage.getItem('practitionerId');
}

// Helper function to logout
function logout() {
    localStorage.removeItem('practitionerId');
    localStorage.removeItem('userEmail');
    localStorage.removeItem('userName');
    window.location.href = '/login';
}

// Helper function to switch user (for testing)
function switchUser(userId, email, name) {
    localStorage.setItem('practitionerId', userId);
    localStorage.setItem('userEmail', email);
    localStorage.setItem('userName', name);
    window.location.reload();
}

console.log('Plan 91 Auth Helper loaded');
console.log('Current user:', localStorage.getItem('userName'), '(' + localStorage.getItem('userEmail') + ')');
console.log('Practitioner ID:', localStorage.getItem('practitionerId'));
console.log('\nTo switch users, use: switchUser(TEST_USERS.demo.id, TEST_USERS.demo.email, TEST_USERS.demo.name)');
