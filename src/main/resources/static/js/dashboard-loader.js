// Dashboard Data Loader - Loads real user data into the beautiful dashboard UI
let practitionerId;
let routines = [];
let statistics = null;

// Initialize on page load
document.addEventListener('DOMContentLoaded', async () => {
    practitionerId = getCurrentPractitionerId();
    if (!practitionerId) {
        console.warn('Practitioner ID not found');
        return;
    }

    await loadDashboardData();
});

async function loadDashboardData() {
    try {
        // Load statistics and routines in parallel
        const [statsResponse, routinesResponse] = await Promise.all([
            fetch(`/api/analytics/practitioners/${practitionerId}/statistics`),
            fetch(`/api/routines/active?practitionerId=${practitionerId}`)
        ]);

        if (statsResponse.ok && routinesResponse.ok) {
            statistics = await statsResponse.json();
            routines = await routinesResponse.json();

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

function updateRoutinesList() {
    const routinesContainer = document.getElementById('routines-container');
    if (!routinesContainer) return;

    if (routines.length === 0) {
        routinesContainer.innerHTML = `
            <div class="bg-white rounded-xl shadow-md p-12 text-center">
                <svg class="mx-auto h-16 w-16 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
                </svg>
                <h3 class="text-xl font-medium text-gray-900 mb-2">No active routines</h3>
                <p class="text-gray-600 mb-6">Start your first 91-day routine to begin tracking.</p>
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

    routinesContainer.innerHTML = routines.map((routine, index) => createRoutineCard(routine, index)).join('');
}

function createRoutineCard(routine, index) {
    const daysElapsed = Math.floor((new Date() - new Date(routine.startDate)) / (1000 * 60 * 60 * 24));
    const progressPercent = Math.min(((routine.totalCompletions / 91) * 100), 100).toFixed(0);
    const daysRemaining = 91 - routine.totalCompletions;

    // Check if completed today
    const today = new Date().toISOString().split('T')[0];
    const completedToday = routine.lastCompletionDate === today;

    // Rotate colors for variety
    const borderColors = ['border-primary-500', 'border-warning-500', 'border-success-500', 'border-purple-500'];
    const gradientColors = [
        'from-primary-500 to-primary-600',
        'from-warning-400 to-warning-600',
        'from-success-400 to-success-600',
        'from-purple-400 to-purple-600'
    ];
    const borderColor = borderColors[index % borderColors.length];
    const gradientColor = gradientColors[index % gradientColors.length];

    const strikeWarning = routine.hasUsedStrike ? `
        <div class="bg-warning-50 border border-warning-200 rounded-lg p-3 mb-3">
            <p class="text-sm text-warning-800 font-medium">⚠️ Strike used - No more misses allowed!</p>
        </div>
    ` : '';

    const badgeStatus = routine.hasUsedStrike ?
        '<span class="badge-warning text-xs font-semibold">⚠️ Strike Used</span>' :
        '<span class="badge-success text-xs">Active</span>';

    return `
        <div class="bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 p-6 border-l-4 ${borderColor}">
            <div class="flex items-start justify-between gap-6">
                <div class="flex-1">
                    <div class="flex items-center gap-3 mb-3">
                        <h3 class="text-xl font-bold text-gray-900">${routine.habitName}</h3>
                        ${badgeStatus}
                    </div>
                    <p class="text-gray-600 mb-5">${formatRecurrence(routine.recurrenceType, routine.specificDays)}</p>

                    <!-- Progress Bar -->
                    <div class="mb-4">
                        <div class="flex justify-between text-sm mb-2">
                            <span class="text-gray-700 font-medium">Progress</span>
                            <span class="text-gray-900 font-bold">${routine.totalCompletions}/91 days (${progressPercent}%)</span>
                        </div>
                        <div class="w-full bg-gray-200 rounded-full h-3 overflow-hidden">
                            <div class="bg-gradient-to-r ${gradientColor} h-3 rounded-full shadow-sm transition-all duration-500" style="width: ${progressPercent}%"></div>
                        </div>
                    </div>

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
                <div class="flex flex-col gap-2">
                    ${completedToday ? `
                        <button disabled
                                class="text-white text-base px-6 py-3 whitespace-nowrap rounded-lg shadow-md font-semibold opacity-90 cursor-not-allowed"
                                style="background: linear-gradient(to right, #059669, #047857);"
                                id="complete-btn-${routine.id}">
                            ✓ Done Today!
                        </button>
                    ` : `
                        <button onclick="openCompleteEntryModal('${routine.id}', '${escapeHtml(routine.habitName)}', '${routine.habitId}', this)"
                                class="text-base px-6 py-3 whitespace-nowrap rounded-lg font-semibold transition-colors duration-200"
                                style="background-color: #10b981; color: white;"
                                onmouseover="this.style.backgroundColor='#059669'"
                                onmouseout="this.style.backgroundColor='#10b981'"
                                id="complete-btn-${routine.id}">
                            ✓ Mark Complete
                        </button>
                    `}
                    <a href="/routines/detail?id=${routine.id}"
                       class="text-center px-6 py-3 border border-blue-600 text-blue-600 rounded-md hover:bg-blue-50 text-sm whitespace-nowrap">
                        View Details
                    </a>
                </div>
            </div>
        </div>
    `;
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

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Store reference to the button being clicked
let currentCompleteButton = null;

// Modal functions (similar to habits/dashboard.html)
function openCompleteEntryModal(routineId, habitName, habitId, buttonElement) {
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
    document.getElementById('selectedTrackingType').value = 'BOOLEAN';
    document.getElementById('numericValueInput').classList.add('hidden');

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
        <div id="completeEntryModal" class="hidden fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div class="bg-white rounded-lg shadow-xl max-w-md w-full mx-4">
                <div class="p-6">
                    <h2 class="text-2xl font-bold text-gray-900 mb-4">Complete Entry</h2>
                    <p class="text-gray-600 mb-6" id="modalHabitName"></p>

                    <form id="completeEntryForm">
                        <input type="hidden" id="selectedRoutineId">
                        <input type="hidden" id="selectedTrackingType">

                        <div class="space-y-4">
                            <div id="numericValueInput" class="hidden">
                                <label for="entryValue" class="block text-sm font-medium text-gray-700 mb-2">
                                    <span id="numericLabel">Value</span>
                                </label>
                                <input type="number" id="entryValue" name="value"
                                       class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                            </div>

                            <div id="notesToggleButton">
                                <button type="button" onclick="toggleNotesField()"
                                        class="text-blue-600 hover:text-blue-800 text-sm font-medium flex items-center gap-1">
                                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                                    </svg>
                                    Want to add a note?
                                </button>
                            </div>

                            <div id="notesContainer" class="hidden">
                                <label for="entryNotes" class="block text-sm font-medium text-gray-700 mb-2">Notes (optional)</label>
                                <textarea id="entryNotes" name="notes" rows="3"
                                          class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                          placeholder="How did it go?"></textarea>
                            </div>
                        </div>

                        <div id="modalErrorMessage" class="hidden mt-4 p-4 bg-red-50 border border-red-200 rounded-md">
                            <p class="text-red-800"></p>
                        </div>

                        <div class="mt-6 flex justify-end space-x-4">
                            <button type="button" onclick="closeCompleteEntryModal()" class="px-6 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50">
                                Cancel
                            </button>
                            <button type="submit" class="px-6 py-2 text-white rounded-md font-semibold"
                                    style="background-color: #10b981;"
                                    onmouseover="this.style.backgroundColor='#059669'"
                                    onmouseout="this.style.backgroundColor='#10b981'">
                                Mark Complete
                            </button>
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
            date: new Date().toISOString().split('T')[0],
            value: trackingType === 'NUMERIC' ? parseInt(formData.get('value')) : null,
            notes: formData.get('notes') || null
        };

        try {
            const response = await fetch('/api/entries', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                // Update the button appearance
                if (currentCompleteButton) {
                    currentCompleteButton.innerHTML = '✓ Marked! Well done!';
                    currentCompleteButton.className = 'text-white text-base px-6 py-3 whitespace-nowrap rounded-lg shadow-md font-semibold';
                    currentCompleteButton.style.background = 'linear-gradient(to right, #10b981, #059669)';
                    currentCompleteButton.disabled = true;

                    // Reset after 2 seconds
                    setTimeout(() => {
                        if (currentCompleteButton) {
                            currentCompleteButton.innerHTML = '✓ Mark Complete';
                            currentCompleteButton.className = 'text-base px-6 py-3 whitespace-nowrap rounded-lg font-semibold transition-colors duration-200';
                            currentCompleteButton.style.backgroundColor = '#10b981';
                            currentCompleteButton.style.color = 'white';
                            currentCompleteButton.onmouseover = () => currentCompleteButton.style.backgroundColor = '#059669';
                            currentCompleteButton.onmouseout = () => currentCompleteButton.style.backgroundColor = '#10b981';
                            currentCompleteButton.disabled = false;
                        }
                    }, 2000);
                }

                closeCompleteEntryModal();
                // Reload dashboard data
                await loadDashboardData();
            } else {
                const errorData = await response.json();
                let errorMessage = errorData.message || 'Failed to complete entry';

                // Show friendly message for already completed
                if (errorMessage.includes('Already completed')) {
                    errorMessage = '✓ Great! You already marked this complete today!';
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
