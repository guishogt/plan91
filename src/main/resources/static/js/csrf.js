// CSRF Token handling for secure API calls

/**
 * Get CSRF token from cookie
 */
function getCsrfToken() {
    const name = 'XSRF-TOKEN=';
    const cookies = document.cookie.split(';');
    for (let cookie of cookies) {
        cookie = cookie.trim();
        if (cookie.startsWith(name)) {
            return decodeURIComponent(cookie.substring(name.length));
        }
    }
    return null;
}

/**
 * Secure fetch wrapper that includes CSRF token
 */
async function secureFetch(url, options = {}) {
    const csrfToken = getCsrfToken();

    const headers = {
        ...options.headers
    };

    // Add CSRF token for state-changing requests
    if (csrfToken && options.method && ['POST', 'PUT', 'DELETE', 'PATCH'].includes(options.method.toUpperCase())) {
        headers['X-XSRF-TOKEN'] = csrfToken;
    }

    // Add Content-Type for JSON if body is present and not FormData
    if (options.body && !(options.body instanceof FormData)) {
        headers['Content-Type'] = headers['Content-Type'] || 'application/json';
    }

    const response = await fetch(url, {
        ...options,
        headers,
        credentials: 'same-origin' // Include session cookies
    });

    // If unauthorized, redirect to login
    if (response.status === 401 || response.status === 403) {
        window.location.href = '/login';
        throw new Error('Session expired. Please log in again.');
    }

    return response;
}
