// Dashboard Data Loader - Loads real user data into the beautiful dashboard UI
let practitionerId;
let routines = [];
let statistics = null;
let completedRoutineIds = new Set();

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
        // Load statistics and routines in parallel (using secureFetch for session auth)
        const [statsResponse, routinesResponse] = await Promise.all([
            secureFetch(`/api/analytics/practitioners/${practitionerId}/statistics`),
            secureFetch(`/api/routines/active?practitionerId=${practitionerId}`)
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

async function updateRoutinesList() {
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

    // Fetch which routines are completed for the selected date
    const routineIds = routines.map(r => r.id).join(',');
    const dateStr = getSelectedDateString();

    try {
        const response = await secureFetch(`/api/entries/completed-routines?routineIds=${routineIds}&date=${dateStr}`);
        if (response.ok) {
            const completedIds = await response.json();
            completedRoutineIds = new Set(completedIds);
        }
    } catch (error) {
        console.error('Error fetching completed routines:', error);
        completedRoutineIds = new Set();
    }

    routinesContainer.innerHTML = routines.map((routine, index) => createRoutineCard(routine, index)).join('');
}

function createRoutineCard(routine, index) {
    const daysElapsed = Math.floor((new Date() - new Date(routine.startDate)) / (1000 * 60 * 60 * 24));
    const targetDays = routine.targetDays || 91;
    const progressPercent = Math.min(((routine.totalCompletions / targetDays) * 100), 100).toFixed(0);
    const daysRemaining = targetDays - routine.totalCompletions;

    // Check if completed on the selected date using the fetched completedRoutineIds
    const selectedDateStr = getSelectedDateString();
    const completedOnSelectedDate = completedRoutineIds.has(routine.id);

    // Check if viewing today
    const isViewingToday = selectedDateStr === toLocalDateString(today);

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
        <div class="bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 p-4 md:p-6 border-l-4 ${borderColor}">
            <div class="flex flex-col md:flex-row md:items-start md:justify-between gap-4 md:gap-6">
                <div class="flex-1">
                    <div class="flex items-center gap-3 mb-3">
                        <h3 class="text-lg md:text-xl font-bold text-gray-900">${routine.habitName}</h3>
                        ${badgeStatus}
                    </div>
                    <p class="text-gray-600 mb-4 md:mb-5 text-sm md:text-base">${formatRecurrence(routine.recurrenceType, routine.specificDays)}</p>

                    <!-- Progress Bar -->
                    <div class="mb-4">
                        <div class="flex justify-between text-sm mb-2">
                            <span class="text-gray-700 font-medium">Progress</span>
                            <span class="text-gray-900 font-bold">${routine.totalCompletions}/${targetDays} days (${progressPercent}%)</span>
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
                <div class="routine-buttons">
                    ${completedOnSelectedDate ? `
                        <button onclick="uncompleteEntry('${routine.id}', this)"
                                class="text-white text-base px-6 py-3 whitespace-nowrap rounded-lg shadow-md font-semibold transition-colors duration-200"
                                style="background: linear-gradient(to right, #059669, #047857);"
                                title="Click to unmark"
                                id="complete-btn-${routine.id}">
                            ✓ ${isViewingToday ? 'Done Today!' : 'Done!'}
                        </button>
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
