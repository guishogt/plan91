// Dashboard Data Loader - Loads real user data into the beautiful dashboard UI
let practitionerId;
let routines = [];
let abandonedRoutines = [];
let archivedRoutines = [];
let statistics = null;
let completedRoutineIds = new Set();
let skippedRoutineIds = new Set();
let skippedNotes = {}; // routineId -> note

// Date navigation - allow viewing up to 5 days back
let selectedDate = new Date();
selectedDate.setHours(0, 0, 0, 0); // Normalize to midnight
const today = new Date();
today.setHours(0, 0, 0, 0); // Normalize to midnight
const maxDaysBack = 5;

// Helper to get local date string (YYYY-MM-DD) without timezone issues
function toLocalDateString(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', async () => {
    practitionerId = getCurrentPractitionerId();
    if (!practitionerId) {
        console.warn('Practitioner ID not found');
        return;
    }

    updateDateDisplay();
    await loadDashboardData();
});

// Date navigation functions
function goToPreviousDay() {
    const minDate = new Date(today);
    minDate.setDate(minDate.getDate() - maxDaysBack);

    if (selectedDate > minDate) {
        selectedDate.setDate(selectedDate.getDate() - 1);
        updateDateDisplay();
        updateRoutinesList();
    }
}

function goToNextDay() {
    if (selectedDate < today) {
        selectedDate.setDate(selectedDate.getDate() + 1);
        updateDateDisplay();
        updateRoutinesList();
    }
}

function updateDateDisplay() {
    const dateDisplay = document.getElementById('selectedDateDisplay');
    const dateFull = document.getElementById('selectedDateFull');
    const prevBtn = document.getElementById('prevDayBtn');
    const nextBtn = document.getElementById('nextDayBtn');

    if (!dateDisplay) return;

    const todayStr = toLocalDateString(today);
    const selectedStr = toLocalDateString(selectedDate);

    // Calculate days difference (dates are normalized to midnight)
    const diffDays = Math.round((today - selectedDate) / (1000 * 60 * 60 * 24));

    if (diffDays === 0) {
        dateDisplay.textContent = 'Today';
    } else if (diffDays === 1) {
        dateDisplay.textContent = 'Yesterday';
    } else {
        dateDisplay.textContent = `${diffDays} days ago`;
    }

    // Show full date
    const options = { weekday: 'long', month: 'long', day: 'numeric' };
    dateFull.textContent = selectedDate.toLocaleDateString('en-US', options);

    // Update button states
    const minDate = new Date(today);
    minDate.setDate(minDate.getDate() - maxDaysBack);

    if (prevBtn) prevBtn.disabled = selectedDate <= minDate;
    if (nextBtn) nextBtn.disabled = selectedDate >= today;
}

function getSelectedDateString() {
    return toLocalDateString(selectedDate);
}

async function loadDashboardData() {
    try {
        // Load statistics, active routines, and abandoned routines in parallel
        const [statsResponse, routinesResponse, abandonedResponse, archivedResponse] = await Promise.all([
            secureFetch(`/api/analytics/practitioners/${practitionerId}/statistics`),
            secureFetch(`/api/routines/active?practitionerId=${practitionerId}`),
            secureFetch(`/api/routines/status/ABANDONED?practitionerId=${practitionerId}`),
            secureFetch(`/api/routines/status/ARCHIVED?practitionerId=${practitionerId}`)
        ]);

        if (statsResponse.ok && routinesResponse.ok) {
            statistics = await statsResponse.json();
            routines = await routinesResponse.json();
            abandonedRoutines = abandonedResponse.ok ? await abandonedResponse.json() : [];
            archivedRoutines = archivedResponse.ok ? await archivedResponse.json() : [];

            updateStatsCards();
            updateRoutinesList();
        }
    } catch (error) {
        console.error('Error loading dashboard data:', error);
    }
}

function updateStatsCards() {
    if (!statistics) return;

    // Update Active Routines card
    const activeRoutinesEl = document.querySelector('[data-stat="active-routines"]');
    if (activeRoutinesEl) {
        activeRoutinesEl.textContent = statistics.routines.active;
    }

    // Update Longest Streak card
    const longestStreakEl = document.querySelector('[data-stat="longest-streak"]');
    if (longestStreakEl) {
        longestStreakEl.innerHTML = `${statistics.streaks.allTimeLongest} <span class="text-2xl text-gray-500">days</span>`;
    }

    // Update Completed Routines card
    const completedRoutinesEl = document.querySelector('[data-stat="completed-routines"]');
    if (completedRoutinesEl) {
        completedRoutinesEl.textContent = statistics.routines.completed;
    }
}

async function updateRoutinesList() {
    const routinesContainer = document.getElementById('routines-container');
    if (!routinesContainer) return;

    if (routines.length === 0) {
        routinesContainer.innerHTML = `
            <div class="bg-white rounded-xl shadow-md p-12 text-center">
                <svg class="mx-auto h-16 w-16 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
                </svg>
                <h3 class="text-xl font-medium text-gray-900 mb-2">No active routines or trackers</h3>
                <p class="text-gray-600 mb-6">Start a 91-day routine or a simple tracker to begin.</p>
                <button onclick="openNewRoutineModal()" class="btn-primary inline-flex items-center">
                    <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                    </svg>
                    Start New Routine
                </button>
            </div>
        `;
        return;
    }

    // Fetch status of all routines for the selected date
    const routineIds = routines.map(r => r.id).join(',');
    const dateStr = getSelectedDateString();

    try {
        const response = await secureFetch(`/api/entries/status?routineIds=${routineIds}&date=${dateStr}`);
        if (response.ok) {
            const statusMap = await response.json();
            completedRoutineIds = new Set();
            skippedRoutineIds = new Set();
            skippedNotes = {};

            for (const [routineId, status] of Object.entries(statusMap)) {
                if (status.completed) {
                    completedRoutineIds.add(routineId);
                } else {
                    skippedRoutineIds.add(routineId);
                    if (status.notes) {
                        skippedNotes[routineId] = status.notes;
                    }
                }
            }
        }
    } catch (error) {
        console.error('Error fetching routine status:', error);
        completedRoutineIds = new Set();
        skippedRoutineIds = new Set();
        skippedNotes = {};
    }

    let html = routines.map((routine, index) => createRoutineCard(routine, index)).join('');

    // Show abandoned routines in a dimmed section
    if (abandonedRoutines.length > 0) {
        html += `
            <div class="mt-8 mb-4">
                <h3 class="text-sm font-semibold text-gray-400 uppercase tracking-wider">Broken Routines</h3>
            </div>
        `;
        html += abandonedRoutines.map((routine, index) => createAbandonedRoutineCard(routine, index)).join('');
    }

    // Show archived routines
    if (archivedRoutines.length > 0) {
        html += `
            <div class="mt-8 mb-4">
                <h3 class="text-sm font-semibold text-gray-400 uppercase tracking-wider">Archived</h3>
            </div>
        `;
        html += archivedRoutines.map((routine, index) => createArchivedRoutineCard(routine, index)).join('');
    }

    routinesContainer.innerHTML = html;
}

function createRoutineCard(routine, index) {
    const isTracker = routine.routineType === 'TRACKER';
    const daysElapsed = Math.floor((new Date() - new Date(routine.startDate)) / (1000 * 60 * 60 * 24));
    const targetDays = routine.targetDays || 91;
    const progressPercent = isTracker ? 0 : Math.min(((routine.totalCompletions / targetDays) * 100), 100).toFixed(0);

    // Check if completed or skipped on the selected date
    const selectedDateStr = getSelectedDateString();
    const completedOnSelectedDate = completedRoutineIds.has(routine.id);
    const skippedOnSelectedDate = skippedRoutineIds.has(routine.id);
    const skipNote = skippedNotes[routine.id] || '';

    // Check if viewing today
    const isViewingToday = selectedDateStr === toLocalDateString(today);

    // Rotate colors for variety (trackers use different palette)
    const borderColors = isTracker
        ? ['border-blue-500', 'border-indigo-500', 'border-cyan-500', 'border-teal-500']
        : ['border-primary-500', 'border-warning-500', 'border-success-500', 'border-purple-500'];
    const gradientColors = isTracker
        ? ['from-blue-400 to-blue-600', 'from-indigo-400 to-indigo-600', 'from-cyan-400 to-cyan-600', 'from-teal-400 to-teal-600']
        : ['from-primary-500 to-primary-600', 'from-warning-400 to-warning-600', 'from-success-400 to-success-600', 'from-purple-400 to-purple-600'];
    const borderColor = borderColors[index % borderColors.length];
    const gradientColor = gradientColors[index % gradientColors.length];

    // Strike warning only applies to ROUTINE type
    const strikeWarning = (!isTracker && routine.hasUsedStrike) ? `
        <div class="bg-warning-50 border border-warning-200 rounded-lg p-3 mb-3">
            <p class="text-sm text-warning-800 font-medium">⚠️ Strike used - No more misses allowed!</p>
        </div>
    ` : '';

    // Badge: Tracker shows "Tracker" badge, Routine shows Active/Strike Used
    let badgeStatus;
    if (isTracker) {
        badgeStatus = '<span class="bg-blue-100 text-blue-800 px-2 py-1 rounded-full text-xs font-semibold">📊 Tracker</span>';
    } else if (routine.hasUsedStrike) {
        badgeStatus = '<span class="badge-warning text-xs font-semibold">⚠️ Strike Used</span>';
    } else {
        badgeStatus = '<span class="badge-success text-xs">Active</span>';
    }

    return `
        <div class="bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 p-4 md:p-6 border-l-4 ${borderColor}">
            <div class="flex flex-col md:flex-row md:items-start md:justify-between gap-4 md:gap-6">
                <div class="flex-1">
                    <div class="flex items-center gap-3 mb-3">
                        <h3 class="text-lg md:text-xl font-bold text-gray-900">${routine.habitName}</h3>
                        ${badgeStatus}
                    </div>
                    <p class="text-gray-600 mb-4 md:mb-5 text-sm md:text-base">${formatRecurrence(routine.recurrenceType, routine.specificDays)}</p>

                    <!-- Progress Bar (different for Trackers) -->
                    ${isTracker ? `
                    <div class="mb-4">
                        <div class="flex justify-between text-sm mb-2">
                            <span class="text-gray-700 font-medium">Total completions</span>
                            <span class="text-gray-900 font-bold">${routine.totalCompletions} days</span>
                        </div>
                    </div>
                    ` : `
                    <div class="mb-4">
                        <div class="flex justify-between text-sm mb-2">
                            <span class="text-gray-700 font-medium">Progress</span>
                            <div class="text-right">
                                ${routine.expectedEndDate ? `<p class="text-xs text-gray-500">${formatEndDate(routine.expectedEndDate)}</p>` : ''}
                                <span class="text-gray-900 font-bold">${routine.totalCompletions}/${targetDays} days (${progressPercent}%)</span>
                            </div>
                        </div>
                        <div class="w-full bg-gray-200 rounded-full h-3 overflow-hidden">
                            <div class="bg-gradient-to-r ${gradientColor} h-3 rounded-full shadow-sm transition-all duration-500" style="width: ${progressPercent}%"></div>
                        </div>
                    </div>
                    `}

                    ${strikeWarning}

                    <div class="flex items-center gap-6 text-sm">
                        <span class="flex items-center gap-2 text-gray-700">
                            <span class="text-xl">🔥</span>
                            <span class="font-medium">Current: ${routine.currentStreak} days</span>
                        </span>
                        <span class="flex items-center gap-2 text-gray-700">
                            <span class="text-xl">⭐</span>
                            <span class="font-medium">Best: ${routine.longestStreak} days</span>
                        </span>
                    </div>
                </div>
                <div class="flex flex-col items-center gap-2 flex-shrink-0">
                    ${completedOnSelectedDate ? `
                        <button onclick="uncompleteEntry('${routine.id}', this)"
                                class="text-white text-base px-6 py-3 whitespace-nowrap rounded-lg shadow-md font-semibold transition-colors duration-200"
                                style="background: linear-gradient(to right, #059669, #047857);"
                                title="Click to unmark"
                                id="complete-btn-${routine.id}">
                            ✓ ${isViewingToday ? 'Done Today!' : 'Done!'}
                        </button>
                    ` : skippedOnSelectedDate ? `
                        <div class="text-center">
                            <div class="text-sm text-gray-500 italic mb-1 ${skipNote ? 'cursor-help' : ''}" ${skipNote ? `title="${escapeHtml(skipNote)}"` : ''}>
                                Skipped${skipNote ? ' (hover for note)' : ''}
                            </div>
                            <button onclick="openCompleteEntryModal('${routine.id}', '${escapeHtml(routine.habitName)}', '${routine.trackingType || 'BOOLEAN'}', '${escapeHtml(routine.numericUnit || '')}', this)"
                                    class="text-sm px-4 py-2 whitespace-nowrap rounded-lg font-medium transition-colors duration-200"
                                    style="background-color: #10b981; color: white;"
                                    onmouseover="this.style.backgroundColor='#059669'"
                                    onmouseout="this.style.backgroundColor='#10b981'"
                                    id="complete-btn-${routine.id}">
                                ✓ Did it after all
                            </button>
                        </div>
                    ` : `
                        <button onclick="openCompleteEntryModal('${routine.id}', '${escapeHtml(routine.habitName)}', '${routine.trackingType || 'BOOLEAN'}', '${escapeHtml(routine.numericUnit || '')}', this)"
                                class="text-base px-6 py-3 whitespace-nowrap rounded-lg font-semibold transition-colors duration-200"
                                style="background-color: #10b981; color: white;"
                                onmouseover="this.style.backgroundColor='#059669'"
                                onmouseout="this.style.backgroundColor='#10b981'"
                                id="complete-btn-${routine.id}">
                            ✓ Mark Complete
                        </button>
                    `}
                    <!-- Icon row -->
                    <div class="flex items-center gap-3">
                        ${!completedOnSelectedDate && !skippedOnSelectedDate ? `
                        <button onclick="openSkipModal('${routine.id}', '${escapeHtml(routine.habitName)}')"
                                class="text-gray-400 hover:text-red-500 p-1 transition-colors"
                                title="Didn't do it"
                                id="skip-btn-${routine.id}">
                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636"></path>
                            </svg>
                        </button>
                        ` : ''}
                        <a href="/routines/detail?id=${routine.id}"
                           class="text-gray-400 hover:text-blue-600 p-1 transition-colors"
                           title="View Details">
                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path>
                            </svg>
                        </a>
                        <button onclick="openEditRoutineModal('${routine.id}', '${escapeHtml(routine.habitName)}', '${routine.recurrenceType}', '${routine.startDate}', ${routine.targetDays || 'null'}, '${routine.routineType || 'ROUTINE'}')"
                                class="text-gray-400 hover:text-gray-600 p-1 transition-colors"
                                title="Edit routine">
                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"></path>
                            </svg>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `;
}

function createAbandonedRoutineCard(routine, index) {
    const targetDays = routine.targetDays || 91;

    return `
        <div class="bg-white rounded-xl shadow-sm p-4 md:p-6 border-l-4 border-gray-300 opacity-60 hover:opacity-100 transition-opacity duration-300">
            <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
                <div class="flex-1">
                    <div class="flex items-center gap-3 mb-2">
                        <h3 class="text-lg font-bold text-gray-500">${routine.habitName}</h3>
                        <span class="bg-red-100 text-red-700 px-2 py-0.5 rounded-full text-xs font-semibold">Abandoned</span>
                    </div>
                    <p class="text-gray-400 text-sm mb-2">${formatRecurrence(routine.recurrenceType, routine.specificDays)}</p>
                    <div class="flex items-center gap-4 text-sm text-gray-400">
                        <span>Total: ${routine.totalCompletions}/${targetDays} days</span>
                        <span>Best streak: ${routine.longestStreak} days</span>
                    </div>
                </div>
                <div class="flex items-center gap-3 flex-shrink-0">
                    <button onclick="restartRoutine('${routine.id}')"
                            class="px-4 py-2 rounded-lg font-semibold text-sm transition-colors duration-200 border-2 border-green-500 text-green-600 hover:bg-green-500 hover:text-white">
                        ↻ Restart
                    </button>
                    <a href="/routines/detail?id=${routine.id}"
                       class="text-gray-400 hover:text-blue-600 p-1 transition-colors"
                       title="View Details">
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path>
                        </svg>
                    </a>
                </div>
            </div>
        </div>
    `;
}

function createArchivedRoutineCard(routine, index) {
    const targetDays = routine.targetDays || 91;

    return `
        <div class="bg-white rounded-xl shadow-sm p-4 md:p-6 border-l-4 border-gray-200 opacity-50 hover:opacity-100 transition-opacity duration-300">
            <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
                <div class="flex-1">
                    <div class="flex items-center gap-3 mb-2">
                        <h3 class="text-lg font-bold text-gray-400">${routine.habitName}</h3>
                        <span class="bg-gray-100 text-gray-500 px-2 py-0.5 rounded-full text-xs font-semibold">Archived</span>
                    </div>
                    <p class="text-gray-400 text-sm mb-2">${formatRecurrence(routine.recurrenceType, routine.specificDays)}</p>
                    <div class="flex items-center gap-4 text-sm text-gray-400">
                        <span>Total: ${routine.totalCompletions}/${targetDays} days</span>
                        <span>Best streak: ${routine.longestStreak} days</span>
                    </div>
                </div>
                <div class="flex items-center gap-3 flex-shrink-0">
                    <button onclick="restartRoutine('${routine.id}')"
                            class="px-4 py-2 rounded-lg font-semibold text-sm transition-colors duration-200 border-2 border-green-500 text-green-600 hover:bg-green-500 hover:text-white">
                        ↻ Restart
                    </button>
                    <a href="/routines/detail?id=${routine.id}"
                       class="text-gray-400 hover:text-blue-600 p-1 transition-colors"
                       title="View Details">
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path>
                        </svg>
                    </a>
                </div>
            </div>
        </div>
    `;
}

async function restartRoutine(routineId) {
    if (!confirm('Restart this routine? It will begin a new cycle from today, keeping your history.')) {
        return;
    }

    try {
        const response = await secureFetch(`/api/routines/${routineId}/restart`, {
            method: 'POST'
        });

        if (response.ok) {
            await loadDashboardData();
        } else {
            const errorData = await response.json();
            alert(errorData.message || 'Failed to restart routine');
        }
    } catch (error) {
        alert('Network error: ' + error.message);
    }
}

function formatRecurrence(type, specificDays) {
    switch (type) {
        case 'DAILY':
            return 'Every day';
        case 'WEEKDAYS':
            return 'Monday - Friday';
        case 'WEEKENDS':
            return 'Saturday - Sunday';
        case 'SPECIFIC_DAYS':
            return specificDays ? specificDays.join(', ') : 'Custom schedule';
        default:
            return type;
    }
}

function formatEndDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString + 'T00:00:00'); // Parse as local date
    const currentYear = new Date().getFullYear();
    const dateYear = date.getFullYear();

    const options = { month: 'short', day: 'numeric' };
    if (dateYear !== currentYear) {
        options.year = 'numeric';
    }
    return date.toLocaleDateString('en-US', options);
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Uncomplete an entry (toggle off)
async function uncompleteEntry(routineId, buttonElement) {
    const dateStr = getSelectedDateString();

    try {
        const response = await secureFetch(`/api/entries?routineId=${routineId}&date=${dateStr}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            // Update button to show uncompleted state
            buttonElement.innerHTML = '✓ Mark Complete';
            buttonElement.style.background = '#10b981';
            buttonElement.onclick = function() {
                openCompleteEntryModal(routineId, '', 'BOOLEAN', '', this);
            };

            // Refresh the list to get proper state
            await loadDashboardData();
        } else {
            console.error('Failed to uncomplete entry');
        }
    } catch (error) {
        console.error('Error uncompleting entry:', error);
    }
}

// Store reference to the button being clicked
let currentCompleteButton = null;

// Modal functions
function openCompleteEntryModal(routineId, habitName, trackingType, numericUnit, buttonElement) {
    // Store button reference for later update
    currentCompleteButton = buttonElement;

    // Create modal if it doesn't exist
    let modal = document.getElementById('completeEntryModal');
    if (!modal) {
        createCompleteEntryModal();
        modal = document.getElementById('completeEntryModal');
    }

    document.getElementById('selectedRoutineId').value = routineId;
    document.getElementById('modalHabitName').textContent = habitName;
    document.getElementById('selectedTrackingType').value = trackingType;

    // Show/hide numeric input based on tracking type
    const numericInput = document.getElementById('numericValueInput');
    if (trackingType === 'NUMERIC') {
        numericInput.classList.remove('hidden');
        const label = document.getElementById('numericLabel');
        label.textContent = numericUnit ? `How many ${numericUnit}?` : 'Value';
        document.getElementById('entryValue').focus();
    } else {
        numericInput.classList.add('hidden');
    }

    modal.classList.remove('hidden');
}

function closeCompleteEntryModal() {
    const modal = document.getElementById('completeEntryModal');
    if (modal) {
        modal.classList.add('hidden');
        document.getElementById('completeEntryForm').reset();
        const errorDiv = document.getElementById('modalErrorMessage');
        if (errorDiv) errorDiv.classList.add('hidden');

        // Reset notes visibility
        const notesContainer = document.getElementById('notesContainer');
        const notesToggle = document.getElementById('notesToggleButton');
        if (notesContainer) notesContainer.classList.add('hidden');
        if (notesToggle) notesToggle.classList.remove('hidden');
    }
}

function toggleNotesField() {
    const notesContainer = document.getElementById('notesContainer');
    const notesToggle = document.getElementById('notesToggleButton');

    if (notesContainer && notesToggle) {
        notesContainer.classList.remove('hidden');
        notesToggle.classList.add('hidden');

        // Focus on the textarea
        const textarea = document.getElementById('entryNotes');
        if (textarea) textarea.focus();
    }
}

function createCompleteEntryModal() {
    const modalHtml = `
        <div id="completeEntryModal" class="hidden fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" onclick="if(event.target === this) closeCompleteEntryModal()">
            <div class="bg-white rounded-2xl shadow-xl max-w-sm w-full mx-4">
                <div class="p-6">
                    <!-- Close button -->
                    <button onclick="closeCompleteEntryModal()" class="absolute top-3 right-3 text-gray-400 hover:text-gray-600">
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                        </svg>
                    </button>

                    <!-- Habit name as subtle header -->
                    <p class="text-center text-gray-500 text-sm mb-4" id="modalHabitName"></p>

                    <form id="completeEntryForm">
                        <input type="hidden" id="selectedRoutineId">
                        <input type="hidden" id="selectedTrackingType">

                        <!-- Numeric input (shown for NUMERIC habits) -->
                        <div id="numericValueInput" class="hidden mb-4">
                            <label for="entryValue" class="block text-sm font-medium text-gray-700 mb-2 text-center">
                                <span id="numericLabel">Value</span>
                            </label>
                            <input type="number" id="entryValue" name="value"
                                   class="w-full px-4 py-3 text-center text-2xl border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                                   placeholder="0">
                        </div>

                        <!-- Big centered Mark Complete button -->
                        <button type="submit" class="w-full py-4 text-white rounded-xl font-semibold text-lg mb-4"
                                style="background-color: #10b981;"
                                onmouseover="this.style.backgroundColor='#059669'"
                                onmouseout="this.style.backgroundColor='#10b981'">
                            ✓ Mark Complete
                        </button>

                        <!-- Notes toggle -->
                        <div id="notesToggleButton" class="text-center">
                            <button type="button" onclick="toggleNotesField()"
                                    class="text-blue-600 hover:text-blue-800 text-sm font-medium inline-flex items-center gap-1">
                                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                                </svg>
                                Want to add a note?
                            </button>
                        </div>

                        <div id="notesContainer" class="hidden mt-4">
                            <textarea id="entryNotes" name="notes" rows="2"
                                      class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                                      placeholder="How did it go?"></textarea>
                        </div>

                        <div id="modalErrorMessage" class="hidden mt-4 p-3 bg-red-50 border border-red-200 rounded-lg text-center">
                            <p class="text-red-800 text-sm"></p>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', modalHtml);

    // Add form submit handler
    document.getElementById('completeEntryForm').addEventListener('submit', async function(e) {
        e.preventDefault();

        const formData = new FormData(e.target);
        const trackingType = document.getElementById('selectedTrackingType').value;

        const data = {
            routineId: document.getElementById('selectedRoutineId').value,
            date: getSelectedDateString(),
            value: trackingType === 'NUMERIC' ? parseInt(formData.get('value')) : null,
            notes: formData.get('notes') || null
        };

        try {
            const response = await secureFetch('/api/entries', {
                method: 'POST',
                body: JSON.stringify(data)
            });

            if (response.ok) {
                // Update the button appearance
                if (currentCompleteButton) {
                    currentCompleteButton.innerHTML = '✓ Done!';
                    currentCompleteButton.className = 'text-white text-base px-6 py-3 whitespace-nowrap rounded-lg shadow-md font-semibold opacity-90 cursor-not-allowed';
                    currentCompleteButton.style.background = 'linear-gradient(to right, #059669, #047857)';
                    currentCompleteButton.disabled = true;
                    // Keep button in done state - no reset needed
                }

                closeCompleteEntryModal();
                // Reload dashboard data
                await loadDashboardData();
            } else {
                const errorData = await response.json();
                let errorMessage = errorData.message || 'Failed to complete entry';

                // Show friendly message for already completed
                if (errorMessage.includes('Already completed')) {
                    errorMessage = '✓ Great! You already marked this complete for this day!';
                }

                showModalError(errorMessage);
            }
        } catch (error) {
            showModalError('Network error: ' + error.message);
        }
    });
}

function showModalError(message) {
    const errorDiv = document.getElementById('modalErrorMessage');
    if (errorDiv) {
        errorDiv.querySelector('p').textContent = message;
        errorDiv.classList.remove('hidden');
    }
}

// Skip modal functions
function openSkipModal(routineId, habitName) {
    // Create modal if it doesn't exist
    let modal = document.getElementById('skipEntryModal');
    if (!modal) {
        createSkipModal();
        modal = document.getElementById('skipEntryModal');
    }

    document.getElementById('skipRoutineId').value = routineId;
    document.getElementById('skipModalHabitName').textContent = habitName;
    document.getElementById('skipNotes').value = '';

    modal.classList.remove('hidden');
    document.getElementById('skipNotes').focus();
}

function closeSkipModal() {
    const modal = document.getElementById('skipEntryModal');
    if (modal) {
        modal.classList.add('hidden');
        document.getElementById('skipForm').reset();
        const errorDiv = document.getElementById('skipModalError');
        if (errorDiv) errorDiv.classList.add('hidden');
    }
}

function createSkipModal() {
    const modalHtml = `
        <div id="skipEntryModal" class="hidden fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" onclick="if(event.target === this) closeSkipModal()">
            <div class="bg-white rounded-2xl shadow-xl max-w-sm w-full mx-4">
                <div class="p-6">
                    <!-- Header -->
                    <div class="text-center mb-4">
                        <div class="inline-flex items-center justify-center w-12 h-12 bg-red-100 rounded-full mb-3">
                            <svg class="w-6 h-6 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                            </svg>
                        </div>
                        <p class="text-gray-500 text-sm" id="skipModalHabitName"></p>
                    </div>

                    <form id="skipForm">
                        <input type="hidden" id="skipRoutineId">

                        <!-- Notes input -->
                        <div class="mb-4">
                            <label for="skipNotes" class="block text-sm font-medium text-gray-700 mb-2">
                                Why couldn't you do it? <span class="text-gray-400">(optional)</span>
                            </label>
                            <textarea id="skipNotes" name="notes" rows="3"
                                      class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                                      placeholder="Too busy, feeling sick, etc."></textarea>
                        </div>

                        <!-- Submit button -->
                        <button type="submit" class="w-full py-3 text-white rounded-xl font-semibold transition-colors duration-200"
                                style="background-color: #dc2626;"
                                onmouseover="this.style.backgroundColor='#b91c1c'"
                                onmouseout="this.style.backgroundColor='#dc2626'">
                            Record Skip
                        </button>

                        <!-- Cancel link -->
                        <button type="button" onclick="closeSkipModal()"
                                class="w-full mt-2 py-2 text-gray-500 hover:text-gray-700 text-sm font-medium">
                            Cancel
                        </button>

                        <div id="skipModalError" class="hidden mt-4 p-3 bg-red-50 border border-red-200 rounded-lg text-center">
                            <p class="text-red-800 text-sm"></p>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', modalHtml);

    // Add form submit handler
    document.getElementById('skipForm').addEventListener('submit', async function(e) {
        e.preventDefault();

        const routineId = document.getElementById('skipRoutineId').value;
        const notes = document.getElementById('skipNotes').value;

        const data = {
            routineId: routineId,
            date: getSelectedDateString(),
            notes: notes || null
        };

        try {
            const response = await secureFetch('/api/entries/skip', {
                method: 'POST',
                body: JSON.stringify(data)
            });

            if (response.ok) {
                closeSkipModal();
                // Reload dashboard data to show updated state
                await loadDashboardData();
            } else {
                const errorData = await response.json();
                showSkipModalError(errorData.message || 'Failed to record skip');
            }
        } catch (error) {
            showSkipModalError('Network error: ' + error.message);
        }
    });
}

function showSkipModalError(message) {
    const errorDiv = document.getElementById('skipModalError');
    if (errorDiv) {
        errorDiv.querySelector('p').textContent = message;
        errorDiv.classList.remove('hidden');
    }
}
