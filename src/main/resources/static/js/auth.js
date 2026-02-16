/**
 * Authentication helper for Plan 91.
 * Fetches user info from the server and stores in localStorage.
 */

// Fetch current user info from server
async function fetchCurrentUser() {
    try {
        const response = await fetch('/api/me', {
            credentials: 'same-origin'
        });

        if (response.status === 401 || response.status === 403) {
            // Not logged in - redirect to login
            console.log('Not authenticated, redirecting to login...');
            window.location.href = '/login';
            return null;
        }

        if (response.status === 404) {
            // User exists but no practitioner - this shouldn't happen normally
            console.error('Practitioner not found for logged-in user');
            return null;
        }

        if (!response.ok) {
            console.error('Failed to fetch user info:', response.status);
            return null;
        }

        const user = await response.json();

        // Store in localStorage for quick access
        localStorage.setItem('practitionerId', user.practitionerId);
        localStorage.setItem('userEmail', user.email);
        localStorage.setItem('userName', user.firstName + ' ' + user.lastName);

        console.log('User info loaded:', user.firstName, user.lastName);
        return user;
    } catch (error) {
        console.error('Error fetching user info:', error);
        return null;
    }
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
    window.location.href = '/logout';
}

// Initialize on page load
(async function() {
    console.log('Plan 91 Auth Helper loading...');

    // Always fetch fresh user info from server
    const user = await fetchCurrentUser();

    if (user) {
        console.log('Authenticated as:', user.email);
        console.log('Practitioner ID:', user.practitionerId);
    }
})();
